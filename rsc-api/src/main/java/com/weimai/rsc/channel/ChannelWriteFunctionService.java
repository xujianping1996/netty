package com.weimai.rsc.channel;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * netty 写数据的函数式接口
 *
 * @author DiZhi
 * @since 2021-08-25 18:54
 */
@FunctionalInterface
public interface ChannelWriteFunctionService {
    void write();
}
