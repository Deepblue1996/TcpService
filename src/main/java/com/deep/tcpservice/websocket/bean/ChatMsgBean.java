package com.deep.tcpservice.websocket.bean;

import com.deep.tcpservice.bean.UserTable;

import java.io.Serializable;

public class ChatMsgBean<T> implements Serializable {
    public UserTable userTableMine;
    public UserTable userTableHere;
    public int type;
    public T data;
}
