package com.weimai.rsc.handler;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.weimai.rsc.executor.pool.CommandLineExecPool;
import com.weimai.rsc.executor.sql.SqlFunctionExecuter;
import com.weimai.rsc.executor.sql.SqlQueryExecuter;
import com.weimai.rsc.executor.sql.SqlUpdateExecuter;
import com.weimai.rsc.log.InternalLogger;
import com.weimai.rsc.log.InternalLoggerFactory;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.request.SQL;
import com.weimai.rsc.util.HessianUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.weimai.rsc.constant.ProtocolDataType.COMMAND_LINE_SQL_FUNCTION;
import static com.weimai.rsc.constant.ProtocolDataType.COMMAND_LINE_SQL_SELECT;
import static com.weimai.rsc.constant.ProtocolDataType.COMMAND_LINE_SQL_UPDATE;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * netty handler 接收协议包并解析分发给给具体线程执行
 *
 * @author DiZhi
 * @since 2021-06-16 14:53
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(NettyServerHandler.class);
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
        int port = socketAddress.getPort();
        String hostAddress = socketAddress.getAddress().getHostAddress();
        LOGGER.info(hostAddress + ":" + port + "已连接！");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol messageProtocol) {
        Channel channel = ctx.channel();
        switch (messageProtocol.getProtocolHead().getDataType()) {
            case COMMAND_LINE_SQL_SELECT:
                CommandLineExecPool.submit(new SqlQueryExecuter(
                        HessianUtils.read(messageProtocol.getProtocolBody().getContent(), SQL.class),
                        channel, messageProtocol.getProtocolHead().getRequestId()));
                break;
            case COMMAND_LINE_SQL_UPDATE:
                CommandLineExecPool.submit(new SqlUpdateExecuter(
                        HessianUtils.read(messageProtocol.getProtocolBody().getContent(), SQL.class),
                        channel, messageProtocol.getProtocolHead().getRequestId()));
                break;
            case COMMAND_LINE_SQL_FUNCTION:
                CommandLineExecPool.submit(new SqlFunctionExecuter(
                        HessianUtils.read(messageProtocol.getProtocolBody().getContent(), SQL.class),
                        channel, messageProtocol.getProtocolHead().getRequestId()));
                break;
            default:
                LOGGER.error("来自["+((InetSocketAddress)channel.remoteAddress()).getAddress().getHostAddress()+"]的请求已被忽略。未知的请求类型！");
                break;
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx){
        //这里执行客户端断开连接后的操作
        InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
        String hostAddress = socketAddress.getAddress().getHostAddress();
        int port = socketAddress.getPort();
        LOGGER.info("客户端："+hostAddress + ":" + port+"已断开连接！");
    }
}
