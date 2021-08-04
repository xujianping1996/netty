package com.weimai.rsc.handler;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-07-30 18:51
 */
public class HttpObjectAggregatorWapper extends HttpObjectAggregator {

    public void toDecode(ChannelHandlerContext ctx, HttpObject object, List<Object> out) throws Exception {
        super.decode(ctx,object,out);
    }
    public HttpObjectAggregatorWapper(int maxContentLength) {
        super(maxContentLength);
    }

    public HttpObjectAggregatorWapper(int maxContentLength, boolean closeOnExpectationFailed) {
        super(maxContentLength, closeOnExpectationFailed);
    }
}
