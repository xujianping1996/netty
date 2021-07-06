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
public class ProtocolHead implements Serializable {
    private String requestId;

    public ProtocolHead() {
    }

    public ProtocolHead(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "ProtocolHead{" + "requestId='" + requestId + '\'' + '}';
    }
}
