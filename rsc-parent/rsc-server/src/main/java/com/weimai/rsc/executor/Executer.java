package com.weimai.rsc.executor;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * 命令行执行接口
 * @author DiZhi
 * @since 2021-07-19 15:06
 */
public interface Executer<T> {
    T execute(String commandLine) throws Exception;
}
