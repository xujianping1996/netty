package com.weimai.rsc.msg;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import com.weimai.rsc.util.HessianUtils;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 协议包
 *
 * @author DiZhi
 * @since 2021-06-29 20:48
 */
public class MessageProtocol implements Serializable,Message {


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

    private void setHeadLength(int headLength) {
        this.headLength = headLength;
    }

    public ProtocolHead getProtocolHead() {
        return protocolHead;
    }

    public void setProtocolHead(ProtocolHead protocolHead) {
        this.protocolHead = protocolHead;
        byte[] write = HessianUtils.write(protocolHead);
        setHeadLength(write.length);

    }

    public int getBodyLength() {
        return bodyLength;
    }

    private void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public ProtocolBody getProtocolBody() {
        return protocolBody;
    }

    public void setProtocolBody(ProtocolBody protocolBody) {
        this.protocolBody = protocolBody;
        setBodyLength(HessianUtils.write(protocolBody).length);
    }

    @Override
    public String toString() {
        return "MessageProtocol{" + "headLength=" + headLength + ", protocolHead=" + protocolHead + ", bodyLength="
               + bodyLength + ", protocolBody=" + protocolBody + '}';
    }
}
