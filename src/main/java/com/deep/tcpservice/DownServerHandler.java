package com.deep.tcpservice;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownServerHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(DownServerHandler.class);

    /**
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        String channelId = channel.id().toString();

        logger.info("链接报告开始");
        logger.info("链接报告信息：有一客户端链接到本服务端:"+channelId);
        logger.info("链接报告IP:" + channel.localAddress().getHostString());
        logger.info("链接报告Port:" + channel.localAddress().getPort());
        logger.info("链接报告完毕");
    }

    /**
     * 发送的内容必须"\n"换行符结尾
     * @param ctx
     * @param objMsgJsonStr
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object objMsgJsonStr) throws Exception {

        String msg = objMsgJsonStr.toString();
        //接收设备发来信息
        logger.info("收到数据:"+msg);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        // 设备断开
        logger.info("链接报告结束");
    }
}