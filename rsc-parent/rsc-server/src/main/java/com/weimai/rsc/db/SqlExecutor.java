//package com.weimai.rsc.db;
//
//import com.weimai.rsc.executor.ServerExecuter;
//import com.weimai.rsc.msg.MessageProtocol;
//import io.netty.channel.Channel;
//
///**
// * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
// * sql 执行类
// * @author DiZhi
// * @since 2021-07-02 16:03
// */
//public class SqlExecutor {
//
//    public static void execute(MessageProtocol messageProtocol, Channel channel){
//        MessageProtocol messageProtocol1 = new MessageProtocol();
//        String s = new String(messageProtocol.getProtocolBody().getContent());
//        String requestId = messageProtocol.getProtocolHead().getRequestId();
//        ServerExecuter sqlExecutor = new ServerExecuter(s,channel,requestId);
//        new Thread(sqlExecutor).start();
//    }
//
//}
