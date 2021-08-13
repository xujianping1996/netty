package com.weimai.rsc.msg.request;

import java.io.Serializable;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 自定义协议请求体的命令行封装，这里为 sql 命令行的实现
 *
 * @author DiZhi
 * @since 2021-07-29 16:21
 */

public class SQL implements Command, Serializable {

    public static final int INDEX = 0;
    public static final int VALUE = 1;
    public static final int SQL_PARAM_TYPE = 2;
    public static final int PARAM_TYPE = 3;

    /**
     * 待执行sql命令行 允许是带有占位符的sql命令行
     */
    private String sqlLine;
    /**
     * 填充sql命令行的占位符参数
     * |----------------------------------------------|
     * |    index           |   index           | ... |
     * |----------------------------------------------|
     * |    value           |   value           | ... |
     * |----------------------------------------------|
     * |    sqlParamType    |   sqlParamType    | ... |
     * |----------------------------------------------|
     * |    paramType       |   paramType       | ... |
     * |----------------------------------------------|
     */
    private Object[][] params;

    public String getSqlLine() {
        return sqlLine;
    }

    public void setSqlLine(String sqlLine) {
        this.sqlLine = sqlLine;
    }

    public Object[][] getParams() {
        return params;
    }

    public void setParams(Object[][] params) {
        this.params = params;
    }
}
