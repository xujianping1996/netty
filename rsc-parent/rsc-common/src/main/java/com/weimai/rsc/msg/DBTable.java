package com.weimai.rsc.msg;

import java.io.Serializable;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-07-16 15:41
 */
public class DBTable implements Serializable {

    private Object [][] header ;
    private Object [][] data;

    public Object[][] getHeader() {
        return header;
    }

    public void setHeader(Object[][] header) {
        this.header = header;
    }

    public Object[][] getData() {
        return data;
    }

    public void setData(Object[][] data) {
        this.data = data;
    }
}
