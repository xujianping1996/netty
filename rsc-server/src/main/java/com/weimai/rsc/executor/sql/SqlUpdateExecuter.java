package com.weimai.rsc.executor.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.weimai.rsc.db.datasource.config.DBConfig;
import com.weimai.rsc.msg.DBTable;
import com.weimai.rsc.msg.Message;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.ProtocolBody;
import com.weimai.rsc.msg.ProtocolHead;
import com.weimai.rsc.util.HessianUtils;
import com.zaxxer.hikari.HikariDataSource;
import io.netty.channel.Channel;

import static com.weimai.rsc.constant.ProtocolDataType.INT;
import static com.weimai.rsc.constant.ProtocolDataType.TABLE;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 增删改类sql执行对象
 *
 * @author DiZhi
 * @since 2021-07-19 15:10
 */
public class SqlUpdateExecuter  extends AbstractNettySqlExecuter<Integer> implements  Runnable  {

    public SqlUpdateExecuter(String sql, Channel channel, String requestId) {
        super(DBConfig.hikariDataSource(), sql, channel, requestId);
    }

    @Override
    protected Integer toExecuteCommandLine(Connection dbConnection, String commandLine)
            throws SQLException {
        Statement statement = dbConnection.createStatement();
        int i = statement.executeUpdate(commandLine);
        statement.close();
        dbConnection.close();

        return i;
    }

    @Override
    protected Message result2Message(Integer updateRows) {
        byte[] targets = new byte[4];
        targets[3] = (byte) (updateRows & 0xFF);
        targets[2] = (byte) (updateRows >> 8 & 0xFF);
        targets[1] = (byte) (updateRows >> 16 & 0xFF);
        targets[0] = (byte) (updateRows >> 24 & 0xFF);
        ProtocolHead protocolHead = new ProtocolHead();

        protocolHead.setRequestId(getRequestId());
        protocolHead.setDataType(INT);
        ProtocolBody protocolBody = new ProtocolBody();
        protocolBody.setContent(targets);
        MessageProtocol messageProtocol = new MessageProtocol();
        messageProtocol.setProtocolHead(protocolHead);
        messageProtocol.setProtocolBody(protocolBody);

        return messageProtocol;
    }

    @Override
    public void run() {
        toSendMessageToClient();
    }
}
