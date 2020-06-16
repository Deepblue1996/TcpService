package com.deep.tcpservice.websocket;

import com.deep.tcpservice.netty.ServerConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class WssServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        //ChannelPipeline pipeline = ch.pipeline();

        // websocket基于http协议,所以要有http编解码器
//        pipeline.addLast(new HttpServerCodec());
//        // 对httpMessage进行聚合, 聚合成FullHttpRequest或fullHttpResponse
//        pipeline.addLast(new HttpObjectAggregator(1024*64));
//        // 对写大数据流的支持
//        pipeline.addLast(new ChunkedWriteHandler());
//        // ----------------- 以上用于支持HTTP协议------------------
//        // websocket处理的协议,用于指定给客户端连接访问的路由, : /ws
//        // 本handler会帮你处理一些繁重的复杂的事
//        // 会帮你处理握手动作: handshaking(close,ping,pong) ping+pong=心跳
//        // 对于websocket来说,都是以frames进行传输的,不同的数据类型对应的frames也不同
//        //pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
//        pipeline.addLast(new WssHandler(userTableRepository));
//        pipeline.addLast(new WebSocketServerProtocolHandler("/ws",null,true,65535));

//        pipeline.addLast(new HttpServerCodec())
//                .addLast(new HttpObjectAggregator(65536))
//                //.addLast(new ChunkedWriteHandler())
//                .addLast(new WebSocketServerCompressionHandler())
//                .addLast(new WebSocketServerProtocolHandler("/ws"))
//                .addLast(new WssHandler());
        ChannelPipeline pipeline = ch.pipeline();

        for (ChannelHandler chatHandler: ServerConfig.getInstance().getWebsocketHandler()) {
            pipeline.addLast(chatHandler);
        }

    }

}
