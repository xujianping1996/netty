package com.weimai.rsc.executor.sql;

import java.sql.Connection;
import java.sql.SQLException;

import com.weimai.rsc.db.datasource.config.DBConnPoolFactory;
import com.weimai.rsc.db.repository.SransferStation;
import com.weimai.rsc.executor.Executer;

import com.weimai.rsc.log.InternalLogger;
import com.weimai.rsc.log.InternalLoggerFactory;
import com.weimai.rsc.msg.Message;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.MessageProtocolBody;
import com.weimai.rsc.msg.MessageProtocolHead;
import com.weimai.rsc.msg.request.SQL;
import com.weimai.rsc.util.HessianUtils;
import com.zaxxer.hikari.HikariDataSource;
import io.netty.channel.Channel;
import sun.util.locale.provider.AvailableLanguageTags;

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

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(AbstractNettySqlExecuter.class);

    private final HikariDataSource hikariDataSource;
    private final SQL sql;
    private final Channel channel;
    private final long requestId;

    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }

    public SQL getSql() {
        return sql;
    }

    public Channel getChannel() {
        return channel;
    }

    public Long getRequestId() {
        return requestId;
    }

    protected AbstractNettySqlExecuter(SQL sql, Channel channel, Long requestId) {
        this.sql = sql;
        this.hikariDataSource = DBConnPoolFactory.dataSource(sql.getDataSource());
        this.channel = channel;
        this.requestId = requestId;
    }

    @Override
    public void sendMessageToClient(Message message) {
        this.channel.writeAndFlush(message);
    }



    @Override
    public abstract T execute() throws Exception;

    protected abstract Message result2Message(T t);

    private Message formatErrorMessage(Exception e){
        MessageProtocolHead messageProtocolHead = new MessageProtocolHead();
        messageProtocolHead.setRequestId(this.requestId);
        messageProtocolHead.setDataType(ERROR);
        MessageProtocolBody messageProtocolBody = new MessageProtocolBody();
        messageProtocolBody.setContent(HessianUtils.write(e.getMessage()));
        MessageProtocol messageProtocol = new MessageProtocol();
        messageProtocol.setProtocolHead(messageProtocolHead);
        messageProtocol.setProtocolBody(messageProtocolBody);
        return messageProtocol;
    };
    protected Connection getConnection() throws SQLException {
        return this.hikariDataSource.getConnection();
    }

    protected void toSendMessageToClient(){
        T execute ;
        Message message;
        try {
            execute = this.execute();
            message = this.result2Message(execute);
        }catch (Exception e){
            LOGGER.error("执行 sql 异常！",e);
            message = formatErrorMessage(e);
        }
        this.sendMessageToClient(message);

    }
}
