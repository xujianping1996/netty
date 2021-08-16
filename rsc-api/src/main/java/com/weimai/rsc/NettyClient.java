package com.weimai.rsc;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import com.weimai.rsc.handler.MessageDecoder;
import com.weimai.rsc.handler.MessageEncoder;
import com.weimai.rsc.handler.NettyClientHandler;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.impl.MessageServiceImpl;
import com.weimai.rsc.pool.ChannelPoolGroup;
import com.weimai.rsc.util.HessianUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import static com.weimai.rsc.constant.ProtocolDataType.ERROR;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * netty client
 *
 * @author DiZhi
 * @since 2021-07-07 15:28
 */
public class NettyClient {
    private static final NettyClient nettyClient = new NettyClient();

    private final MessageServiceImpl messageService;

    private final Bootstrap bootstrap;


    protected final ChannelPoolGroup pool = ChannelPoolGroup.pool();

    public static NettyClient getSingleInstance() {
        return nettyClient;
    }

    private NettyClient() {
        messageService = MessageServiceImpl.getSingleInstance();
        EventLoopGroup client = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(client).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(
                new RSCClientChannelHandler());
    }

    private static class RSCClientChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();
            pipeline.addLast(new MessageDecoder());
            pipeline.addLast(new MessageEncoder());
            pipeline.addLast(new NettyClientHandler());
        }
    }

    public ChannelFuture connect(String ip, int port) throws InterruptedException {
        return bootstrap.connect(ip, port).sync();
    }

    public MessageProtocol sendMessage(String ip,int port, MessageProtocol messageProtocol) {
        ChannelFuture channelFuture = null;
        try {
            channelFuture = pool.getChannel(ip, port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InetSocketAddress localAddress = (InetSocketAddress)channelFuture.channel().localAddress();
        InetSocketAddress remoteAddress = (InetSocketAddress)channelFuture.channel().remoteAddress();
        String localHostAddress = localAddress.getAddress().getHostAddress();
        String remoteHostAddress = remoteAddress.getAddress().getHostAddress();
        int localPort = localAddress.getPort();
        int remotePort = remoteAddress.getPort();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        messageService.registerLock(messageProtocol.getProtocolHead().getRequestId(), countDownLatch);
        channelFuture.channel().writeAndFlush(messageProtocol);
        System.out.println("客户端["+localHostAddress+":"+localPort+"]发送消息到服务端["+remoteHostAddress+":"+remotePort+"]");
        pool.recycleChannel(channelFuture);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MessageProtocol response = messageService.getResponse(messageProtocol.getProtocolHead().getRequestId());
        if (ERROR == response.getProtocolHead().getDataType()) {
            throw new RuntimeException("server error ! info :" + Arrays
                    .toString(HessianUtils.read(response.getProtocolBody().getContent(), StackTraceElement[].class)));
        }
        return response;

    }
}
