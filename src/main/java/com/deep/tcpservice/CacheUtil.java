package com.deep.tcpservice;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class CacheUtil {

    //���ڴ���û�Channel��Ϣ��Ҳ���Խ���map�ṹģ�ⲻͬ����ϢȺ
    public static ChannelGroup wsChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static ChannelGroup downChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

}
