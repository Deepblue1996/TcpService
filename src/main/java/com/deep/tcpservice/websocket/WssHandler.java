package com.deep.tcpservice.websocket;

import com.deep.tcpservice.bean.UserTable;
import com.deep.tcpservice.config.CacheGroup;
import com.deep.tcpservice.util.TokenUtil;
import com.deep.tcpservice.websocket.bean.BaseEn;
import com.deep.tcpservice.websocket.bean.ChatMsgBean;
import com.deep.tcpservice.websocket.bean.TokenChatBean;
import com.deep.tcpservice.websocket.bean.UserTableChatBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;

public class WssHandler extends SimpleChannelInboundHandler<Object> {

    private Logger logger = LoggerFactory.getLogger(WssHandler.class);

    // 当前在线用户的信息
    public static List<UserTableChatBean> userChatBeanList = new ArrayList<>();

    private WebSocketServerHandshaker handShaker;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("Add client");
        userChatBeanList.add(new UserTableChatBean(null, ctx.channel().id().asLongText(), false));
        CacheGroup.wsShannels.add(ctx.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("Wss Client:" + incoming.remoteAddress() + " online");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        logger.info("Remove client");
        for (int i = 0; i < userChatBeanList.size(); i++) {
            if (userChatBeanList.get(i).getAsLongText().equals(ctx.channel().id().asLongText())) {
                userChatBeanList.remove(i);
                break;
            }
        }
        CacheGroup.wsShannels.remove(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("Wss Client:" + incoming.remoteAddress() + " offline");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("Wss Client:" + incoming.remoteAddress() + " exception");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 获取客户端传输过来的消息
        if (msg instanceof FullHttpRequest) {
            //以http请求形式接入，但是走的是websocket
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            //处理websocket客户端的消息
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            handShaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(
                    new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 本例程仅支持文本消息，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            logger.debug("本例程仅支持文本消息，不支持二进制消息");
            throw new UnsupportedOperationException(String.format(
                    "%s frame types not supported", frame.getClass().getName()));
        }

        // 会话操作 --------------------------------------------------------

        // 返回应答消息
        String strMsg = ((TextWebSocketFrame) frame).text();

        logger.debug("Wss Service：" + strMsg);
        if (strMsg != null) {
            logger.info("Message:" + strMsg);
            // 向下位机转发消息
            // CacheGroup.downChannelGroup.writeAndFlush(msg.text());
            BaseEn<?> baseEn = new BaseEn<>();
            try {
                baseEn = new Gson().fromJson(strMsg, BaseEn.class);
            } catch (Exception e) {
                baseEn.code = 404;
                e.printStackTrace();
            }
            switch (baseEn.code) {
                case 404:
                    logger.error("json error");
                    break;
                case 10000:
                    // 连接成功，返回给客户端
                    Type type = new TypeToken<BaseEn<TokenChatBean>>() {
                    }.getType();
                    BaseEn<TokenChatBean> baseEnChild = new Gson().fromJson(strMsg, type);

                    baseEnChild.data.asLongText = ctx.channel().id().asLongText();
                    baseEnChild.code = 10000;
                    baseEnChild.msg = "connected to service";

                    String msg = new Gson().toJson(baseEnChild);

                    //ByteBuf resp = Unpooled.copiedBuffer(msg.getBytes());
                    //ctx.channel().writeAndFlush(resp);

                    logger.info("connect client success, return id");
                    ctx.channel().writeAndFlush(new TextWebSocketFrame(msg));

                    for (int i = 0; i < userChatBeanList.size(); i++) {
                        if (userChatBeanList.get(i).getAsLongText().equals(ctx.channel().id().asLongText())) {
                            userChatBeanList.get(i).setConnectFirst(true);
                        }
                    }
                    break;
                case 20000:
                    /**
                     * 获取客户端的Token
                     */
                    for (int i = 0; i < userChatBeanList.size(); i++) {
                        if (userChatBeanList.get(i).getAsLongText().equals(ctx.channel().id().asLongText()) && userChatBeanList.get(i).isConnectFirst()) {

                            Type type2 = new TypeToken<BaseEn<TokenChatBean>>() {
                            }.getType();
                            BaseEn<TokenChatBean> baseEnChild2 = new Gson().fromJson(strMsg, type2);

                            if (!TokenUtil.haveToken(baseEnChild2.data.token)) {
                                logger.info("token over");
                                logger.info("connect client error");
                                ctx.close();
                            } else {
                                logger.info("connect client success again");
                                userChatBeanList.get(i).setConnectFirst(false);
                                // 临时记录在线用户的信息
                                userChatBeanList.get(i).setUserTable(TokenUtil.getUser(baseEnChild2.data.token));
                            }
                        }
                    }
                    break;
                case 30000:
                    /**
                     * 消息
                     */
                    logger.info(strMsg);
                    Type type2 = new TypeToken<BaseEn<ChatMsgBean<?>>>() {
                    }.getType();
                    BaseEn<ChatMsgBean<?>> baseEnChild2 = new Gson().fromJson(strMsg, type2);
                    if (baseEnChild2.data.type == 0) {
                        Type typeTemp = new TypeToken<BaseEn<ChatMsgBean<String>>>() {
                        }.getType();
                        BaseEn<ChatMsgBean<String>> baseEnChildTemp = new Gson().fromJson(strMsg, typeTemp);
                        for (int i = 0; i < userChatBeanList.size(); i++) {
                            if (userChatBeanList.get(i).getUserTable().getId() == baseEnChildTemp.data.userTableHere.getId()) {
                                for (int j = 0; j < CacheGroup.wsShannels.size(); j++) {
                                    if (CacheGroup.wsShannels.get(j).id().asLongText().equals(userChatBeanList.get(i).getAsLongText())) {
                                        String sendMsg = new Gson().toJson(baseEnChildTemp);
                                        logger.info("service send:" + sendMsg);
                                        CacheGroup.wsShannels.get(j).writeAndFlush(new TextWebSocketFrame(sendMsg));
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 唯一的一次http请求，用于创建websocket
     */
    private void handleHttpRequest(ChannelHandlerContext ctx,
                                   FullHttpRequest req) {
        //要求Upgrade为websocket，过滤掉get/Post
        if (!req.decoderResult().isSuccess()
                || (!"websocket".equals(req.headers().get("Upgrade")))) {
            //若不是websocket方式，则创建BAD_REQUEST的req，返回给客户端
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:8087", null, false);
        handShaker = wsFactory.newHandshaker(req);
        if (handShaker == null) {
            // 不支持
            WebSocketServerHandshakerFactory
                    .sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handShaker.handshake(ctx.channel(), req);
        }
    }

    /**
     * 拒绝不合法的请求，并返回错误信息
     */
    private static void sendHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        // 如果是非Keep-Alive，关闭连接
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
