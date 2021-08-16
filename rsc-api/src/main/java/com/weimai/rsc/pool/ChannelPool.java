package com.weimai.rsc.pool;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.weimai.rsc.NettyClient;
import io.netty.channel.ChannelFuture;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 链接服务器的通道池
 *
 * @author DiZhi
 * @since 2021-08-12 10:40
 */
public class ChannelPool {
    private final String ip;
    private final int port;
    private static final NettyClient nettyClient = NettyClient.getSingleInstance();

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 50, 60, TimeUnit.SECONDS,
                                                                 new LinkedBlockingQueue(),
                                                                 Executors.defaultThreadFactory(), new AbortPolicy());

    /**
     * 配置-链接空闲时间
     */
    private final int cfgConnLiveTime;
    /**
     * 配置-最大连接数
     */
    private final int cfgConnMaxSize;
    /**
     * 配置-核心连接数
     */
    private final int cfgCoreConnSize;
    /**
     * 配置-临时连接数
     */
    private final int cfgTempConnSize;
    /**
     * 配置-等待队列长度
     */
    private final int cfgWaitQueueSize;

    /**
     * 当前连接总数
     */
    private final AtomicInteger connSum;
    /**
     * 核心空闲连接数
     */
    private final AtomicInteger coreConnSize;
    /**
     * 临时空闲连接数
     */
    private final AtomicInteger tempConnSize;
    /**
     * 繁忙连接数
     */
    private final AtomicInteger busyConnSize;
    /**
     * 等待队列长度
     */
    private final AtomicInteger waitQueueSize;
    /**
     * 核心连接队列
     */
    private final ConcurrentLinkedQueue<ChannelFuture> coreIdle;

    /**
     * 临时连接队列
     */
    private final ConcurrentLinkedQueue<ChannelFuture> tempIdle;

    /**
     * 等待队列
     */
    private final ConcurrentLinkedQueue<TaskWapper> waitQueue;

    public ChannelPool(String ip, int port) {
        this.ip = ip;
        this.port = port;

        cfgCoreConnSize = ConnectPoolConfigurations.CORE_LINK_SIZE.getValue();
        cfgConnLiveTime = ConnectPoolConfigurations.CORE_LINK_LIVE_TIME.getValue();
        cfgTempConnSize = ConnectPoolConfigurations.MAX_LINK_SIZE.getValue() - ConnectPoolConfigurations.CORE_LINK_SIZE
                .getValue();
        cfgWaitQueueSize = ConnectPoolConfigurations.WAIT_QUEUE_SIZE.getValue();
        cfgConnMaxSize = ConnectPoolConfigurations.MAX_LINK_SIZE.getValue();

        coreConnSize = new AtomicInteger(0);
        tempConnSize = new AtomicInteger(0);
        busyConnSize = new AtomicInteger(0);
        waitQueueSize = new AtomicInteger(0);
        connSum = new AtomicInteger(0);

        coreIdle = new ConcurrentLinkedQueue<>();
        tempIdle = new ConcurrentLinkedQueue<>();
        //busy = new ConcurrentLinkedQueue<>();
        waitQueue = new ConcurrentLinkedQueue<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("当前连接总数："+connSum+"\r\n"+
                                       "当前核心空闲连接数："+coreConnSize+"\r\n"+
                                       "当前临时空闲连接数："+tempConnSize+"\r\n"+
                                       "当前任务队列数："+waitQueueSize+"\r\n"+
                                       "当前繁忙连接数："+busyConnSize+"\r\n"
                    );
                }


            }
        }).start();
    }

    public synchronized ChannelFuture getChannel() throws InterruptedException {

        if (connSum.get() <= 0) {
            return createNewConnAndReturn();
        }

        if (coreConnSize.get() > 0) {
            return dequeueFormCoreIdle2Busy();
        }

        if (busyConnSize.get() < cfgCoreConnSize) {
            return createNewConnAndReturn();
        }

        if (waitQueueSize.get() < cfgWaitQueueSize) {
            return waitForConn();
        }

        if (tempConnSize.get() > 0) {
            return dequeueFormTempIdle2Busy();
        }

        if (connSum.get() >= cfgConnMaxSize) {
            throw new RuntimeException("网络繁忙，连接数已达到最大配置数，请求被拒绝！");
        }

        return createNewConnAndReturn();
    }

    /**
     * 排队等待获取空闲连接
     *
     * @return 空闲连接
     * @throws InterruptedException 获取连接异常
     * @throws RuntimeException     获取连接超时
     */
    private ChannelFuture waitForConn() throws InterruptedException, RuntimeException {
        CountDownLatch lock = new CountDownLatch(1);
        TaskWapper task = new TaskWapper(lock);
        waitQueue.offer(task);
        boolean await = lock.await(1000 * 30, TimeUnit.MILLISECONDS);
        if (!await) {
            waitQueue.remove(task);
            throw new RuntimeException("获取连接超时！");
        }
        return Objects.requireNonNull(waitQueue.poll()).getChannelFuture();

    }

    /**
     * 从临时空闲连接队列获取连接并返回
     *
     * @return 临时空闲连接
     */
    private synchronized ChannelFuture dequeueFormTempIdle2Busy() {
        ChannelFuture poll = tempIdle.poll();
        tempConnSize.decrementAndGet();
        busyConnSize.incrementAndGet();
        return poll;
    }

    /**
     * 从核心空闲队列获取连接并返回
     *
     * @return 核心空闲队列的连接
     */
    private synchronized ChannelFuture dequeueFormCoreIdle2Busy() {
        ChannelFuture poll = coreIdle.poll();
        coreConnSize.decrementAndGet();
        busyConnSize.incrementAndGet();
        return poll;
    }

    /**
     * 创建新连接，并返回
     *
     * @return 新创建的连接
     */
    private synchronized ChannelFuture createNewConnAndReturn() throws InterruptedException {
        ChannelFuture connect = nettyClient.connect(ip, port);
        busyConnSize.incrementAndGet();
        connSum.incrementAndGet();
        return connect;

    }

    public void recycleChannel(ChannelFuture future) {
        if (coreConnSize.get() < cfgCoreConnSize) {
            recycleCoreConn(future);
        }else if (tempConnSize.get() < cfgTempConnSize) {
            recycleTempConn(future);
        }
    }

    /**
     * 回收临时连接，将连接在临时空闲连接队列入队，并触发任务队列任务处理
     * @param conn 归还的空闲连接
     */
    private synchronized void recycleTempConn(ChannelFuture conn) {
        tempIdle.offer(conn);
        tempConnSize.incrementAndGet();
        busyConnSize.decrementAndGet();
        submitTask();
    }
    /**
     * 回收核心连接，将连接在核心空闲连接队列入队，并触发任务队列任务处理
     * @param conn 归还的空闲连接
     */
    private synchronized void recycleCoreConn(ChannelFuture conn) {
        coreIdle.offer(conn);
        coreConnSize.incrementAndGet();
        busyConnSize.decrementAndGet();
        submitTask();
    }

    /**
     * 提交任务，异步处理任务队列
     */
    private void submitTask() {
        executor.execute(() -> {
            if (waitQueueSize.get() <= 0) {
                return;
            }
            TaskWapper task = waitQueue.poll();

            if (coreConnSize.get() > 0) {
                ChannelFuture conn = coreIdle.poll();
                coreConnSize.decrementAndGet();
                busyConnSize.incrementAndGet();
                task.setChannelFuture(conn);
            } else if (tempConnSize.get() > 0) {
                ChannelFuture conn = tempIdle.poll();
                tempConnSize.decrementAndGet();
                busyConnSize.incrementAndGet();
                task.setChannelFuture(conn);
            }
            task.getLock().countDown();
        });
    }
}
