package com.weimai.rsc;

import java.util.UUID;

import com.weimai.rsc.common.SqlParamType;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.ProtocolBody;
import com.weimai.rsc.msg.ProtocolHead;
import com.weimai.rsc.msg.content.SQL;
import com.weimai.rsc.msg.impl.MessageServiceImpl;
import com.weimai.rsc.util.HessianUtils;
import io.netty.channel.ChannelFuture;

import static com.weimai.rsc.msg.content.SQL.*;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * sql 执行客户端抽象类
 *
 * @author DiZhi
 * @since 2021-06-17 14:38
 */
public abstract class AbstractClient<T> {
    protected final NettyClient nettyClient = NettyClient.getSingleInstance();
    protected final MessageServiceImpl messageService = MessageServiceImpl.getSingleInstance();
    protected ChannelFuture channelFuture = null;
    protected final MessageProtocol messageProtocol = new MessageProtocol();

    protected String sql;
    protected byte sqlType;

    protected Object[][] param;
    protected int paramsLength = 0;
    protected int paramIndex = -1;

    protected static final String PLACEHOLDER = "?";

    public AbstractClient(String ip, int port) {
        try {
            channelFuture = nettyClient.connect(ip, port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        messageService.registerChannel(channelFuture);
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

    public T execute() {
        if (this.channelFuture == null) {
            throw new RuntimeException("未链接到远程服务器或已断开链接！");
        }
        convertMsg2Protocol();
        MessageProtocol messageProtocol = nettyClient.sendMessage(this.channelFuture, this.messageProtocol);
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
        String requestId = UUID.randomUUID().toString().replace("-", "");
        SQL sql = new SQL();
        sql.setSqlLine(this.sql);
        sql.setParams(param);
        //封装协议头
        ProtocolHead protocolHead = new ProtocolHead();
        protocolHead.setRequestId(requestId);
        protocolHead.setDataType(this.sqlType);
        //封装协议体
        ProtocolBody protocolBody = new ProtocolBody();
        protocolBody.setContent(HessianUtils.write(sql));
        //封装协议包
        messageProtocol.setProtocolBody(protocolBody);
        messageProtocol.setProtocolHead(protocolHead);
    }
}
