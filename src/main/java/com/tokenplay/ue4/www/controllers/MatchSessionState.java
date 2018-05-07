package com.tokenplay.ue4.www.controllers;

public enum MatchSessionState {
    LOBBY(0),
    IN_PROGRESS(1),
    COMPLETE(2);

    int code;

    MatchSessionState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static MatchSessionState getEnum(int code) {
        for (MatchSessionState v : values())
            if (v.getCode() == code)
                return v;
        throw new IllegalArgumentException();
    }
}
