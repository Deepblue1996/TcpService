package com.deep.tcpservice;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

public class WssServerInitializer extends ChannelInitializer<SocketChannel>{

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();
        // 基于换行符号
        pipeline.addLast(new LineBasedFrameDecoder(1024));
        // 解码转String，注意调整自己的编码格式GBK、UTF-8
        pipeline.addLast(new StringDecoder(Charset.forName("GBK")));
        // 解码转String，注意调整自己的编码格式GBK、UTF-8
        pipeline.addLast(new StringEncoder(Charset.forName("GBK")));
        // 在管道中添加我们自己的接收数据实现方法
        pipeline.addLast(new DownServerHandler());

//        ChannelPipeline pipeline = ch.pipeline();
//
//        //websocket基于http协议，所以需要http编解码器
//        pipeline.addLast(new HttpServerCodec());
//        //添加对于读写大数据流的支持
//        pipeline.addLast(new ChunkedWriteHandler());
//        //对httpMessage进行聚合
//        pipeline.addLast(new HttpObjectAggregator(1024*64));
//
//        // ================= 上述是用于支持http协议的 ==============
//
//        pipeline.addLast(new MyServerHandler());
//
//        //websocket 服务器处理的协议，用于给指定的客户端进行连接访问的路由地址
//        //比如处理一些握手动作(ping,pong)
//        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
//
//        //自定义handler
//        pipeline.addLast(new ChatHandler());
    }

}
