package com.weimai.rsc.pool;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 默认值
 *
 * @author DiZhi
 * @since 2021-08-12 11:02
 */
public enum ConnectPoolConfigurations {

    MAX_LINK_SIZE("rsc.client.channel.pool.max.link.size",10),
    CORE_LINK_SIZE("rsc.client.channel.pool.core.link.size",5),
    WAIT_QUEUE_SIZE("rsc.client.channel.pool.wait.queue.size",2),
    CORE_LINK_LIVE_TIME("rsc.client.channel.survive.size",5);

    private String configName;
    private int value;
    ConnectPoolConfigurations(String name, int value){
        this.configName = name;
        this.value = value;
    }
    public int getValue(){
        Integer configuration = getConfiguration();
        if (configuration!=null){
            return configuration;
        }
        return this.value;
    }
    public Integer getConfiguration(){
        //可扩展为配置
        return this.value;
    }
    ///**
    // * 核心链接数
    // */
    //public static final int CORE_LINK_SIZE = 20;
    ///**
    // * 最大连接数
    // */
    //public static final int MAX_LINK_SIZE = 100;
    ///**
    // * 等待队列允许的最大长度
    // */
    //public static final int QUEUE_MAX_SIZE = 100;
    ///**
    // * 核心链接存活时间 (ms)
    // */
    //public static final int CORE_LINK_LIVE_TIME = 1000 * 60 * 60;
    ///**
    // * 临时链接存活时间 (ms)
    // */
    //public static final int TEMP_LINK_LIVE_TIME = 1000 * 60 * 2;
}
