package com.weimai.rsc.handler;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.weimai.rsc.executor.pool.CommandLineExecPool;
import com.weimai.rsc.executor.sql.SqlFunctionExecuter;
import com.weimai.rsc.executor.sql.SqlQueryExecuter;
import com.weimai.rsc.executor.sql.SqlUpdateExecuter;
import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.request.SQL;
import com.weimai.rsc.util.HessianUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.weimai.rsc.constant.ProtocolDataType.COMMAND_LINE_SQL_FUNCTION;
import static com.weimai.rsc.constant.ProtocolDataType.COMMAND_LINE_SQL_SELECT;
import static com.weimai.rsc.constant.ProtocolDataType.COMMAND_LINE_SQL_UPDATE;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * netty handler 接收协议包并解析分配给具体线程执行
 *
 * @author DiZhi
 * @since 2021-06-16 14:53
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
        int port = socketAddress.getPort();
        String hostAddress = socketAddress.getAddress().getHostAddress();
        System.out.println(hostAddress + ":" + port + "已链接成功");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol messageProtocol) {
        switch (messageProtocol.getProtocolHead().getDataType()) {
            case COMMAND_LINE_SQL_SELECT:
                CommandLineExecPool.submit(new SqlQueryExecuter(
                        HessianUtils.read(messageProtocol.getProtocolBody().getContent(), SQL.class),
                        channelHandlerContext.channel(), messageProtocol.getProtocolHead().getRequestId()));
                break;
            case COMMAND_LINE_SQL_UPDATE:
                CommandLineExecPool.submit(new SqlUpdateExecuter(
                        HessianUtils.read(messageProtocol.getProtocolBody().getContent(), SQL.class),
                        channelHandlerContext.channel(), messageProtocol.getProtocolHead().getRequestId()));
                break;
            case COMMAND_LINE_SQL_FUNCTION:
                CommandLineExecPool.submit(new SqlFunctionExecuter(
                        HessianUtils.read(messageProtocol.getProtocolBody().getContent(), SQL.class),
                        channelHandlerContext.channel(), messageProtocol.getProtocolHead().getRequestId()));
                break;
            default:
                break;
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }

    @Override

    public void handlerRemoved(ChannelHandlerContext ctx){
        //这里执行客户端断开连接后的操作
        InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
        String hostAddress = socketAddress.getAddress().getHostAddress();
        int port = socketAddress.getPort();
        System.out.println("客户端："+hostAddress + ":" + port+"已断开连接");
    }
}
