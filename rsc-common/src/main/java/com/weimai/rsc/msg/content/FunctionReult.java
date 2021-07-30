package com.weimai.rsc.msg.content;

import java.io.Serializable;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-07-29 19:57
 */
public class FunctionReult implements Serializable {

    Object[][] outParams;
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
