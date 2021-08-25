package com.weimai.rsc.clients;

import java.math.BigDecimal;
import java.sql.Types;

import com.weimai.rsc.common.SqlParamType;
import com.weimai.rsc.enumeration.DataSourceIndex;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.MessageProtocolBody;
import com.weimai.rsc.msg.response.RespFunction;
import com.weimai.rsc.util.HessianUtils;

import static com.weimai.rsc.constant.ProtocolDataType.COMMAND_LINE_SQL_SELECT;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 数据库函数或存储过程执行类
 *
 * @author DiZhi
 * @since 2021-08-10 16:25
 */
public class SqlFunctionClient extends AbstractClient<RespFunction> {

    /**
     * 构造函数 设置当前链接的服务器ip和端口
     *
     * @param ip   服务器 ip
     * @param port 服务器端口
     */
    public SqlFunctionClient(String ip, int port) {
        super(ip, port);
    }

    /**
     * 设置待执行 sql 语句 ,重写 sql(DataSourceIndex dataSource, String sql) 方法，使用默认数据源
     *
     * @param sql sql 语句
     * @return 当前对象 实现链式编程
     */
    public SqlFunctionClient sql(String sql) {
        return sql(DataSourceIndex.DEFAULT_DATA_SOURCE_INDEX,sql);
    }
    /**
     * 设置待执行 sql 语句
     *
     * @param sql sql 语句
     * @return 当前对象 实现链式编程
     */
    public SqlFunctionClient sql(DataSourceIndex dataSource, String sql) {
        super.setDataSource(dataSource);
        super.setSql(sql, COMMAND_LINE_SQL_SELECT);
        return this;
    }
    /**
     * 设置输入参数 调用顺序要严格按照待执行的预编译 sql 的占位符 “?” 顺序调用 如待执行 sql 是没有占位符的完整 sql 则不应该调用该方法
     *
     * @param obj 参数引用
     * @return 当前对象 实现链式编程
     */
    public SqlFunctionClient setInParam(Object obj) {
        super.setParam(obj, SqlParamType.IN, null);
        return this;
    }

    /**
     * 设置输出参数 调用顺序要严格按照待执行的预编译 sql 的占位符 “?” 顺序调用 如待执行 sql 是没有占位符的完整 sql 则不应该调用该方法
     *
     * @param obj 参数引用
     * @return 当前对象 实现链式编程
     */
    public SqlFunctionClient setOutParam(Object obj) {
        return setOutParam(obj, getJdbcType(obj));
    }

    /**
     * 设置输出参数 重写 setOutParam(Object obj) 方法，为满足传入对象无法设置初始值或当前版本不兼容的数据类型时又调用方自行选择 jdbc 数据类型
     *
     * @param obj  参数引用
     * @param type jdbc 数据类型，具体可查看 {@link java.sql.Types Types}
     * @return 当前对象 实现链式编程
     */
    public SqlFunctionClient setOutParam(Object obj, int type) {
        super.setParam(obj, SqlParamType.OUT, type);
        return this;
    }

    /**
     * 设置参数 调用顺序要严格按照待执行的预编译 sql 的占位符 “?” 顺序调用 如待执行 sql 是没有占位符的完整 sql 则不应该调用该方法
     *
     * @param obj 参数引用
     * @return 当前对象 实现链式编程
     */
    public SqlFunctionClient setInOutParam(Object obj) {
        return setInOutParam(obj, getJdbcType(obj));
    }

    /**
     * 设置参数 重写 setInOutParam(Object obj) 方法，为满足传入对象无法设置初始值或当前版本不兼容的数据类型时又调用方自行选择 jdbc 数据类型
     *
     * @param obj 参数引用
     * @return 当前对象 实现链式编程
     */
    public SqlFunctionClient setInOutParam(Object obj, int type) {
        super.setParam(obj, SqlParamType.IN_OUT, type);
        return this;
    }

    @Override
    protected RespFunction convertProtocol2JavaObj(MessageProtocol messageProtocol) {
        MessageProtocolBody messageProtocolBody = messageProtocol.getProtocolBody();
        byte[] content = messageProtocolBody.getContent();
        return HessianUtils.read(content, RespFunction.class);
    }
    public RespFunction execute() {
        return super.execute();
    }
    private static int getJdbcType(Object o) {

        if (o instanceof String) {
            return Types.VARCHAR;
        } else if (o instanceof BigDecimal) {
            return Types.NUMERIC;
        } else if (o instanceof java.sql.Date) {
            return Types.DATE;
        } else if (o instanceof java.sql.Timestamp) {
            return Types.TIMESTAMP;
        } else if (o instanceof java.sql.Time) {
            return Types.TIME;
        } else if (o instanceof Boolean) {
            return Types.BIT;
        } else if (o instanceof Integer) {
            return Types.INTEGER;
        } else if (o instanceof Long) {
            return Types.BIGINT;
        } else if (o instanceof Float) {
            return Types.REAL;
        } else if (o instanceof Double) {
            return Types.DOUBLE;
        } else if (o instanceof byte[]) {
            return Types.VARBINARY;
        } else {
            throw new RuntimeException("参数需指明类型，并赋初始值！否则请使用带有类型参数的 api！");
        }
    }
}
