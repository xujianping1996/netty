package com.weimai.rsc.msg.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import com.weimai.rsc.channel.ChannelWriteFunctionService;
import com.weimai.rsc.msg.MessageProtocol;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-07-07 11:24
 */
public class MessageServiceImpl {
    private static final MessageServiceImpl messageService = new MessageServiceImpl();

    private final Map<Long ,MessageProtocol> messageProtocols ;

    private final Map<Long ,CountDownLatch> locks ;


    public static MessageServiceImpl getSingleInstance(){
        return messageService;
    }

    private MessageServiceImpl(){
        messageProtocols = new ConcurrentHashMap<>();
        locks = new ConcurrentHashMap<>();
    }


    public MessageProtocol synReceive(Long requestId , ChannelWriteFunctionService service){
        CountDownLatch latch = new CountDownLatch(1);
        locks.put(requestId,latch);
        service.write();
        try {
            latch.await();
            return messageProtocols.get(requestId);
        } catch (InterruptedException e) {
            throw new RuntimeException("读取响应超时！");
        }finally {
            messageProtocols.remove(requestId);
        }

    }
    public void cacheMessage(MessageProtocol messageProtocol){
        messageProtocols.put(messageProtocol.getProtocolHead().getRequestId(),messageProtocol);
        CountDownLatch latch = locks.remove(messageProtocol.getProtocolHead().getRequestId());
        latch.countDown();
    }

}
