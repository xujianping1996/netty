package com.weimai.rsc;

import java.nio.charset.StandardCharsets;

import com.weimai.rsc.constant.Suffix;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 发送消息工具类
 *
 * @author DiZhi
 * @since 2021-06-21 20:18
 */
public class RSCClient extends ChannelInboundHandlerAdapter {

    private SendClient sendClient ;
    private ChannelFuture channelFuture;
    //private ChannelPromise channelPromise;

    public RSCClient() throws InterruptedException {
        sendClient = SendClient.getInstance();
    }

    public RSCClient connect (String ip,int port) throws InterruptedException{
        channelFuture = sendClient.connect(ip,port);
        return this;
    }
    public Object execute(String sql){
        Channel channel = channelFuture.channel();

        ByteBuf delemiter = Unpooled.buffer();
        delemiter.writeBytes((sql + Suffix.LINE_FEED).getBytes(StandardCharsets.UTF_8));
        channel.writeAndFlush(delemiter);
        return null;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf)msg;
        try{
            int readableBytes = byteBuf.readableBytes();
            byte[] bytes = new byte[readableBytes];
            byteBuf.readBytes(bytes);

            //System.out.println("readableBytes is{"+readableBytes+"},server received message：{"+new String(bytes, StandardCharsets.UTF_8)+"}");
            System.out.println("::"+new String(bytes));
        }catch (Exception e){
            e.printStackTrace();

        }finally {
            byteBuf.release();
        }
    }

}
