package com.deep.tcpservice;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class WssServerInitializer extends ChannelInitializer<SocketChannel>{


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        for (ChannelHandler chatHandler: Server.getInstance().getWebsocketHandler()) {
            pipeline.addLast(chatHandler);
        }

    }

}
