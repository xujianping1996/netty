package com.weimai.rsc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
 *
 * @author DiZhi
 * @since 2021-06-17 14:38
 */
public class SendClient {
    private static final SendClient instance = new SendClient();
    EventLoopGroup client;
    Bootstrap bootstrap;
    private Map<String,ChannelFuture> channelFutures = new ConcurrentHashMap();

    public static SendClient getInstance(){
        return instance;
    }
    private SendClient() {
        initClient();
    }

    private void initClient() {
        client = new NioEventLoopGroup();
        bootstrap = new io.netty.bootstrap.Bootstrap();
        bootstrap.group(client).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(
                new RSCClientChannelHandler());
    }
    private static class RSCClientChannelHandler extends ChannelInitializer<SocketChannel>{
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();
            pipeline.addLast(new MessageDecoder());
            pipeline.addLast(new MessageEncoder());
            pipeline.addLast(new NettyClientHandler());
        }
    }
    public ChannelFuture connect(String ip,int port) throws InterruptedException {
        ChannelFuture sync = channelFutures.get(ip+port);
        if (sync==null){
            sync = bootstrap.connect(ip, port).sync();
            channelFutures.put(ip+port,sync);
        }
        return sync;
    }

    public Bootstrap getBootstrap(){
        return this.bootstrap;
    }

}
