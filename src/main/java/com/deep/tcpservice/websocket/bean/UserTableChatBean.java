package com.deep.tcpservice.websocket.bean;

import com.deep.tcpservice.bean.UserTable;
import lombok.Data;

@Data
public class UserTableChatBean {
    private UserTable userTable;
    private String asLongText;
    private boolean isConnectFirst;

    public UserTableChatBean(UserTable userTable, String asLongText, boolean isConnectFirst) {
        this.userTable = userTable;
        this.asLongText = asLongText;
        this.isConnectFirst = isConnectFirst;
    }
}
