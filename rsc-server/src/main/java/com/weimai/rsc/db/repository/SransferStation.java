package com.weimai.rsc.db.repository;

import com.weimai.rsc.msg.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-07-16 16:13
 */
public interface SransferStation {
    ///**
    // * 缓存请求所使用的链接通道
    // * @param requestId
    // * @param channelFuture
    // */
    //void cachedChannel(String requestId, ChannelFuture channelFuture);
    //
    ///**
    // * 缓存响应消息
    // * @param requestId
    // * @param message
    // */
    //void cachedMessage(String requestId,Message message);

    /**
     * 发送消息给客户端
     * @param channel
     * @param message
     */
    void sendMessageToClient(Message message);
}
