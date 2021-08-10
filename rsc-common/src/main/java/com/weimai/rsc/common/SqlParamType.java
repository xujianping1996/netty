package com.weimai.rsc.common;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * 预编译 sql 数据库级别参数类型
 * 主要针对方法参数、存储过程参数
 * @author DiZhi
 * @since 2021-08-10 16:42
 */
public enum SqlParamType {
    /**
     * 输入类型参数
     */
    IN(),
    /**
     * 输出类型参数
     */
    OUT(),
    /**
     * 既是输入也是输入类型参数
     */
    IN_OUT();

}
