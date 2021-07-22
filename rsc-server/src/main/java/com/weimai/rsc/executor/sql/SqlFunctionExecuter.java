package com.weimai.rsc.executor.sql;

import java.sql.Connection;
import java.util.Map;

import com.weimai.rsc.msg.Message;
import com.zaxxer.hikari.HikariDataSource;
import io.netty.channel.Channel;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * 存储过程或方法sql执行者
 * @author DiZhi
 * @since 2021-07-19 16:37
 */
public class SqlFunctionExecuter extends AbstractNettySqlExecuter<Map<String,Object[][]>> implements  Runnable  {

    protected SqlFunctionExecuter(HikariDataSource hikariDataSource, String sql, Channel channel, String requestId) {
        super(hikariDataSource, sql, channel, requestId);
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
