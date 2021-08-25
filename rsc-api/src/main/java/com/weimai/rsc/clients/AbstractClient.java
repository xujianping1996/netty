package com.weimai.rsc.clients;

import com.weimai.rsc.common.IDWorker;
import com.weimai.rsc.common.SqlParamType;
import com.weimai.rsc.enumeration.DataSourceIndex;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.MessageProtocolBody;
import com.weimai.rsc.msg.MessageProtocolHead;
import com.weimai.rsc.msg.request.SQL;
import com.weimai.rsc.channel.ChannelGroup;
import com.weimai.rsc.channel.ChannelWrapper;
import com.weimai.rsc.util.HessianUtils;

import static com.weimai.rsc.msg.request.SQL.*;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * sql 执行客户端抽象模板类
 *
 * @author DiZhi
 * @since 2021-06-17 14:38
 */
public abstract class AbstractClient<T> {

    protected final ChannelWrapper channelWrapper ;
    protected final MessageProtocol messageProtocol = new MessageProtocol();

    private static final IDWorker ID_WORKER = IDWorker.ID_WORKER;

    protected String sql;
    protected byte sqlType;
    protected DataSourceIndex dataSource;

    protected Object[][] param;
    protected int paramsLength = 0;
    protected int paramIndex = -1;

    protected static final String PLACEHOLDER = "?";

    public AbstractClient(String ip, int port) {
        channelWrapper = ChannelGroup.getSingleInstance().getChannel(ip,port);
    }
    protected void setDataSource(DataSourceIndex dataSourceIndex){
        this.dataSource = dataSourceIndex;
    }
    protected void setSql(String sql, byte requestType) {
        this.sql = sql;
        this.sqlType = requestType;
        if (sql == null || "".equals(sql.trim())) {
            throw new RuntimeException("待执行sql语句不能为空！");
        }
        int count = 0;
        int index = 0;
        while ((index = sql.indexOf(PLACEHOLDER, index)) != -1) {
            index = index + PLACEHOLDER.length();
            count++;
        }
        param = new Object[count][4];
        paramsLength = count;
    }

    protected void setParam(Object obj, SqlParamType sqlParamType, Integer paramType) {
        if (param == null || param.length <= 0) {
            throw new RuntimeException("执行sql不是预编译sql或缺少必要占位符，无须设置参数！");
        }
        if (paramIndex >= paramsLength) {
            throw new RuntimeException("参数个数超出预编译占位符数！");
        }
        paramIndex++;
        param[paramIndex][INDEX] = paramIndex + 1;
        param[paramIndex][VALUE] = obj;
        param[paramIndex][SQL_PARAM_TYPE] = sqlParamType;
        param[paramIndex][PARAM_TYPE] = paramType;
    }

    protected T execute() {
        convertMsg2Protocol();
        MessageProtocol messageProtocol = channelWrapper.sendMessage(this.messageProtocol);
        return convertProtocol2JavaObj(messageProtocol);
    }

    /**
     * 将网络传输返回的协议对象解析成方便调用的 api 对象
     *
     * @param messageProtocol 协议对象
     * @return api java 对象，由具体实现类决定
     */
    protected abstract T convertProtocol2JavaObj(MessageProtocol messageProtocol);

    private void convertMsg2Protocol() {
        SQL sql = new SQL();
        sql.setDataSource(dataSource.getIndex());
        sql.setSqlLine(this.sql);
        sql.setParams(param);

        //封装协议头
        MessageProtocolHead messageProtocolHead = new MessageProtocolHead();
        messageProtocolHead.setRequestId(ID_WORKER.nextId());
        messageProtocolHead.setDataType(this.sqlType);
        //封装协议体
        MessageProtocolBody messageProtocolBody = new MessageProtocolBody();
        messageProtocolBody.setContent(HessianUtils.write(sql));
        //封装协议包
        messageProtocol.setProtocolBody(messageProtocolBody);
        messageProtocol.setProtocolHead(messageProtocolHead);
    }
}
