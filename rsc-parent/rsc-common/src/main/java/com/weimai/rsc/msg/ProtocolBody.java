package com.weimai.rsc.msg;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * 协议体
 *
 * @author DiZhi
 * @since 2021-07-02 14:53
 */
public class ProtocolBody implements Serializable {
    private byte[] content;

    public ProtocolBody() {
    }

    public ProtocolBody(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ProtocolBody{" + "content=" + new String(content, StandardCharsets.UTF_8) + '}';
    }
}
