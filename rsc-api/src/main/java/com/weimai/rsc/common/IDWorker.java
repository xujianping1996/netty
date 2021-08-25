package com.weimai.rsc.common;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * 雪花算法实现获取唯一请求id
 *
 * 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000
 * 1位，正整数占位
 * 46位，记录时间戳
 * 2位，机房id
 * 3位，机器id
 * 12位，同一毫秒内产生的id序列号
 * @author DiZhi
 * @since 2021-08-25 16:56
 */
public class IDWorker{


    public static final IDWorker ID_WORKER = new IDWorker();
    /**
     * 机房id
     */
    private static final long datacenterId = Long.parseLong(System.getProperty("datacenterId")==null?"2":System.getProperty("datacenterId"));
    /**
     * 具体服务id
     */
    private static final long workerId = Long.parseLong(System.getProperty("workerId")==null?"3":System.getProperty("workerId"));

    private static final long datacenterIdBits = 2L;
    private static final long workerIdBits = 3L;
    private static final long sequenceBits = 12L;

    private static final long maxDatacenterId = ~(-1L << datacenterIdBits);
    private static final long maxWorkerId = ~(-1L << workerIdBits);
    private static final long sequenceMask = ~(-1L << sequenceBits);



    static {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("启动参数 -DworkerId 错误，必须且在[0,7]范围内！",maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException("启动参数 -DdatacenterId 错误，必须且在[0,3]范围内！");
        }
    }
    private static long sequence = 0;
    private static final long twepoch = System.currentTimeMillis();
    private IDWorker(){
    }



    private long workerIdShift = sequenceBits;
    private long datacenterIdShift = sequenceBits + workerIdBits;
    private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;


    private long lastTimestamp = -1L;

    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                                                     lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << timestampLeftShift) |
               (datacenterId << datacenterIdShift) |
               (workerId << workerIdShift) |
               sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen(){
        return System.currentTimeMillis();
    }

}