package com.deep.tcpservice;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public static Server server;

    public static Server getInstance() {
        if(server == null) {
            server = new Server();
        }
        return server;
    }

    public List<ChannelHandler> getTcpHandler() {

        List<ChannelHandler> channelHandlers = new ArrayList<>();

        // 基于换行符号
        channelHandlers.add(new LineBasedFrameDecoder(1024));
        // 解码转String，注意调整自己的编码格式GBK、UTF-8
        channelHandlers.add(new StringDecoder(Charset.forName("GBK")));
        // 在管道中添加我们自己的接收数据实现方法
        channelHandlers.add(new DownServerHandler());

        return channelHandlers;
    }

    public  List<ChannelHandler> getWebsocketHandler() {

        List<ChannelHandler> channelHandlers = new ArrayList<>();

        //websocket基于http协议，所以需要http编解码器
        channelHandlers.add(new HttpServerCodec());
        //添加对于读写大数据流的支持
        channelHandlers.add(new ChunkedWriteHandler());
        //对httpMessage进行聚合
        channelHandlers.add(new HttpObjectAggregator(1024*64));

        // ================= 上述是用于支持http协议的 ==============

        //websocket 服务器处理的协议，用于给指定的客户端进行连接访问的路由地址
        //比如处理一些握手动作(ping,pong)
        channelHandlers.add(new WebSocketServerProtocolHandler("/ws","webSocket",true, 65536*10));

        //自定义handler
        channelHandlers.add(new ChatHandler());
        return channelHandlers;
    }
}
