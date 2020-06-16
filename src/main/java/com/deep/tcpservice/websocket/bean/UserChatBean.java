package com.deep.tcpservice.websocket.bean;

import com.deep.tcpservice.bean.UserTable;

import java.io.Serializable;

public class UserChatBean implements Serializable {
    /**
     * 校验是否Token
     */
    public boolean isConnectFirst;

    public String asLongText;

    public UserTable userTable;

    public UserChatBean(boolean isConnectFirst, String asLongText, UserTable userTable) {
        this.isConnectFirst = isConnectFirst;
        this.asLongText = asLongText;
        this.userTable = userTable;
    }
}
