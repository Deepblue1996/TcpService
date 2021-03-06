package com.deep.tcpservice.config;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.List;

public class CacheGroup {

    //用于存放用户Channel信息，也可以建立map结构模拟不同的消息群
    //public static ChannelGroup wsChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static ChannelGroup downChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static List<Channel> wsShannels = new ArrayList<>();
}
