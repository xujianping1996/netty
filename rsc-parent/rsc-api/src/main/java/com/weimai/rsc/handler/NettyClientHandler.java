package com.weimai.rsc.handler;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import com.weimai.rsc.SendClient;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.ProtocolBody;
import com.weimai.rsc.msg.ProtocolHead;
import com.weimai.rsc.pool.SendExecutorPool;
import com.weimai.rsc.util.HessianUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
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
public class NettyClientHandler extends SimpleChannelInboundHandler<MessageProtocol> implements Runnable {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    private String sql;
    private SendClient sendClient ;
    private ChannelFuture channelFuture;
    //private ChannelPromise channelPromise;

    public NettyClientHandler() throws InterruptedException {
        sendClient = SendClient.getInstance();
    }

    public NettyClientHandler connect (String ip,int port) throws InterruptedException{
        channelFuture = sendClient.connect(ip,port);
        return this;
    }
    public Object execute(String sql){
        this.sql = sql;
        SendExecutorPool.execute(this);

        return null;
    }

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
