package com.weimai.rsc;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import com.weimai.rsc.constant.Suffix;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.ProtocolBody;
import com.weimai.rsc.msg.ProtocolHead;
import com.weimai.rsc.pool.SendExecutorPool;
import com.weimai.rsc.util.HessianUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 发送消息工具类
 *
 * @author DiZhi
 * @since 2021-06-21 20:18
 */
public class RSCClient extends SimpleChannelInboundHandler<MessageProtocol> implements Runnable {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    private String sql;
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
        this.sql = sql;
        SendExecutorPool.execute(this);

        return null;
    }
    //@Override
    //public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    //    ByteBuf byteBuf = (ByteBuf)msg;
    //    try{
    //        int readableBytes = byteBuf.readableBytes();
    //        byte[] bytes = new byte[readableBytes];
    //        byteBuf.readBytes(bytes);
    //
    //        //System.out.println("readableBytes is{"+readableBytes+"},server received message：{"+new String(bytes, StandardCharsets.UTF_8)+"}");
    //        System.out.println("::"+new String(bytes));
    //    }catch (Exception e){
    //        e.printStackTrace();
    //
    //    }finally {
    //        byteBuf.release();
    //    }
    //}

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol messageProtocol)
            throws Exception {
        System.out.println(new String(messageProtocol.getProtocolBody().getContent(), StandardCharsets.UTF_8));
    }

    @Override
    public void run() {


        Channel channel = channelFuture.channel();
        String requestId = UUID.randomUUID().toString().replace("-", "");
        byte[] sqlBytes = sql.getBytes(StandardCharsets.UTF_8);
        ProtocolBody protocolBody = new ProtocolBody();
        ProtocolHead protocolHead = new ProtocolHead();
        protocolBody.setContent(sqlBytes);
        protocolHead.setRequestId(requestId);
        MessageProtocol messageProtocol = new MessageProtocol();
        messageProtocol.setProtocolBody(protocolBody);
        messageProtocol.setProtocolHead(protocolHead);
        messageProtocol.setBodyLength(HessianUtils.write(protocolBody).length);
        messageProtocol.setHeadLength(HessianUtils.write(protocolHead).length);
        channel.writeAndFlush(messageProtocol);

    }
}
