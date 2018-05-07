package com.tokenplay.ue4.www.controllers;

public enum ContainerType {
    MESSAGES(0);

    int code;

    ContainerType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ContainerType getEnum(int code) {
        for (ContainerType v : values())
            if (v.getCode() == code)
                return v;
        throw new IllegalArgumentException();
    }
}
