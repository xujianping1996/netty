package com.weimai.rsc.pool;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.weimai.rsc.msg.MessageProtocol;
import io.netty.channel.ChannelFuture;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 链接池
 *
 * @author DiZhi
 * @since 2021-07-02 16:20
 */
public class ConnectedPool {

    private static final ConnectedPool CONNECTED_POOL = new ConnectedPool();

    private Map<String ,ChannelPool> channelPools = new ConcurrentHashMap<>();


    /**
     * 请求等待队列
     */
    private ConcurrentLinkedQueue<MessageProtocol> requestMessageWaitQueue = new ConcurrentLinkedQueue<>();

    private ConnectedPool (){

    }

    /**
     * 获取当前 jvm 中的连接池
     * @return
     */
    public static ConnectedPool pool(){
        return CONNECTED_POOL;
    }
    public ChannelFuture getChannel(String ip,int port){
        ChannelPool channelPool = channelPools.get(poolKey(ip, port));
        if (channelPool==null){
            channelPool = new ChannelPool(ip,port);
            channelPools.put(poolKey(ip,port),channelPool);
        }
        return channelPool.getChannel();
    }

    public void recycleChannel(ChannelFuture channelFuture){
        InetSocketAddress inetSocketAddress = (InetSocketAddress)channelFuture.channel().remoteAddress();
        ChannelPool channelPool = channelPools.get(
                poolKey(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort()));
        channelPool.recycleChannel(channelFuture);
    }

    private String poolKey(String ip,int port){
        return ip+"_"+port;
    }

}
