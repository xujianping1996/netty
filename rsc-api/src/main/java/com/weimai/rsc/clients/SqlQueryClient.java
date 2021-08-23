package com.weimai.rsc.clients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.weimai.rsc.AbstractClient;
import com.weimai.rsc.common.SqlParamType;
import com.weimai.rsc.enumeration.DataSourceIndex;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.MessageProtocolBody;
import com.weimai.rsc.msg.response.RespDbTable;
import com.weimai.rsc.msg.response.RespFunction;
import com.weimai.rsc.util.HessianUtils;

import static com.weimai.rsc.constant.ProtocolDataType.COMMAND_LINE_SQL_SELECT;
import static com.weimai.rsc.msg.request.SQL.*;
import static com.weimai.rsc.msg.response.RespDbTable.COLUMN_NAME;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 数据库 Select 语句执行类
 *
 * @author DiZhi
 * @since 2021-08-09 16:09
 */
public class SqlQueryClient extends AbstractClient<List<Map<String, String>>> {

    /**
     * 构造函数 设置当前链接的服务器ip和端口
     *
     * @param ip   服务器 ip
     * @param port 服务器端口
     */
    public SqlQueryClient(String ip, Integer port) {
        super(ip, port);
    }

    /**
     * 重写 sql(int dataSourceIndex, String sql) 方法，使用默认数据源，适用于服务端只有一个数据源的情况
     *
     * @param sql sql 语句
     * @return 当前对象 实现链式编程
     */
    public SqlQueryClient sql(String sql) {
        return sql(DataSourceIndex.DEFAULT_DATA_SOURCE_INDEX, sql);
    }

    /**
     * 设置待执行 sql 语句，以及执行 sql 的数据源
     *
     * @param dataSource 数据源序号
     * @param sql 待执行 sql 语句
     * @return 当前对象 实现链式编程
     */
    public SqlQueryClient sql(DataSourceIndex dataSource, String sql) {
        super.setDataSource(dataSource);
        super.setSql(sql, COMMAND_LINE_SQL_SELECT);
        return this;
    }

    /**
     * 设置预编译 sql 参数 如果 sql 不是预编译sql，则不应该调用该接口
     *
     * @param obj 传入的参数引用
     * @return 返回当前对象 实现链式编程
     */
    public SqlQueryClient param(Object obj) {
        super.setParam(obj, SqlParamType.IN, null);
        return this;
    }

    public List<Map<String, String>> execute() {
        return super.execute();
    }

    @Override
    protected List<Map<String, String>> convertProtocol2JavaObj(MessageProtocol messageProtocol) {
        MessageProtocolBody messageProtocolBody = messageProtocol.getProtocolBody();

        byte[] content = messageProtocolBody.getContent();
        RespDbTable read = HessianUtils.read(content, RespDbTable.class);
        Object[][] header = read.getHeader();
        Object[][] data = read.getData();
        ArrayList<Map<String, String>> objects = new ArrayList<>();
        for (Object[] datum : data) {
            HashMap<String, String> objectHashMap = new HashMap<>();
            for (int columnIndex = 0; columnIndex < header.length; columnIndex++) {
                String columnName = String.valueOf(header[columnIndex][COLUMN_NAME]);
                String obj = String.valueOf(datum[columnIndex]);
                objectHashMap.put(columnName, obj);
            }
            objects.add(objectHashMap);
        }
        return objects;
    }
}
