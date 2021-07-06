package com.weimai.rsc.handler;

import com.weimai.rsc.db.SqlExecutor;
import com.weimai.rsc.msg.MessageProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-06-16 14:53
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("connected!");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol messageProtocol)
            throws Exception {
        System.out.println("接收到请求："+messageProtocol.toString());
        MessageProtocol execute = SqlExecutor.execute(messageProtocol);
        channelHandlerContext.writeAndFlush(execute);

    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }
}
