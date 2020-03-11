package com.deep.tcpservice.netty;

import com.deep.tcpservice.tcp.TcpServer;
import com.deep.tcpservice.websocket.WssServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * netty服务端启动加载配置
 * @author asus
 *
 */

@Component
public class NettyServerInitConfig implements ApplicationListener<ContextRefreshedEvent>{

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if(event.getApplicationContext().getParent() == null){
            try {
                TcpServer.getInstance().start();
                WssServer.getInstance().start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
