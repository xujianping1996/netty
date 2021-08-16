package com.weimai.rsc.pool;

import java.util.concurrent.CountDownLatch;

import io.netty.channel.ChannelFuture;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * 任务对象封装
 * @author DiZhi
 * @since 2021-08-16 18:13
 */
public class TaskWapper {
    private CountDownLatch lock ;
    private ChannelFuture channelFuture;

    public TaskWapper() {
    }

    public TaskWapper(CountDownLatch lock) {
        this.lock = lock;
    }

    public TaskWapper(CountDownLatch lock, ChannelFuture channelFuture) {
        this.lock = lock;
        this.channelFuture = channelFuture;
    }

    public CountDownLatch getLock() {
        return lock;
    }

    public void setLock(CountDownLatch lock) {
        this.lock = lock;
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    public void setChannelFuture(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }
}
