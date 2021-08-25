package com.weimai.rsc.channel;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import com.weimai.rsc.NettyClient;
import com.weimai.rsc.log.InternalLogger;
import com.weimai.rsc.log.InternalLoggerFactory;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.impl.MessageServiceImpl;
import com.weimai.rsc.util.HessianUtils;
import io.netty.channel.Channel;

import static com.weimai.rsc.constant.ProtocolDataType.ERROR;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-08-12 14:34
 */
public class ChannelWrapper {
    private Channel channel;
    private final String ip;
    private final int port;
    private final MessageServiceImpl messageService;
    private final NettyClient nettyClient = NettyClient.getSingleInstance();
    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ChannelWrapper.class);

    public ChannelWrapper(String ip,Integer port ){
        this.ip = ip;
        this.port = port;
        try {
            this.channel = nettyClient.doConnect(ip,port).channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        messageService = MessageServiceImpl.getSingleInstance();
    }

    public MessageProtocol sendMessage(MessageProtocol requestMessage){
        InetSocketAddress localAddress = (InetSocketAddress)this.channel.localAddress();
        String localHostAddress = localAddress.getAddress().getHostAddress();
        int localPort = localAddress.getPort();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        messageService.registerLock(requestMessage.getProtocolHead().getRequestId(), countDownLatch);
        this.channel.writeAndFlush(requestMessage);
        LOGGER.debug("客户端["+localHostAddress+":"+localPort+"]发送消息到服务端["+ip+":"+port+"]");
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.debug("客户端["+localHostAddress+":"+localPort+"]从服务端["+ip+":"+port+"]接受到消息");
        MessageProtocol response = messageService.getResponse(requestMessage.getProtocolHead().getRequestId());
        if (ERROR == response.getProtocolHead().getDataType()) {
            throw new RuntimeException("server error ! info :" + Arrays
                    .toString(HessianUtils.read(response.getProtocolBody().getContent(), StackTraceElement[].class)));
        }
        return response;
    }


    void destroy(String ip, int port){
        this.channel.close();
        LOGGER.info("客户端主动关闭["+ip+":"+port+"]连接！");
    }

}
