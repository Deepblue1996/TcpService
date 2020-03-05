package com.deep.tcpservice;

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
public class TcpServer {

    /**
     * 单例静态内部类
     * @author asus
     *
     */
    public static class SingletionTcpServer {
        static final TcpServer instance = new TcpServer();
    }

    public static TcpServer getInstance(){
        return SingletionTcpServer.instance;
    }

    private EventLoopGroup mainGroup ;
    private EventLoopGroup subGroup;
    private ServerBootstrap server;

    public TcpServer(){
        mainGroup = new NioEventLoopGroup();
        subGroup = new NioEventLoopGroup();
        server = new ServerBootstrap();
        server.group(mainGroup, subGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new TcpServerInitializer());	//添加自定义初始化处理器
    }

    public void start() throws InterruptedException {
        this.server.bind(8088).sync();
        System.err.println("tcp netty service started .....");
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
