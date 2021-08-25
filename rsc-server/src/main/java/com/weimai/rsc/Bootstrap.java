package com.weimai.rsc;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import com.weimai.rsc.Configurations.Server;
import com.weimai.rsc.handler.*;
import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.yaml.snakeyaml.Yaml;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * Main
 *
 * @author DiZhi
 * @since 2021-06-16 14:49
 */
public class Bootstrap {
    public static void main(String[] args) {

        Server server = Configurations.CONFIGURATIONS.getServer();

        //配置两个服务端的NIO线程组，一个用于接收客服端的链接，另一个用于进行SocketChannel的网络读写。
        //NioEventLoopGroup是一个处理I/O操作的多线程事件循环
        //"boss"：接收一个传入连接
        EventLoopGroup boss = new NioEventLoopGroup(2);
        //"worker" : 当boss接收连接并把接收的连接注册给worker，work就开始处理
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            //ServerBootstrap是一个帮助类，可以设置服务器
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    //NioServerSocketChannel用于实例化新通道来接收传入的连接
                    .channel(NioServerSocketChannel.class)
                    //ChannelInitializer用于配置新通道
                    .childHandler(new MyChannelInitializer())
                    //ChannelOption和ChannelConfig可以设置各种参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //option()用于接受传入连接的NioServerSocketChannel,childOption()用于父ServerChannel接受的通道
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            //异步地绑定服务器；调用 sync()方法阻塞等待直到绑定完成
            ChannelFuture f = bootstrap.bind(server.getPort()).sync();
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

    private static void loadConfigurations() {
        Configurations instance = Configurations.getInstance();
    }

    static class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
            ChannelPipeline pipeline = channel.pipeline();
            pipeline.addLast(new MessageEncoder())
                    //.addLast(new HttpRequestDecoder())
                    //.addLast(new HttpObjectAggregator(65536))
                    //.addLast(new HttpResponseEncoder())
                    .addLast(new MessageDecoder())

                    //.addLast("fileServerHandler", new HttpFileServerHander(DEFAULT_URL))
                    .addLast(new NettyServerHandler());

        }
    }
}