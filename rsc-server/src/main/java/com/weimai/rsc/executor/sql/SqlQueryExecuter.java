package com.weimai.rsc.executor.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import com.weimai.rsc.db.datasource.config.DBConfig;
import com.weimai.rsc.msg.DBTable;
import com.weimai.rsc.msg.Message;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.ProtocolBody;
import com.weimai.rsc.msg.ProtocolHead;
import com.weimai.rsc.util.HessianUtils;
import com.zaxxer.hikari.HikariDataSource;
import io.netty.channel.Channel;
import io.netty.util.internal.StringUtil;

import static com.weimai.rsc.constant.ProtocolDataType.TABLE;
import static com.weimai.rsc.constant.TableColumnConstant.COLUMN_NAME;
import static com.weimai.rsc.constant.TableColumnConstant.COLUMN_TYPE;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 查询类sql执行对象
 *
 * @author DiZhi
 * @since 2021-07-19 15:09
 */
public class SqlQueryExecuter extends AbstractNettySqlExecuter<Object[][][]> implements Runnable {

    public SqlQueryExecuter( String sql, Channel channel, String requestId) {
        super(DBConfig.hikariDataSource(), sql, channel, requestId);
    }

    @Override
    protected Object[][][] toExecuteCommandLine(Connection dbConnection, String commandLine) throws Exception {
        if (StringUtil.isNullOrEmpty(commandLine)) {
            throw new RuntimeException("待执行sql语句为空！");
        }

        Object[][][] abc = new Object[1][][];
        Statement statement = dbConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(commandLine);
        //statement.executeLargeUpdate()
        //statement.execute
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

        //int row = resultSet.getRow();
        resultSet.last(); //移到最后一行
        int row = resultSet.getRow(); //得到当前行号，也就是记录数
        resultSet.beforeFirst(); //还要用到记录集，就把指针再移到初始化的位置
        Object[][] tableData = new Object[row][columns];
        int index = -1;
        while (resultSet.next()) {
            index++;
            Object[] dataRow = new Object[columns];
            for (int i = 0; i < columns; i++) {
                dataRow[i] = resultSet.getObject(i + 1);
            }
            tableData[index] = dataRow;
        }
        resultSet.close();
        statement.close();
        dbConnection.close();
        return new Object[][][] {tableHeaders, tableData};
    }

    @Override
    protected Message result2Message(Object[][][] stringMap) {
        DBTable dbTable = new DBTable();
        dbTable.setHeader(stringMap[0]);
        dbTable.setData(stringMap[1]);
        ProtocolHead protocolHead = new ProtocolHead();
        protocolHead.setRequestId(getRequestId());
        protocolHead.setDataType(TABLE);
        ProtocolBody protocolBody = new ProtocolBody();
        protocolBody.setContent(HessianUtils.write(dbTable));
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
