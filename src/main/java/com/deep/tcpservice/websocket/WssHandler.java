package com.deep.tcpservice.websocket;

import com.deep.tcpservice.bean.UserTableRepository;
import com.deep.tcpservice.config.CacheGroup;
import com.deep.tcpservice.util.TokenUtil;
import com.deep.tcpservice.websocket.bean.BaseEn;
import com.deep.tcpservice.websocket.bean.TokenChatBean;
import com.deep.tcpservice.websocket.bean.UserChatBean;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WssHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private Logger logger = LoggerFactory.getLogger(WssHandler.class);

    @Resource
    private UserTableRepository userTableRepository;

    public List<UserChatBean> userChatBeanList = new ArrayList<>();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("Add client");
        CacheGroup.wsChannelGroup.add(ctx.channel());

        userChatBeanList.add(new UserChatBean(false, ctx.channel().id()));

        TokenChatBean tokenChatBean = new TokenChatBean();
        tokenChatBean.id = ctx.channel().id();
        tokenChatBean.token = "";
        BaseEn<TokenChatBean> baseEn = new BaseEn<>();
        baseEn.code = 10000;
        baseEn.msg = "connected to service";
        baseEn.data = tokenChatBean;

        String msg = new Gson().toJson(baseEn);
        ctx.writeAndFlush(msg);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        logger.info("Remove client");
        for (int i = 0; i < userChatBeanList.size(); i++) {
            if (userChatBeanList.get(i).channelId == ctx.channel().id()) {
                userChatBeanList.remove(i);
                break;
            }
        }
        CacheGroup.wsChannelGroup.remove(ctx.channel());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        if (msg != null) {
            logger.info("Message:" + msg.text());
            // 向下位机转发消息
            // CacheGroup.downChannelGroup.writeAndFlush(msg.text());
            BaseEn<?> baseEn = new Gson().fromJson(msg.text(), BaseEn.class);
            if (baseEn.code == 10000) { // Token Code
                for (int i = 0; i < userChatBeanList.size(); i++) {
                    if (userChatBeanList.get(i).channelId == ctx.channel().id() && userChatBeanList.get(i).isConnectFirst) {
                        TokenChatBean tokenBean = (TokenChatBean) baseEn.data;
                        if (!TokenUtil.haveToken(userTableRepository, tokenBean.token)) {
                            logger.info("token over");
                            ctx.close();
                        } else {
                            userChatBeanList.get(i).isConnectFirst = true;
                        }
                    }
                }
            }

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
        CacheGroup.wsChannelGroup.remove(ctx.channel());
    }
}
