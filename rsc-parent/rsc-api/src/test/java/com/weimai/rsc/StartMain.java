package com.weimai.rsc;

import java.util.Scanner;

import com.weimai.rsc.handler.NettyClientHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.base64.Base64Decoder;
import io.netty.handler.codec.base64.Base64Encoder;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-06-17 14:39
 */
public class StartMain {
    //private static Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);
    public static String IP = "127.0.0.1";
    public static int PORT = 8088;

    public static void main(String[] args) throws InterruptedException {
        RSCClient rscClient = new RSCClient();
        RSCClient connect = rscClient.connect(IP, PORT);

        Object execute = connect.execute("select * from asdf;");

        //SendClient sendClient = new SendClient(IP, PORT);
        //sendClient.sendMessage("select * from user;");
        //while (true){
        //    sendClient.sendMessage("张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿"
        //                           + "森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳"
        //                           + "张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森"
        //                           + "纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森"
        //                           + "纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳"
        //                           + "张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张"
        //                           + "张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张"
        //                           + "张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张"
        //                           + "张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张"
        //                           + "张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张"
        //                           + "张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张"
        //                           + "张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张"
        //                           + "阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森"
        //                           + "纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张"
        //                           + "阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿"
        //                           + "森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿"
        //                           + "森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森"
        //                           + "纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森"
        //                           + "纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张"
        //                           + "阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张"
        //                           + "阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森"
        //                           + "纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿"
        //                           + "森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张"
        //                           + "阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳"
        //                           + "张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿"
        //                           + "森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森"
        //                           + "纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张"
        //                           + "阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森"
        //                           + "纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳"
        //                           + "张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森"
        //                           + "纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳"
        //                           + "张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张"
        //                           + "阿森纳张阿森纳张阿森纳张阿森纳张阿森纳张阿森纳");
        //}
        //Scanner s = new Scanner(System.in);
        //
        //System.out.println("enter your name:");
        //while (true){
        //    String name = s.nextLine();
        //    sendClient.sendMessage(name);
        //}



    }
}
