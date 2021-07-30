package com.weimai.rsc.handler;

import java.util.List;

import com.weimai.rsc.msg.MessageProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-07-30 17:31
 */
public class DecoderDistributor extends ByteToMessageDecoder {
    private final HttpRequestDecoderWapper httpRequestDecoderWapper = new HttpRequestDecoderWapper();
    private final MessageDecoder messageDecoder = new MessageDecoder();
    private final HttpObjectAggregator httpObjectAggregator = new HttpObjectAggregator(65536);
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println(in);
        try {
            if (in.readableBytes()<4){
                //字节数大于四，才能用来区分是否为自定义协议
                return;
            }
            int begin = in.readerIndex();
            // 标记本次协议包开始的位置
            in.markReaderIndex();
            int i = in.readInt();
            in.readerIndex(begin);
            if (i == MessageProtocol.HEAD_DATE) {
                //此时为自定义协议
                messageDecoder.decode(ctx,in,out);
            }else {
                //否则按照http协议处理
                httpRequestDecoderWapper.decode(ctx,in,out);
                httpObjectAggregator
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
