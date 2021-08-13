package com.weimai.rsc.msg.impl;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.MessageService;
import com.weimai.rsc.msg.MessageProtocolBody;
import com.weimai.rsc.msg.MessageProtocolHead;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-07-07 11:24
 */
public class MessageServiceImpl implements MessageService {
    private static final MessageServiceImpl messageService = new MessageServiceImpl();

    private Map<String ,MessageProtocol> messageProtocols ;

    private Map<String ,CountDownLatch> locks ;


    public static MessageServiceImpl getSingleInstance(){
        return messageService;
    }

    private MessageServiceImpl(){
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
        MessageProtocolHead messageProtocolHead = new MessageProtocolHead();
        messageProtocolHead.setRequestId(requestId);
        //封装协议体
        MessageProtocolBody messageProtocolBody = new MessageProtocolBody();
        messageProtocolBody.setContent(sqlBytes);
        //封装协议包
        messageProtocol.setProtocolBody(messageProtocolBody);
        messageProtocol.setProtocolHead(messageProtocolHead);
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
