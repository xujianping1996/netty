package com.weimai.rsc.channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.weimai.rsc.log.InternalLogger;
import com.weimai.rsc.log.InternalLoggerFactory;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * <p>长连接池
 * <p>提供获取连接、主动销毁连接的功能，内部缓存活跃的连接
 *
 * @author DiZhi
 * @since 2021-07-02 16:20
 */
public class ChannelWrapperFactory {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ChannelWrapperFactory.class);

    public static final ChannelWrapperFactory CHANNEL_WRAPPER_FACTORY = new ChannelWrapperFactory();

    /**
     * 长连接缓存池
     */
    private final Map<String, ChannelWrapper> channels = new ConcurrentHashMap<>();

    private ChannelWrapperFactory(){

    }


    public static ChannelWrapper getInstance(String ip, int port) {
        return CHANNEL_WRAPPER_FACTORY.newInstance(ip,port);
    }

    /**
     * 获取连接，如果当前远程服务器的连接仍然可用，直接返回。否则，创建新的连接。
     * @param ip 要连接的远程服务器 ip
     * @param port 要连接的远程服务器端口
     * @return 连接对象
     */
    private ChannelWrapper newInstance(String ip, int port){
        String key = channelKey(ip, port);
        ChannelWrapper existedWrapper = channels.get(key);
        if (existedWrapper == null) {
            synchronized (key.intern()) {
                if (channels.get(key) == null) {
                    channels.putIfAbsent(key, new ChannelWrapper(ip, port));
                }
            }
        }
        return channels.get(key);
    }

    /**
     * 主动断开连接
     * @param ip 远程服务器 ip
     * @param port 远程服务器端口
     */
    public static void destroyInstance(String ip, int port) {
        CHANNEL_WRAPPER_FACTORY.disconnect(ip,port);
    }

    private void disconnect(String ip, int port){
        String key = channelKey(ip, port);
        ChannelWrapper existedWrapper = channels.get(key);
        existedWrapper.destroy(ip, port);
        channels.remove(key);
        LOGGER.info("连接[" + ip + ":" + port + "]达到 netty 配置的最大空闲时间，已断开连接！" + "当前长连接数总数" + channels.size());
    }

    private String channelKey(String ip, int port) {
        return ip + ":" + port;
    }

}
