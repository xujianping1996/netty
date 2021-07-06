package com.weimai.rsc.db;

import java.nio.charset.StandardCharsets;

import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.ProtocolBody;
import com.weimai.rsc.msg.ProtocolHead;
import com.weimai.rsc.util.HessianUtils;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * sql 执行类
 * @author DiZhi
 * @since 2021-07-02 16:03
 */
public class SqlExecutor {

    public static MessageProtocol execute(MessageProtocol messageProtocol){
        try {
            Thread.sleep(3L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MessageProtocol messageProtocol1 = new MessageProtocol();
        String requestId = messageProtocol.getProtocolHead().getRequestId();
        String result = messageProtocol.getProtocolBody().toString();
        ProtocolHead protocolHead = new ProtocolHead();
        ProtocolBody protocolBody = new ProtocolBody();
        protocolHead.setRequestId(requestId);
        protocolBody.setContent(result.getBytes(StandardCharsets.UTF_8));
        messageProtocol1.setHeadLength(HessianUtils.write(protocolHead).length);
        messageProtocol1.setBodyLength(HessianUtils.write(protocolBody).length);
        messageProtocol1.setProtocolBody(protocolBody);
        messageProtocol1.setProtocolHead(protocolHead);

        return messageProtocol1;
    }
}
