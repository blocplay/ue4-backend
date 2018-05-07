package com.tokenplay.ue4.www.controllers;

public enum MessageType {
    INVITE(0),
    KICK(1),
    LAUNCH(2);

    int code;

    MessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static MessageType getEnum(int code) {
        for (MessageType v : values())
            if (v.getCode() == code)
                return v;
        throw new IllegalArgumentException();
    }
}
