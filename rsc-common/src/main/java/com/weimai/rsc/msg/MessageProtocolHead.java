package com.weimai.rsc.msg;

import java.io.Serializable;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 协议头
 *
 * @author DiZhi
 * @since 2021-07-02 14:52
 */
public class MessageProtocolHead implements Serializable {
    private long requestId;

    private byte dataType;

    public byte getDataType() {
        return dataType;
    }

    public void setDataType(byte dataType) {
        this.dataType = dataType;
    }

    public MessageProtocolHead() {
    }

    public MessageProtocolHead(long requestId) {
        this.requestId = requestId;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "ProtocolHead{" + "requestId='" + requestId + '\'' + '}';
    }
}
