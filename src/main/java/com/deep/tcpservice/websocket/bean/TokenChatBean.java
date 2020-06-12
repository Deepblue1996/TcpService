package com.deep.tcpservice.websocket.bean;

import io.netty.channel.ChannelId;

import java.io.Serializable;

public class TokenChatBean implements Serializable {
    public ChannelId id;
    public String token;
}
