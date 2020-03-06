package com.deep.tcpservice.websocket;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * 服务端基本配置，通过一个静态单例类，保证启动时候只被加载一次
 * @author asus
 *
 */
@Component
public class WssServer {

    /**
     * 单例静态内部类
     * @author asus
     *
     */
    public static class SingletionWSServer{
        static final WssServer instance = new WssServer();
    }

    public static WssServer getInstance(){
        return SingletionWSServer.instance;
    }

    private EventLoopGroup mainGroup ;
    private EventLoopGroup subGroup;
    private ServerBootstrap server;

    public WssServer(){
        mainGroup = new NioEventLoopGroup();
        subGroup = new NioEventLoopGroup();
        server = new ServerBootstrap();
        server.group(mainGroup, subGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new WssServerInitializer());	//添加自定义初始化处理器
    }

    public void start() throws InterruptedException {
        this.server.bind(8087).sync();
        System.err.println("wss netty service started .....");
    }
    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        mainGroup.shutdownGracefully().syncUninterruptibly();
        subGroup.shutdownGracefully().syncUninterruptibly();
        System.err.println("closeed netty service .....");
    }
}
