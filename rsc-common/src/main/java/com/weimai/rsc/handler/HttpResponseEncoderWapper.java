package com.weimai.rsc.handler;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-07-30 18:20
 */
public class HttpResponseEncoderWapper extends HttpResponseEncoder {
    public void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        super.encode( ctx,  msg, out);
    }
}
