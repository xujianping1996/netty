package com.weimai.rsc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 序列化工具类
 *
 * @author DiZhi
 * @since 2021-07-02 15:17
 */
public class HessianUtils {
        public static <T> T read(byte[] data, Class<T> clazz) {
            ByteArrayInputStream os = new ByteArrayInputStream(data);
            Hessian2Input input = new Hessian2Input(os);
            try {
                return (T) input.readObject(clazz);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        public static byte[] write(Object obj) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Hessian2Output output = new Hessian2Output(os);
            try {
                output.writeObject(obj);
                output.close();
                return os.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new byte[0];
        }
}
