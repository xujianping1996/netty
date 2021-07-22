package com.weimai.rsc.executor.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.weimai.rsc.db.datasource.config.DBConfig;
import com.weimai.rsc.db.repository.SransferStation;
import com.weimai.rsc.executor.Executer;
import com.weimai.rsc.msg.Message;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.ProtocolBody;
import com.weimai.rsc.msg.ProtocolHead;
import com.weimai.rsc.util.HessianUtils;
import com.zaxxer.hikari.HikariDataSource;
import io.netty.channel.Channel;

import static com.weimai.rsc.constant.ProtocolDataType.ERROR;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * sql语句执行抽象类
 *
 * @author DiZhi
 * @since 2021-07-19 17:04
 */
public abstract  class  AbstractNettySqlExecuter<T> implements Executer<T>, SransferStation {

    private final HikariDataSource hikariDataSource;
    private final String sql;
    private final Channel channel;
    private final String requestId;

    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }

    public String getSql() {
        return sql;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getRequestId() {
        return requestId;
    }

    protected AbstractNettySqlExecuter(HikariDataSource hikariDataSource, String sql, Channel channel, String requestId) {
        this.sql = sql;
        this.hikariDataSource = DBConfig.hikariDataSource();
        this.channel = channel;
        this.requestId = requestId;
    }

    @Override
    public void sendMessageToClient(Message message) {
        this.channel.writeAndFlush(message);
    }



    @Override
    public T execute(String commandLine) throws Exception {
        return toExecuteCommandLine(getConnection(), commandLine);
    }

    protected abstract T toExecuteCommandLine(Connection dbConnection,String commandLine )throws Exception;

    protected abstract Message result2Message(T t);

    private Message formatErrorMessage(Exception e){
        ProtocolHead protocolHead = new ProtocolHead();
        protocolHead.setRequestId(this.requestId);
        protocolHead.setDataType(ERROR);
        ProtocolBody protocolBody = new ProtocolBody();
        protocolBody.setContent(HessianUtils.write(e.getStackTrace()));
        MessageProtocol messageProtocol = new MessageProtocol();
        messageProtocol.setProtocolHead(protocolHead);
        messageProtocol.setProtocolBody(protocolBody);
        return messageProtocol;
    };
    protected Connection getConnection() throws SQLException {
        return this.hikariDataSource.getConnection();
    }

    protected void toSendMessageToClient(){
        T execute = null;
        Message message = null;
        try {
            execute = this.execute(this.sql);
            message = this.result2Message(execute);
        }catch (Exception e){
            System.out.println("执行sql异常，异常信息"+e.getMessage());
            message = formatErrorMessage(e);
        }
        this.sendMessageToClient(message);
    }
}
