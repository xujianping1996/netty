package com.weimai.rsc.executor.sql;

import java.util.Map;

import com.weimai.rsc.msg.Message;
import com.weimai.rsc.msg.request.SQL;
import io.netty.channel.Channel;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 批量sql执行类
 *
 * @author DiZhi
 * @since 2021-07-19 16:38
 */
public class SqlBatchExecuter extends AbstractNettySqlExecuter<Map<String,Object[][]>> implements  Runnable {

    protected SqlBatchExecuter(SQL sql, Channel channel, Long requestId) {
        super(sql, channel, requestId);
    }

    @Override
    public Map<String, Object[][]> execute() throws Exception {
        return null;
    }

    @Override
    protected Message result2Message(Map<String, Object[][]> stringMap) {
        return null;
    }

    @Override
    public void run() {

    }
}
