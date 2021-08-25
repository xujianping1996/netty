package com.weimai.rsc.handler;

import java.net.InetSocketAddress;

import com.weimai.rsc.log.InternalLogger;
import com.weimai.rsc.log.InternalLoggerFactory;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.impl.MessageServiceImpl;
import com.weimai.rsc.channel.ChannelGroup;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 发送消息工具类
 *
 * @author DiZhi
 * @since 2021-06-21 20:18
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(NettyClientHandler.class);
    private final MessageServiceImpl messageService;
    private final ChannelGroup channelGroup;
    public NettyClientHandler() {
        messageService = MessageServiceImpl.getSingleInstance();
        channelGroup = ChannelGroup.getSingleInstance();
    }

    public void channelActive(ChannelHandlerContext ctx) {
        InetSocketAddress localSocketAddress = (InetSocketAddress)ctx.channel().localAddress();
        int localPort = localSocketAddress.getPort();
        String localAddress = localSocketAddress.getAddress().getHostAddress();
        InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
        int port = socketAddress.getPort();
        String hostAddress = socketAddress.getAddress().getHostAddress();
        LOGGER.info("客户端["+localAddress+":"+localPort+"]->服务器["+hostAddress+":"+port+"]");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
        ctx.close();
        LOGGER.info("连接["+socketAddress.getAddress().getHostAddress()+":"+socketAddress.getPort()+"]已断开");
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol messageProtocol) {
        messageService.cacheMessage(messageProtocol);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        Channel channel = ctx.channel();
        InetSocketAddress remoteAddress = (InetSocketAddress)channel.remoteAddress();
        String ip = remoteAddress.getAddress().getHostAddress();
        int port = remoteAddress.getPort();


        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent)evt;
            String eventType = null;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
            }
            LOGGER.info(ctx.channel().remoteAddress() + "超时事件:" + eventType);
            channelGroup.destroyChannel(ip,port);
            ctx.channel().close();
        }
    }
}
