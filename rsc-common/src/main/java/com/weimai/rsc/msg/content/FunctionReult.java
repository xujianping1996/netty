package com.weimai.rsc.msg.content;

import java.io.Serializable;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 数据库函数、存储过程执行结果类
 *
 * @author DiZhi
 * @since 2021-07-29 19:57
 */
public class FunctionReult implements Serializable {

    /**
     * OUT 类型参数结果集 一维是个数 [0] 参数在方法中的参数下标 [1] 结果值
     */
    Object[][] outParams;

    /**
     * 存储过程或方法返回的结果集
     */
    DBTable dbTable;

    public Object[][] getOutParams() {
        return outParams;
    }

    public void setOutParams(Object[][] outParams) {
        this.outParams = outParams;
    }

    public DBTable getDbTable() {
        return dbTable;
    }

    public void setDbTable(DBTable dbTable) {
        this.dbTable = dbTable;
    }
}
