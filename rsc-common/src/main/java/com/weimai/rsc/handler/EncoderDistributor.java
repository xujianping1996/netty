package com.weimai.rsc.handler;

import com.weimai.rsc.msg.MessageProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-07-30 17:31
 */
public class EncoderDistributor extends ChannelOutboundHandlerAdapter {
    private final HttpResponseEncoderWapper httpResponseEncoderWapper = new HttpResponseEncoderWapper();
    private final MessageEncoder messageEncoder = new MessageEncoder();


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof MessageProtocol){
            //自定义协议
            messageEncoder.write(ctx,msg,promise);
        }else {
            //http协议
            httpResponseEncoderWapper.write(ctx,msg,promise);
        }
    }
}
