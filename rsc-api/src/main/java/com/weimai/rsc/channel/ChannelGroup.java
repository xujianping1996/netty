package com.weimai.rsc.channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.weimai.rsc.log.InternalLogger;
import com.weimai.rsc.log.InternalLoggerFactory;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 链接池
 *
 * @author DiZhi
 * @since 2021-07-02 16:20
 */
public class ChannelGroup {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ChannelGroup.class);
    private static final ChannelGroup CHANNEL_GROUP = new ChannelGroup();

    private final Map<String, ChannelWrapper> channels = new ConcurrentHashMap<>();

    public static ChannelGroup getSingleInstance() {
        return CHANNEL_GROUP;
    }

    public ChannelWrapper getChannel(String ip, int port) {
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

    public void destroyChannel(String ip, int port) {
        String key = channelKey(ip, port);
        ChannelWrapper existedWrapper = channels.get(key);
        existedWrapper.destroy( ip,  port);
        channels.remove(key);
        LOGGER.info("连接[" + ip + ":" + port + "]达到 netty 配置的最大空闲时间，已断开连接！" + "当前长连接数总数" + channels.size());
    }

    private String channelKey(String ip, int port) {
        return ip + ":" + port;
    }

}
