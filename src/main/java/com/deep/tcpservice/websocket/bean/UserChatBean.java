package com.deep.tcpservice.websocket.bean;

import io.netty.channel.ChannelId;

public class UserChatBean {
    /**
     * 校验是否Token
     */
    public boolean isConnectFirst = false;

    public String asLongText;

    public UserChatBean(boolean isConnectFirst, String asLongText) {
        this.isConnectFirst = isConnectFirst;
        this.asLongText = asLongText;
    }
}
