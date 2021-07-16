package com.weimai.rsc.db.executor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.weimai.rsc.common.Serializer;
import com.weimai.rsc.db.config.DBConfig;
import com.weimai.rsc.db.repository.SransferStation;
import com.weimai.rsc.msg.DBTable;
import com.weimai.rsc.msg.Message;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.ProtocolBody;
import com.weimai.rsc.msg.ProtocolHead;
import com.weimai.rsc.util.HessianUtils;
import com.zaxxer.hikari.HikariDataSource;
import io.netty.channel.Channel;

import static com.weimai.rsc.constant.ProtocolDataType.TABLE;
import static com.weimai.rsc.constant.TableColumnConstant.COLUMN_NAME;
import static com.weimai.rsc.constant.TableColumnConstant.COLUMN_TYPE;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-07-13 19:30
 */
public class ServerExecuter implements Runnable, SransferStation {
    private final HikariDataSource hikariDataSource;
    private final String sql;
    private final Channel channel;
    private final String requestId;

    public ServerExecuter(String sql, Channel channel,String requestId) {
        this.sql = sql;
        this.hikariDataSource = DBConfig.hikariDataSource();
        this.channel = channel;
        this.requestId = requestId;
    }

    @Override
    public void run() {
        //hikariDataSource.
        Connection connection = null;
        Statement statement = null ;
        ResultSet resultSet = null;
        try {
            connection = hikariDataSource.getConnection();
            statement = connection.createStatement();
             resultSet = statement.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columns = metaData.getColumnCount();
            Object[][] tableHeaders = new Object[columns][];
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i + 1);
                int columnType = metaData.getColumnType(i + 1);
                Object[] tableHeader = new Object[2];
                tableHeader[COLUMN_NAME] = columnName;
                tableHeader[COLUMN_TYPE] = columnType;
                tableHeaders[i] = tableHeader;
            }

            int row = resultSet.getRow();
            Object[][] tableData = new Object[row][columns];
            int index = -1;
            while (resultSet.next()) {
                index ++;
                Object [] dataRow = new Object[columns];
                for (int i = 0; i < columns; i++) {
                    dataRow[i] = resultSet.getObject(i);
                }
                tableData[index] = dataRow;
            }
            DBTable dbTable = new DBTable();
            dbTable.setHeader(tableHeaders);
            dbTable.setData(tableData);
            ProtocolHead protocolHead = new ProtocolHead();
            protocolHead.setRequestId(this.requestId);
            protocolHead.setDataType(TABLE);
            ProtocolBody protocolBody = new ProtocolBody();
            protocolBody.setContent(HessianUtils.write(dbTable));
            MessageProtocol messageProtocol = new MessageProtocol();
            messageProtocol.setProtocolHead(protocolHead);
            messageProtocol.setProtocolBody(protocolBody);
            sendMessageToClient(channel,messageProtocol);

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {

            try {
                assert resultSet != null;
                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    @Override
    public void sendMessageToClient(Channel channel, Message message) {
        channel.writeAndFlush(message);
    }
}
