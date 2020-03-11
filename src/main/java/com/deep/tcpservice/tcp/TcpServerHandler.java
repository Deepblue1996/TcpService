package com.deep.tcpservice.tcp;

import com.deep.tcpservice.config.CacheGroup;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerHandler extends SimpleChannelInboundHandler<Object> {

    private Logger logger = LoggerFactory.getLogger(TcpServerHandler.class);

    /**
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        logger.info("下位机 连接开始");

        CacheGroup.downChannelGroup.add(ctx.channel());
    }

    /**
     * 发送的内容必须"\n"换行符结尾
     * @param ctx
     * @param objMsgJsonStr
     * @throws Exception
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, Object objMsgJsonStr) throws Exception {

        String msg = objMsgJsonStr.toString();
        //接收设备发来信息
        logger.info("下位机收到数据:"+msg);
        CacheGroup.wsChannelGroup.writeAndFlush(new TextWebSocketFrame(objMsgJsonStr.toString()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        // 设备断开
        logger.info("下位机 结束");
        CacheGroup.downChannelGroup.remove(ctx.channel());
    }
}