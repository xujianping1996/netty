package com.weimai.rsc.constant;

import com.weimai.rsc.msg.response.RespDbTable;
import com.weimai.rsc.msg.response.RespFunction;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * 协议体数据类型
 * @author DiZhi
 * @since 2021-07-16 15:38
 */
public class ProtocolDataType {


    /**
     * 请求-命令行类型-查询类 sql
     */
    public static final byte COMMAND_LINE_SQL_SELECT = 100;
    /**
     * 请求-命令行类型-修改类（增、删、改） sql
     */
    public static final byte COMMAND_LINE_SQL_UPDATE = 101;
    /**
     * 请求-命令行类型-存储过程、函数类 sql
     */
    public static final byte COMMAND_LINE_SQL_FUNCTION = 102;
    /**
     * 请求-命令行类型-批量 sql
     */
    public static final byte COMMAND_LINE_SQL_BATCH = 103;





    /**
     * 响应-响应体内容类型-{@link java.lang.Integer int} 类型
     */
    public static final byte INT = 110;
    /**
     * 响应-响应体内容类型-{@link RespDbTable DBTable} 类型
     */
    public static final byte TABLE = 111;
    /**
     * 响应-响应体内容类型-{@link String String} 类型
     */
    public static final byte STRING = 112;
    /**
     * 响应-响应体内容类型-{@link RespFunction RespFunction} 类型
     */
    public static final byte FUNCTION_DATA = 113;



    /**
     * 响应-响应体内容类型-{@link Exception Exception} 类型 异常、错误类型，主要为错误内容描述
     */
    public static final byte ERROR = 0;


}
