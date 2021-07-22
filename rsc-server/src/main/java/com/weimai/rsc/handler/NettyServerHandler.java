package com.weimai.rsc.handler;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import com.weimai.rsc.executor.pool.CommandLineExecPool;
import com.weimai.rsc.executor.sql.SqlQueryExecuter;
import com.weimai.rsc.executor.sql.SqlUpdateExecuter;
import com.weimai.rsc.msg.MessageProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.weimai.rsc.constant.ProtocolDataType.COMMAND_LINE_SQL_SELECT;
import static com.weimai.rsc.constant.ProtocolDataType.COMMAND_LINE_SQL_UPDATE;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-06-16 14:53
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
        int port = socketAddress.getPort();
        String hostAddress = socketAddress.getAddress().getHostAddress();
        System.out.println(hostAddress+":"+port+"已链接成功");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol messageProtocol)
            throws Exception {
        switch (messageProtocol.getProtocolHead().getDataType()){
            case COMMAND_LINE_SQL_SELECT:
                CommandLineExecPool.submit(new SqlQueryExecuter(new String(messageProtocol.getProtocolBody().getContent(),
                                                                           StandardCharsets.UTF_8), channelHandlerContext.channel(), messageProtocol.getProtocolHead().getRequestId()));
                break;
            case COMMAND_LINE_SQL_UPDATE:
                CommandLineExecPool.submit(new SqlUpdateExecuter(new String(messageProtocol.getProtocolBody().getContent(),
                                                                            StandardCharsets.UTF_8), channelHandlerContext.channel(), messageProtocol.getProtocolHead().getRequestId()));
                break;
            default:
                break;
        }

    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }
}
