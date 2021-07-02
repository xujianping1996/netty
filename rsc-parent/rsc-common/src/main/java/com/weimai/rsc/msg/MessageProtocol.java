package com.weimai.rsc.msg;

import java.io.Serializable;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 协议包
 *
 * @author DiZhi
 * @since 2021-06-29 20:48
 */
public class MessageProtocol implements Serializable {


    public static final int HEAD_DATE = 0x60;

    private int headLength;

    private ProtocolHead protocolHead;

    private int bodyLength;

    private ProtocolBody protocolBody;

    public MessageProtocol() {
    }

    public static int getHeadDate() {
        return HEAD_DATE;
    }

    public int getHeadLength() {
        return headLength;
    }

    public void setHeadLength(int headLength) {
        this.headLength = headLength;
    }

    public ProtocolHead getProtocolHead() {
        return protocolHead;
    }

    public void setProtocolHead(ProtocolHead protocolHead) {
        this.protocolHead = protocolHead;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public ProtocolBody getProtocolBody() {
        return protocolBody;
    }

    public void setProtocolBody(ProtocolBody protocolBody) {
        this.protocolBody = protocolBody;
    }
}
