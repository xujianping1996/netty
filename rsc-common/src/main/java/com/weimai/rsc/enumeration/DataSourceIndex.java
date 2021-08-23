package com.weimai.rsc.enumeration;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 数据源序号
 *
 * @author DiZhi
 * @since 2021-08-23 20:17
 */
public enum DataSourceIndex {
    /**
     * 默认数据源
     */
    DEFAULT_DATA_SOURCE_INDEX(0),
    /**
     * 第二数据源
     */
    SECOND_DATA_SOURCE_INDEX(1),
    /**
     * 第三数据源
     */
    THIRD_DATA_SOURCE_INDEX(2),
    /**
     * 第四数据源
     */
    FOURTH_DATA_SOURCE_INDEX(3),
    /**
     * 第五数据源
     */
    FIFTH_DATA_SOURCE_INDEX(4),
    /**
     * 第六数据源
     */
    SIXTH_DATA_SOURCE_INDEX(5),
    /**
     * 第七数据源
     */
    SEVENTH_DATA_SOURCE_INDEX(6);


    private int index;
    DataSourceIndex(int index){
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
