package com.weimai.rsc.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential. 序列化器
 *
 * @author DiZhi
 * @since 2021-07-16 16:56
 */
public class Serializer {

    public static byte[] getBytes(Object o) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream sOut = new ObjectOutputStream(out);
        sOut.writeObject(o);

        byte[] bytes = out.toByteArray();
        sOut.flush();
        return bytes;

    }

    public static <T> T valueOf(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream sIn = new ObjectInputStream(in);

        return (T)sIn.readObject();
    }

}
