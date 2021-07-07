package com.weimai.rsc;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.ProtocolBody;
import com.weimai.rsc.msg.ProtocolHead;
import com.weimai.rsc.msg.impl.MessageServiceImpl;
import com.weimai.rsc.util.HessianUtils;
import io.netty.channel.ChannelFuture;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-06-17 14:38
 */
public class SendClient {
    private NettyClient nettyClient;
    private MessageServiceImpl messageService;
    private ChannelFuture channelFuture = null;

    public SendClient() {
        nettyClient = NettyClient.getSingleInstance();
        messageService = MessageServiceImpl.getSingleInstance();
    }

    public SendClient connect(String ip, int port) throws InterruptedException {
        ChannelFuture channelFuture = nettyClient.connect(ip, port);
        this.channelFuture = channelFuture;
        messageService.registerChannel(channelFuture);
        return this;
    }

    public Object execute(String message) {
        if (channelFuture == null) {
            throw new RuntimeException("未链接到远程服务器或已断开链接！");
        }
        MessageProtocol request = new MessageProtocol();
        convertMsg2Protocol(message, request);
        nettyClient.sendMessage(this.channelFuture, request);
        MessageProtocol response = messageService.loadMessage(request.getProtocolHead().getRequestId());
        return response;
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
        messageProtocol.setBodyLength(HessianUtils.write(protocolBody).length);
        messageProtocol.setHeadLength(HessianUtils.write(protocolHead).length);
    }
}
