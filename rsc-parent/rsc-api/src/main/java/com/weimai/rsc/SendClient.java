package com.weimai.rsc;

import java.nio.charset.StandardCharsets;

import com.weimai.rsc.constant.Suffix;
import com.weimai.rsc.handler.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

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


    public static SendClient getInstance(){
        return instance;
    }
    public SendClient() {
        initClient();
    }

    public void initClient() {
        client = new NioEventLoopGroup();
        bootstrap = new io.netty.bootstrap.Bootstrap();
        bootstrap.group(client).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(
                new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        ByteBuf delemiter = Unpooled.buffer();
                        delemiter.writeBytes(Suffix.LINE_FEED.getBytes(StandardCharsets.UTF_8));
                        p.addLast(new DelimiterBasedFrameDecoder(1024 * 1024, delemiter))
                                .addLast(new NettyClientHandler());
                    }
                });
    }
    public ChannelFuture connect(String ip,int port) throws InterruptedException {
        return bootstrap.connect(ip ,port).sync();
    }

    public Bootstrap getBootstrap(){
        return this.bootstrap;
    }

}
