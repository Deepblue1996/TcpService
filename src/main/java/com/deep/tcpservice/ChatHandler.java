package com.deep.tcpservice;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private Logger logger = LoggerFactory.getLogger(ChatHandler.class);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("添加客户端\0");
        CacheUtil.wsChannelGroup.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        logger.info("移除客户端\0");
        CacheUtil.wsChannelGroup.remove(ctx.channel());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        if(msg != null) {
            logger.info("收到消息:\0"+ msg.text());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
        CacheUtil.wsChannelGroup.remove(ctx.channel());
    }
}
