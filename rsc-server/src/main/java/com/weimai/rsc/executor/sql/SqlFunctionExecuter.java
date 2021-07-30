package com.weimai.rsc.executor.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import com.weimai.rsc.msg.Message;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.ProtocolBody;
import com.weimai.rsc.msg.ProtocolHead;
import com.weimai.rsc.msg.content.DBTable;
import com.weimai.rsc.msg.content.FunctionReult;
import com.weimai.rsc.msg.content.SQL;
import com.weimai.rsc.util.Collections;
import com.weimai.rsc.util.HessianUtils;
import io.netty.channel.Channel;
import io.netty.util.internal.StringUtil;

import static com.weimai.rsc.constant.ProtocolDataType.FUNCTION_DATA;
import static com.weimai.rsc.constant.TableColumnConstant.COLUMN_NAME;
import static com.weimai.rsc.constant.TableColumnConstant.COLUMN_TYPE;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential. 存储过程或方法sql执行者
 *
 * @author DiZhi
 * @since 2021-07-19 16:37
 */
public class SqlFunctionExecuter extends AbstractNettySqlExecuter<Object[]> implements Runnable {

    public SqlFunctionExecuter(SQL sql, Channel channel, String requestId) {
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
        Object[][] inParams = sql.getInParams();
        int outParamCount = 0;
        if (Collections.isEmpty(inParams)) {
            for (Object[] inParam : inParams) {
                callableStatement.setObject(Integer.parseInt(String.valueOf(inParam[0])), inParam[1]);
            }
        }
        Object[][] outParams = sql.getOutParams();
        if (Collections.isEmpty(outParams)) {
            for (Object[] outParam : outParams) {
                callableStatement.registerOutParameter(Integer.parseInt(String.valueOf(outParam[0])),
                                                       Integer.parseInt(String.valueOf(outParam[1])));
                outParamCount++;
            }
        }
        Object[][] inOutParams = sql.getInOutParams();
        if (Collections.isEmpty(inOutParams)) {
            for (int i = 0; i < inOutParams.length; i++) {
                callableStatement.setObject(Integer.parseInt(String.valueOf(inOutParams[i][0])), inOutParams[i][1]);
                callableStatement.registerOutParameter(Integer.parseInt(String.valueOf(outParams[i][0])),
                                                       Integer.parseInt(String.valueOf(outParams[i][2])));
                outParamCount++;
            }
        }
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
        Object[][] resultParamObjects = null;
        if (outParamCount > 0) {
            resultParamObjects = new Object[outParamCount][];
            int tempIndex = 0;
            if (Collections.isEmpty(outParams)) {
                for (Object[] outParam : outParams) {
                    Object o = callableStatement.getObject(Integer.parseInt(String.valueOf(outParam[0])));
                    resultParamObjects[tempIndex] = new Object[] {outParam[0], o};
                    tempIndex++;
                }
            }
            if (Collections.isEmpty(inOutParams)) {
                for (Object[] inOutParam : inOutParams) {
                    Object o = callableStatement.getObject(Integer.parseInt(String.valueOf(inOutParam[0])));
                    resultParamObjects[tempIndex] = new Object[] {inOutParam[0], o};
                    tempIndex++;
                }
            }
        }

        resultSet.close();
        callableStatement.close();
        dbConnection.close();
        return new Object[] {objects, resultParamObjects};
    }

    @Override
    protected Message result2Message(Object[] stringMap) {
        FunctionReult functionReult = new FunctionReult();
        Object tableData = stringMap[0];
        Object outParamData = stringMap[1];
        if (outParamData != null) {
            functionReult.setOutParams((Object[][])outParamData);
        }
        if (tableData != null) {
            DBTable dbTable = new DBTable();
            dbTable.setHeader(((Object[][][])tableData)[0]);
            dbTable.setData(((Object[][][])tableData)[1]);
            functionReult.setDbTable(dbTable);
        }
        ProtocolHead protocolHead = new ProtocolHead();
        protocolHead.setRequestId(getRequestId());
        protocolHead.setDataType(FUNCTION_DATA);
        ProtocolBody protocolBody = new ProtocolBody();
        protocolBody.setContent(HessianUtils.write(functionReult));
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
