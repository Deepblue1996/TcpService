package com.deep.tcpservice;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class CacheUtil {

    //用于存放用户Channel信息，也可以建立map结构模拟不同的消息群
    public static ChannelGroup wsChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

}
