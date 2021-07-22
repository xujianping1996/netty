//package com.weimai.rsc.pool;
//
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.Executor;
//import java.util.concurrent.RejectedExecutionHandler;
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
///**
// * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
// * <p>
// * 发送消息线程池
// *
// * @author DiZhi
// * @since 2021-07-02 16:20
// */
//public class SendExecutorPool {
//    private static final ThreadPoolExecutor pool = getThreadPoolExecutor();
//
//
//    public static void execute(Runnable runnable){
//        pool.execute(runnable);
//    }
//
//
//    public static ThreadPoolExecutor getThreadPoolExecutor() {
//        return new ThreadPoolExecutor(100, 100, 300, TimeUnit.MINUTES, new ArrayBlockingQueue<>(50), r -> {
//            Thread thread = new Thread(r);
//            return thread;
//        }, (r, executor) -> System.out.println(((Thread)r).getName() + ":被拒绝了"));
//    }
//
//}
