package com.weimai.rsc.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-07-29 18:47
 */
public class Collections {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
    public static boolean isEmpty(Object [] objects) {
        return objects == null || objects.length<=0;
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return Maps.isEmpty(map);
    }

    public static boolean isEmpty(Iterable<?> iterable) {
        return Iters.isEmpty(iterable);
    }

    public static boolean isEmpty(Iterator<?> Iterator) {
        return Iters.isEmpty(Iterator);
    }

    public static boolean isEmpty(Enumeration<?> enumeration) {
        return null == enumeration || !enumeration.hasMoreElements();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
}
