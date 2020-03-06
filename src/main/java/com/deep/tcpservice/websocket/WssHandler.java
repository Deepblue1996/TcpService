package com.deep.tcpservice.websocket;

import com.deep.tcpservice.config.CacheGroup;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WssHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private Logger logger = LoggerFactory.getLogger(WssHandler.class);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("添加客户端\t");
        CacheGroup.wsChannelGroup.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        logger.info("移除客户端\t");
        CacheGroup.wsChannelGroup.remove(ctx.channel());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        if(msg != null) {
                logger.info("收到消息:\t"+ msg.text());
                CacheGroup.downChannelGroup.writeAndFlush(msg.text());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
        CacheGroup.wsChannelGroup.remove(ctx.channel());
    }
}
