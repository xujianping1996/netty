package com.weimai.rsc.executor.sql;

import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Statement;
import java.sql.Types;

import com.weimai.rsc.msg.Message;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.ProtocolBody;
import com.weimai.rsc.msg.ProtocolHead;
import com.weimai.rsc.msg.content.SQL;
import io.netty.channel.Channel;
import io.netty.util.internal.StringUtil;

import static com.weimai.rsc.constant.ProtocolDataType.INT;
import static com.weimai.rsc.msg.content.SQL.INDEX;
import static com.weimai.rsc.msg.content.SQL.VALUE;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 增删改类sql执行对象
 *
 * @author DiZhi
 * @since 2021-07-19 15:10
 */
public class SqlUpdateExecuter extends AbstractNettySqlExecuter<Integer> implements Runnable {

    public SqlUpdateExecuter(SQL sql, Channel channel, String requestId) {
        super(sql, channel, requestId);
    }

    @Override
    public Integer execute() throws SQLException {
        Connection dbConnection = getConnection();
        SQL sql = getSql();
        if (sql == null || StringUtil.isNullOrEmpty(sql.getSqlLine())) {
            throw new RuntimeException("待执行sql语句为空！");
        }
        Statement statement;
        int result;
        if (sql.getParams() == null) {
            statement = dbConnection.createStatement();
            result = statement.executeUpdate(sql.getSqlLine());
        } else {
            statement = dbConnection.prepareStatement(sql.getSqlLine());
            Object[][] params = sql.getParams();
            for (Object[] param : params) {
                ((PreparedStatement)statement).setObject(Integer.parseInt(String.valueOf(param[INDEX])), param[VALUE]);
            }
            result = ((PreparedStatement)statement).executeUpdate();
        }
        statement.close();
        dbConnection.close();

        return result;
    }

    @Override
    protected Message result2Message(Integer updateRows) {
        byte[] targets = new byte[4];
        targets[3] = (byte)(updateRows & 0xFF);
        targets[2] = (byte)(updateRows >> 8 & 0xFF);
        targets[1] = (byte)(updateRows >> 16 & 0xFF);
        targets[0] = (byte)(updateRows >> 24 & 0xFF);
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
