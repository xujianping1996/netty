package com.weimai.rsc.executor.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.weimai.rsc.executor.sql.AbstractNettySqlExecuter;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 用户线程池
 *
 * @author DiZhi
 * @since 2021-07-20 13:52
 */
public class CommandLineExecPool {

    private static final Executor EXECUTOR = new ThreadPoolExecutor(50, 100, 30, TimeUnit.SECONDS,
                                                                    new ArrayBlockingQueue<>(20), r -> {
        Thread thread = new Thread(r);
        thread.setName("commandLineExecPoolThread--");
        return thread;
    }, (r, executor) -> {
        Long requestId = null;
        if (r instanceof AbstractNettySqlExecuter) {
            requestId = ((AbstractNettySqlExecuter<?>)r).getRequestId();
        }
        throw new RuntimeException("网络繁忙，请求：" + requestId + " 被拒绝！");

    });

    private CommandLineExecPool() {
    }

    public static void submit(Runnable runnable) {
        EXECUTOR.execute(runnable);
    }
}
