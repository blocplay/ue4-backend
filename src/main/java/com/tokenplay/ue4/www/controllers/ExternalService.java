package com.tokenplay.ue4.www.controllers;

public enum ExternalService {

    STEAM("steam");

    String serviceName;

    ExternalService(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public static ExternalService getEnum(String serviceName) {
        for (ExternalService v : values())
            if (v.getServiceName().equalsIgnoreCase(serviceName))
                return v;
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return getServiceName();
    }
}
