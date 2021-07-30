package com.weimai.rsc.handler;

import com.weimai.rsc.msg.MessageProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-07-30 17:22
 */
public class HandlerDistributor extends ChannelInboundHandlerAdapter {

    private HttpFileServerHandler httpFileServerHandler = new HttpFileServerHandler("/rsc");
    private NettyServerHandler nettyServerHandler = new NettyServerHandler();
    //private HttpFileServerHander httpFileServerHander = new HttpFileServerHander();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof MessageProtocol){
            nettyServerHandler.channelRead(ctx,msg);
        }else {
            httpFileServerHandler.channelRead(ctx,msg);
        }
    }
}
