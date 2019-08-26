package edu.udacity.java.nano.chat;

import com.alibaba.fastjson.JSON;

/**
 * WebSocket message model
 */
public class Message {
    // TODO: add message model.

    public static final String ENTER = "ENTER";
    public static final String CHAT = "CHAT";
    public static final String LEAVE = "LEAVE";

    // transfer a Message object to a json string
    public static String serializer(String type, String username, String msg, int count) {
        return JSON.toJSONString(new Message(type, username, msg, count));
    }

    // message type(ENTER/CHAT/LEAVE)
    private String type;
    // username
    private String userName;
    // message content
    private String content;
    // number of online users
    private int onlineUsers;

    public Message() {
    }

    public Message(String type, String userName, String content, int onlineUsers) {
        this.type = type;
        this.userName = userName;
        this.content = content;
        this.onlineUsers = onlineUsers;
    }

    public static String getENTER() {
        return ENTER;
    }

    public static String getCHAT() {
        return CHAT;
    }

    public static String getLEAVE() {
        return LEAVE;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(int onlineUsers) {
        this.onlineUsers = onlineUsers;
    }
}
