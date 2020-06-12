package com.deep.tcpservice.websocket.bean;

import io.netty.channel.ChannelId;

public class UserChatBean {
    /**
     * 校验是否Token
     */
    public boolean isConnectFirst;

    public ChannelId channelId;

    public UserChatBean(boolean isConnectFirst, ChannelId channelId) {
        this.isConnectFirst = isConnectFirst;
        this.channelId = channelId;
    }
}
