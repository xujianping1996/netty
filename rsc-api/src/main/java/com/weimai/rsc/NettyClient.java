package com.weimai.rsc;

import com.weimai.rsc.handler.MessageDecoder;
import com.weimai.rsc.handler.MessageEncoder;
import com.weimai.rsc.handler.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;


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

    private final Bootstrap bootstrap;
    public static NettyClient getSingleInstance() {
        return nettyClient;
    }

    private NettyClient() {
        EventLoopGroup client = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(client).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(
                new RSCClientChannelHandler());
    }

    private static class RSCClientChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel socketChannel) {
            ChannelPipeline pipeline = socketChannel.pipeline();
            pipeline.addLast(new MessageDecoder());
            pipeline.addLast(new MessageEncoder());
            pipeline.addLast(new NettyClientHandler());
        }
    }

    public ChannelFuture doConnect(String ip,Integer port) throws InterruptedException {
        return bootstrap.connect(ip,port).sync();
    }

    private String channelKey(String ip,int port){
        return ip+":"+port;
    }
}
