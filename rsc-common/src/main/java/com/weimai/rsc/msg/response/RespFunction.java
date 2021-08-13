package com.weimai.rsc.msg.response;

import java.io.Serializable;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 数据库函数、存储过程执行结果类
 *
 * @author DiZhi
 * @since 2021-07-29 19:57
 */
public class RespFunction implements Serializable {

    /**
     * OUT 类型参数结果集 一维是个数 [0] 参数在方法中的参数下标 [1] 结果值
     */
    Object[][] outParams;

    /**
     * 存储过程或方法返回的结果集
     */
    RespDbTable respDbTable;

    public Object[][] getOutParams() {
        return outParams;
    }

    public void setOutParams(Object[][] outParams) {
        this.outParams = outParams;
    }

    public RespDbTable getDbTable() {
        return respDbTable;
    }

    public void setDbTable(RespDbTable respDbTable) {
        this.respDbTable = respDbTable;
    }
}
