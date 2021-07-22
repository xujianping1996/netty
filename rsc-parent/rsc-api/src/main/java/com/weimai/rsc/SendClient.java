package com.weimai.rsc;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.weimai.rsc.constant.ProtocolDataType;
import com.weimai.rsc.msg.DBTable;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.ProtocolBody;
import com.weimai.rsc.msg.ProtocolHead;
import com.weimai.rsc.msg.impl.MessageServiceImpl;
import com.weimai.rsc.util.HessianUtils;
import io.netty.channel.ChannelFuture;

import static com.weimai.rsc.constant.ProtocolDataType.COMMAND_LINE_SQL_SELECT;
import static com.weimai.rsc.constant.ProtocolDataType.COMMAND_LINE_SQL_UPDATE;

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

    public DBTable executeQuery(String message) {
        if (channelFuture == null) {
            throw new RuntimeException("未链接到远程服务器或已断开链接！");
        }
        MessageProtocol request = new MessageProtocol();
        convertMsg2Protocol(message, COMMAND_LINE_SQL_SELECT, request);
        MessageProtocol messageProtocol = nettyClient.sendMessage(this.channelFuture, request);
        ProtocolBody protocolBody = messageProtocol.getProtocolBody();

        byte[] content = protocolBody.getContent();

        return HessianUtils.read(content, DBTable.class);
    }

    public Integer executeUpdate(String message) {
        if (channelFuture == null) {
            throw new RuntimeException("未链接到远程服务器或已断开链接！");
        }
        MessageProtocol request = new MessageProtocol();
        convertMsg2Protocol(message, COMMAND_LINE_SQL_UPDATE, request);
        MessageProtocol messageProtocol = nettyClient.sendMessage(this.channelFuture, request);
        ProtocolBody protocolBody = messageProtocol.getProtocolBody();

        byte[] content = protocolBody.getContent();
        int b0 = content[0] & 0xFF;
        int b1 = content[1] & 0xFF;
        int b2 = content[2] & 0xFF;
        int b3 = content[3] & 0xFF;
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }

    private void convertMsg2Protocol(String message, byte CommandType, MessageProtocol messageProtocol) {
        //Channel channel = channelFuture.channel();
        String requestId = UUID.randomUUID().toString().replace("-", "");
        byte[] sqlBytes = message.getBytes(StandardCharsets.UTF_8);
        //封装协议头
        ProtocolHead protocolHead = new ProtocolHead();
        protocolHead.setRequestId(requestId);
        protocolHead.setDataType(CommandType);
        //封装协议体
        ProtocolBody protocolBody = new ProtocolBody();
        protocolBody.setContent(sqlBytes);
        //封装协议包
        messageProtocol.setProtocolBody(protocolBody);
        messageProtocol.setProtocolHead(protocolHead);
        //messageProtocol.setBodyLength(HessianUtils.write(protocolBody).length);
        //messageProtocol.setHeadLength(HessianUtils.write(protocolHead).length);
    }
}
