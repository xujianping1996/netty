package com.weimai.rsc.pool;

import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;

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
    /**
     * 核心链接数
     */
    private int coreLinkSize;
    /**
     * 核心链接存活时间
     */
    private int coreLinkLiveTime;
    private final LinkedBlockingQueue<ChannelFuture> idle;

    public ChannelPool(String ip, int port) {
        this.ip = ip;
        this.port = port;
        coreLinkSize = ConnectPoolConfigurations.CORE_LINK_SIZE.getValue();
        coreLinkLiveTime = ConnectPoolConfigurations.CORE_LINK_LIVE_TIME.getValue();
        idle = new LinkedBlockingQueue<>(coreLinkSize);
        for (int i = 0; i < coreLinkSize; i++) {
            ChannelFuture connect = null;
            try {
                connect = nettyClient.connect(ip, port);
                idle.offer(connect);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //空闲队列

    public ChannelFuture getChannel() {
        ChannelFuture channelFuture = idle.poll();
        if (channelFuture.channel().isActive()) {
            return channelFuture;
        }
        return channelFuture.channel().connect(new InetSocketAddress(ip, port));
    }

    public void recycleChannel(ChannelFuture future) {
        idle.offer(future);
    }
}
