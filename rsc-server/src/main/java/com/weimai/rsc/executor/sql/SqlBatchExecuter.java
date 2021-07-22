package com.weimai.rsc.executor.sql;

import java.sql.Connection;
import java.util.Map;

import com.weimai.rsc.db.datasource.config.DBConfig;
import com.weimai.rsc.msg.Message;
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

    protected SqlBatchExecuter(String sql, Channel channel, String requestId) {
        super(DBConfig.hikariDataSource(), sql, channel, requestId);
    }

    @Override
    protected Map<String, Object[][]> toExecuteCommandLine(Connection dbConnection, String commandLine) {
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
