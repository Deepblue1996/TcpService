package com.deep.tcpservice.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class InfoBean<T> implements Serializable {
    private int code = 200;
    private String msg = "success";
    private T data;
}
