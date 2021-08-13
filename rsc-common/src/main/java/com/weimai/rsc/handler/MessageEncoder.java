package com.weimai.rsc.handler;

import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.util.HessianUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-06-17 14:36
 */
public class MessageEncoder extends MessageToByteEncoder<MessageProtocol> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageProtocol messageProtocol, ByteBuf byteBuf)
            throws Exception {
        byteBuf.writeInt(MessageProtocol.HEAD_DATE);
        byteBuf.writeInt(messageProtocol.getHeadLength());
        byteBuf.writeInt(messageProtocol.getBodyLength());
        byteBuf.writeBytes(HessianUtils.write(messageProtocol.getProtocolHead()));
        byteBuf.writeBytes(HessianUtils.write(messageProtocol.getProtocolBody()));
    }
}
