package com.weimai.rsc.executor.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

import com.weimai.rsc.common.SqlParamType;
import com.weimai.rsc.msg.Message;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.MessageProtocolBody;
import com.weimai.rsc.msg.MessageProtocolHead;
import com.weimai.rsc.msg.response.RespDbTable;
import com.weimai.rsc.msg.response.RespFunction;
import com.weimai.rsc.msg.request.SQL;
import com.weimai.rsc.util.Collections;
import com.weimai.rsc.util.HessianUtils;
import io.netty.channel.Channel;
import io.netty.util.internal.StringUtil;

import static com.weimai.rsc.constant.ProtocolDataType.FUNCTION_DATA;
import static com.weimai.rsc.msg.request.SQL.INDEX;
import static com.weimai.rsc.msg.request.SQL.PARAM_TYPE;
import static com.weimai.rsc.msg.request.SQL.SQL_PARAM_TYPE;
import static com.weimai.rsc.msg.request.SQL.VALUE;
import static com.weimai.rsc.msg.response.RespDbTable.COLUMN_NAME;
import static com.weimai.rsc.msg.response.RespDbTable.COLUMN_TYPE;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential. 存储过程或方法sql执行者
 *
 * @author DiZhi
 * @since 2021-07-19 16:37
 */
public class SqlFunctionExecuter extends AbstractNettySqlExecuter<Object[]> implements Runnable {

    public SqlFunctionExecuter(SQL sql, Channel channel, Long requestId) {
        super(sql, channel, requestId);
    }

    @Override
    public Object[] execute() throws Exception {

        SQL sql = getSql();
        Connection dbConnection = getConnection();
        if (sql == null || StringUtil.isNullOrEmpty(sql.getSqlLine())) {
            throw new RuntimeException("待执行sql语句为空！");
        }
        CallableStatement callableStatement = dbConnection.prepareCall(sql.getSqlLine());
        Object[][] params = sql.getParams();
        ArrayList<Integer> indexs = new ArrayList<>();
        int outParamCount = 0;
        if (Collections.isEmpty(params)) {
            for (Object[] param : params) {
                int index = Integer.parseInt(String.valueOf(param[INDEX]));
                if (SqlParamType.IN.equals(param[SQL_PARAM_TYPE])) {
                    callableStatement.setObject(index, param[VALUE]);
                } else if (SqlParamType.OUT.equals(param[SQL_PARAM_TYPE])) {

                    callableStatement.registerOutParameter(index, Integer.parseInt(String.valueOf(PARAM_TYPE)));
                    indexs.add(index);
                    outParamCount++;
                } else if (SqlParamType.IN_OUT.equals(param[SQL_PARAM_TYPE])) {
                    callableStatement.setObject(index, param[VALUE]);
                    callableStatement.registerOutParameter(index, Integer.parseInt(String.valueOf(param[PARAM_TYPE])));
                    indexs.add(index);
                    outParamCount++;
                }
            }
        }
        Object[][] outs = new Object[outParamCount][2];
        ResultSet resultSet = callableStatement.executeQuery();
        Object[][][] objects = null;
        resultSet.last(); //移到最后一行
        int rows = resultSet.getRow(); //得到当前行号，也就是记录数
        if (rows > 0) {
            resultSet.beforeFirst();
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
            Object[][] tableData = new Object[rows][columns];
            while (resultSet.next()) {
                int index = -1;
                while (resultSet.next()) {
                    index++;
                    Object[] dataRow = new Object[columns];
                    for (int i = 0; i < columns; i++) {
                        dataRow[i] = resultSet.getObject(i + 1);
                    }
                    tableData[index] = dataRow;
                }
            }
            objects = new Object[][][] {tableHeaders, tableData};
        }
        if (outParamCount > 0) {
            for (int i = 0; i < indexs.size(); i++) {
                Integer integer = indexs.get(i);
                outs[i][INDEX] = indexs.get(integer);
                outs[i][VALUE]  = callableStatement.getObject(indexs.get(integer));
            }
        }

        resultSet.close();
        callableStatement.close();
        dbConnection.close();
        return new Object[] {objects, outs};
    }

    @Override
    protected Message result2Message(Object[] stringMap) {
        RespFunction respFunction = new RespFunction();
        Object tableData = stringMap[0];
        Object outParamData = stringMap[1];
        if (outParamData != null) {
            respFunction.setOutParams((Object[][])outParamData);
        }
        if (tableData != null) {
            RespDbTable respDbTable = new RespDbTable();
            respDbTable.setHeader(((Object[][][])tableData)[0]);
            respDbTable.setData(((Object[][][])tableData)[1]);
            respFunction.setDbTable(respDbTable);
        }
        MessageProtocolHead messageProtocolHead = new MessageProtocolHead();
        messageProtocolHead.setRequestId(getRequestId());
        messageProtocolHead.setDataType(FUNCTION_DATA);
        MessageProtocolBody messageProtocolBody = new MessageProtocolBody();
        messageProtocolBody.setContent(HessianUtils.write(respFunction));
        MessageProtocol messageProtocol = new MessageProtocol();
        messageProtocol.setProtocolHead(messageProtocolHead);
        messageProtocol.setProtocolBody(messageProtocolBody);
        return messageProtocol;
    }

    @Override
    public void run() {
        toSendMessageToClient();
    }
}
