package com.weimai.rsc.handler;

import java.util.List;

import com.weimai.rsc.msg.MessageProtocol;
import com.weimai.rsc.msg.MessageProtocolBody;
import com.weimai.rsc.msg.MessageProtocolHead;
import com.weimai.rsc.util.HessianUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-06-17 14:34
 */
public class MessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list)
            throws Exception {
        try {
            if (byteBuf.readableBytes()<12){
                //协议开始标识长度+请求头长度+请求体长度
                //如果小于12，说明数据一定没有到齐
                return;
            }
            int begin;
            // 在循环中找到协议开始的位置
            while (true) {
                // 本次协议包开始的位置
                begin = byteBuf.readerIndex();
                // 标记本次协议包开始的位置
                byteBuf.markReaderIndex();
                if (byteBuf.readInt() == MessageProtocol.HEAD_DATE) {
                    break;
                }
                // 没有读到 HEAD_START，那么就读取下一个字节
                byteBuf.resetReaderIndex();
                byteBuf.readByte();
            }
            if (byteBuf.readableBytes()<8){
                //请求头长度+请求体长度
                //如果小于8，说明数据一定没有到齐
                return;
            }
            // 协议包头长度
            int headLength = byteBuf.readInt();
            // 协议包体长度
            int bodyLength = byteBuf.readInt();
            // 协议包数据还未到齐，回到协议开始的位置，等待数据到齐
            if (byteBuf.readableBytes() < (headLength+bodyLength)) {
                byteBuf.readerIndex(begin);
                return;
            }
            // 读取协议包头数据
            byte[] head = new byte[headLength];
            byteBuf.readBytes(head);
            MessageProtocolHead messageProtocolHead = HessianUtils.read(head, MessageProtocolHead.class);

            // 读取协议包体数据
            byte[] body = new byte[bodyLength];
            byteBuf.readBytes(body);
            MessageProtocolBody messageProtocolBody = HessianUtils.read(body, MessageProtocolBody.class);

            // 封装协议
            MessageProtocol messageProtocol = new MessageProtocol();
            //messageProtocol.setHeadLength(headLength);
            //messageProtocol.setBodyLength(bodyLength);
            messageProtocol.setProtocolHead(messageProtocolHead);
            messageProtocol.setProtocolBody(messageProtocolBody);

            list.add(messageProtocol);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
