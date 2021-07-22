package com.weimai.rsc.msg.impl;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.weimai.rsc.SendClient;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.MessageService;
import com.weimai.rsc.msg.ProtocolBody;
import com.weimai.rsc.msg.ProtocolHead;
import com.weimai.rsc.util.HessianUtils;
import io.netty.channel.ChannelFuture;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-07-07 11:24
 */
public class MessageServiceImpl implements MessageService {
    private static final MessageServiceImpl messageService = new MessageServiceImpl();
    private Map<String, ChannelFuture> channelFutures ;

    private Map<String ,MessageProtocol> messageProtocols ;

    private Map<String ,CountDownLatch> locks ;


    public static MessageServiceImpl getSingleInstance(){
        return messageService;
    }

    private MessageServiceImpl(){
        channelFutures = new ConcurrentHashMap<>();
        messageProtocols = new ConcurrentHashMap<>();
        locks = new ConcurrentHashMap<>();
    }

    public void registerLock(String id,CountDownLatch latch){
        locks.put(id,latch);
    };

    public void cacheMessage(MessageProtocol messageProtocol){
        messageProtocols.put(messageProtocol.getProtocolHead().getRequestId(),messageProtocol);
        CountDownLatch latch = locks.remove(messageProtocol.getProtocolHead().getRequestId());
        latch.countDown();
    }

    public MessageProtocol getResponse(String requestId){
        MessageProtocol messageProtocol = messageProtocols.get(requestId);
        if (messageProtocol == null) {
            throw new RuntimeException("响应为空！");
        }
        return messageProtocol;
    }
    public MessageProtocol loadMessage(String requestId){
        return messageProtocols.remove(requestId);
    }
    public ChannelFuture getChannel(String ip,int port){
        return channelFutures.get(getkey(ip,port));
    }

    public void registerChannel(ChannelFuture channelFuture){
        InetSocketAddress socketAddress = (InetSocketAddress)channelFuture.channel().remoteAddress();
        String key = getkey(socketAddress);
        channelFutures.put(key,channelFuture);
    }

    public void removeChannel(ChannelFuture channelFuture){
        InetSocketAddress socketAddress = (InetSocketAddress)channelFuture.channel().remoteAddress();
        String key = getkey(socketAddress);
        channelFutures.remove(key);
    }
    public void removeChannel(String ip,int port){
        String key = getkey(ip,port);
        channelFutures.remove(key);
    }


    private String getkey(InetSocketAddress socketAddress) {
        return socketAddress.getAddress().getHostAddress()+":"+ socketAddress.getPort();
    }
    private String getkey(String ip,int port) {
        return ip+":"+ port;
    }

    @Override
    public Object send(String message) {
        MessageProtocol request = getMessageProtocol();

        convertMsg2Protocol(message,request);

        MessageProtocol response = toSend(request);
        return null;
    }

    private MessageProtocol toSend(MessageProtocol request) {


        return null;
    }

    private MessageProtocol getMessageProtocol() {
        return new MessageProtocol();
    }

    private void convertMsg2Protocol(String message, MessageProtocol messageProtocol) {
        //Channel channel = channelFuture.channel();
        String requestId = UUID.randomUUID().toString().replace("-", "");
        byte[] sqlBytes = message.getBytes(StandardCharsets.UTF_8);
        //封装协议头
        ProtocolHead protocolHead = new ProtocolHead();
        protocolHead.setRequestId(requestId);
        //封装协议体
        ProtocolBody protocolBody = new ProtocolBody();
        protocolBody.setContent(sqlBytes);
        //封装协议包
        messageProtocol.setProtocolBody(protocolBody);
        messageProtocol.setProtocolHead(protocolHead);
        //messageProtocol.setBodyLength(HessianUtils.write(protocolBody).length);
        //messageProtocol.setHeadLength(HessianUtils.write(protocolHead).length);
        //channel.writeAndFlush(messageProtocol);
    }

    private boolean isTimeout(){
        CountDownLatch countDownLatch = new CountDownLatch(1);
        boolean await = false;
        try {
            await = countDownLatch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return await;
    };
}
