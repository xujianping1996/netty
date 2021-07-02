package com.weimai.rsc.handler;

import java.nio.charset.StandardCharsets;

import com.weimai.rsc.msg.MessageProtocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-06-16 14:50
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive connect success!");
    }

    //public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    //    MessageProtocol message = (MessageProtocol)msg;
    //    byte[] content = message.getContent();
    //    //String s = new String(content, StandardCharsets.UTF_8);
    //    //System.out.println();
    //
    //}

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol messageProtocol)
            throws Exception {

    }
}
