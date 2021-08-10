package com.weimai.rsc.msg.content;

import java.io.Serializable;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 自定义协议数据库查询结果集的封装类，模拟表结构
 *
 * @author DiZhi
 * @since 2021-07-16 15:41
 */
public class DBTable implements Serializable {

    public static int COLUMN_NAME = 0;
    public static int COLUMN_TYPE = 1;

    /**
     * 表头 二维数组  列名(0)/类型(1)
     */
    private Object[][] header;
    /**
     * 数据集
     */
    private Object[][] data;

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
