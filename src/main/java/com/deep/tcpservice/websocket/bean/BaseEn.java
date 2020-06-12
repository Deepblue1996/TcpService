package com.deep.tcpservice.websocket.bean;

import java.io.Serializable;

public class BaseEn<T> implements Serializable {
    public int code;
    public String msg;
    public T data;
}
