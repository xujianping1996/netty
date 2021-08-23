package com.weimai.rsc.pool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 链接池
 *
 * @author DiZhi
 * @since 2021-07-02 16:20
 */
public class ChannelGroup {

    private static final ChannelGroup CHANNEL_GROUP = new ChannelGroup();

    private final Map<String ,ChannelWrapper> channels = new ConcurrentHashMap<>();



    public static ChannelGroup getSingleInstance(){
        return CHANNEL_GROUP;
    }

    public ChannelWrapper getChannel(String ip,int port){
        String key = channelKey(ip, port);
        ChannelWrapper existedWrapper = channels.get(key);
        if (existedWrapper==null){
            synchronized (key.intern()){
                if (channels.get(key)==null){
                    channels.putIfAbsent(key,new ChannelWrapper(ip,port));
                }
            }
        }
        return channels.get(key);

    }

    public void destroyChannel(String ip,int port){
        String key = channelKey(ip, port);
        ChannelWrapper existedWrapper = channels.get(key);
        existedWrapper.destroy();
        channels.remove(key);
        System.out.println("当前连接数总数"+channels.size());
        System.out.println("channels:"+channels);
    }

    private String channelKey(String ip,int port){
        return ip+":"+port;
    }

}
