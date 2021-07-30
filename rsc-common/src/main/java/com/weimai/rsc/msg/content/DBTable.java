package com.weimai.rsc.msg.content;

import java.io.Serializable;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-07-16 15:41
 */
public class DBTable implements Serializable {

    /**
     * 表头 二维数组  列名/类型
     */
    private Object [][] header ;
    /**
     * 数据集
     */
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
