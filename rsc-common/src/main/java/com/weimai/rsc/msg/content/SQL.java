package com.weimai.rsc.msg.content;

import java.io.Serializable;

import com.weimai.rsc.msg.Command;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-07-29 16:21
 */

public class SQL implements Command, Serializable {
    /**
     * 待执行sql命令行
     * 允许是带有占位符的sql命令行
     */
    private String sqlLine ;
    /**
     * 填充sql命令行的占位符参数
     * |---------------------|
     * | index | index | ... |
     * |---------------------|
     * | value | value | ... |
     * |---------------------|
     */
    private Object [][] inParams ;
    /**
     * 填充sql命令行的占位符参数
     * |----------------------|
     * | index  | index | ... |
     * |----------------------|
     * | type   | type | ... |
     * |----------------------|
     */
    private Object [][] outParams ;
    /**
     * 填充sql命令行的占位符参数
     * |----------------------|
     * | index  | index | ... |
     * |----------------------|
     * | value  | value | ... |
     * |----------------------|
     * | type   | type  | ... |
     * |----------------------|
     */
    private Object [][] inOutParams;

    public String getSqlLine() {
        return sqlLine;
    }

    public void setSqlLine(String sqlLine) {
        this.sqlLine = sqlLine;
    }

    public Object[][] getInParams() {
        return inParams;
    }

    public void setInParams(Object[][] inParams) {
        this.inParams = inParams;
    }

    public Object[][] getOutParams() {
        return outParams;
    }

    public void setOutParams(Object[][] outParams) {
        this.outParams = outParams;
    }

    public Object[][] getInOutParams() {
        return inOutParams;
    }

    public void setInOutParams(Object[][] inOutParams) {
        this.inOutParams = inOutParams;
    }
}
