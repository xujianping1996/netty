package com.weimai.rsc.clients;

import com.weimai.rsc.common.SqlParamType;
import com.weimai.rsc.enumeration.DataSourceIndex;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.MessageProtocolBody;

import static com.weimai.rsc.constant.ProtocolDataType.COMMAND_LINE_SQL_SELECT;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 数据库 Insert、Update、Delete 语句执行类
 *
 * @author DiZhi
 * @since 2021-08-10 9:24
 */
public class SqlUpdateClient extends AbstractClient<Integer> {

    public SqlUpdateClient(String ip, int port) {
        super(ip, port);
    }

    public SqlUpdateClient sql(String sql) {
        return sql(DataSourceIndex.DEFAULT_DATA_SOURCE_INDEX,sql);
    }

    public SqlUpdateClient sql(DataSourceIndex dataSource, String sql) {
        super.setDataSource(dataSource);
        super.setSql(sql, COMMAND_LINE_SQL_SELECT);
        return this;
    }

    public SqlUpdateClient param(Object obj) {
        super.setParam(obj, SqlParamType.IN, null);
        return this;
    }
    public Integer execute() {
        return super.execute();
    }
    @Override
    protected Integer convertProtocol2JavaObj(MessageProtocol messageProtocol) {
        MessageProtocolBody messageProtocolBody = messageProtocol.getProtocolBody();

        byte[] content = messageProtocolBody.getContent();
        int b0 = content[0] & 0xFF;
        int b1 = content[1] & 0xFF;
        int b2 = content[2] & 0xFF;
        int b3 = content[3] & 0xFF;
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }
}
