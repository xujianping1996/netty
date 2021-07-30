package com.weimai.rsc.handler;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequestDecoder;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-07-30 18:09
 */
public class HttpRequestDecoderWapper extends HttpRequestDecoder {

    public void decode (ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        super.decode( ctx,  buffer, out);
    }

}
