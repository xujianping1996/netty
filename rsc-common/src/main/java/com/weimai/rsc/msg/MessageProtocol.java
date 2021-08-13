package com.weimai.rsc.msg;

import java.io.Serializable;

import com.weimai.rsc.util.HessianUtils;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 自定义协议，协议包
 *
 * @author DiZhi
 * @since 2021-06-29 20:48
 */
public class MessageProtocol implements Serializable,Message {

    /**
     * 自定义协议开始标志
     */
    public static final int HEAD_DATE = 0xffffffff;

    /**
     * 协议头长度
     */
    private int headLength;

    /**
     * 协议头
     */
    private MessageProtocolHead messageProtocolHead;

    /**
     * 协议体长度
     */
    private int bodyLength;

    /**
     * 协议体
     */
    private MessageProtocolBody messageProtocolBody;

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

    public MessageProtocolHead getProtocolHead() {
        return messageProtocolHead;
    }

    public void setProtocolHead(MessageProtocolHead messageProtocolHead) {
        this.messageProtocolHead = messageProtocolHead;
        byte[] write = HessianUtils.write(messageProtocolHead);
        setHeadLength(write.length);

    }

    public int getBodyLength() {
        return bodyLength;
    }

    private void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public MessageProtocolBody getProtocolBody() {
        return messageProtocolBody;
    }

    public void setProtocolBody(MessageProtocolBody messageProtocolBody) {
        this.messageProtocolBody = messageProtocolBody;
        setBodyLength(HessianUtils.write(messageProtocolBody).length);
    }

    @Override
    public String toString() {
        return "MessageProtocol{" + "headLength=" + headLength + ", protocolHead=" + messageProtocolHead
               + ", bodyLength="
               + bodyLength + ", protocolBody=" + messageProtocolBody + '}';
    }
}
