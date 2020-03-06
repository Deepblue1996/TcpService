package com.deep.tcpservice.websocket;

import com.deep.tcpservice.netty.ServerConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class WssServerInitializer extends ChannelInitializer<SocketChannel>{

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        for (ChannelHandler chatHandler: ServerConfig.getInstance().getWebsocketHandler()) {
            pipeline.addLast(chatHandler);
        }

    }

}
