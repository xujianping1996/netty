package com.weimai.rsc.handler;

import java.net.InetSocketAddress;

import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.impl.MessageServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 发送消息工具类
 *
 * @author DiZhi
 * @since 2021-06-21 20:18
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {
    private final MessageServiceImpl messageService;

    public NettyClientHandler() {
        messageService = MessageServiceImpl.getSingleInstance();
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress localSocketAddress = (InetSocketAddress)ctx.channel().localAddress();
        int localPort = localSocketAddress.getPort();
        String localAddress = localSocketAddress.getAddress().getHostAddress();
        InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
        int port = socketAddress.getPort();
        String hostAddress = socketAddress.getAddress().getHostAddress();
        System.out.println("客户端["+localAddress+":"+localPort+"]->服务器["+hostAddress+":"+port+"]");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().remoteAddress();

        System.out.println(socketAddress.getAddress().getHostAddress()+":"+socketAddress.getPort()+"已断开链接");
        ctx.close();
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol messageProtocol) {
        messageService.cacheMessage(messageProtocol);
    }

}
