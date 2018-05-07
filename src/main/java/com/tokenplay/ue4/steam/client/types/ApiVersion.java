package com.tokenplay.ue4.steam.client.types;

/**
 * Copyright (c) Hotelbeds Technology S.L.U. All rights reserved.
 */
public enum ApiVersion {

    V0("0.0");

    private String version;

    ApiVersion(final String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public static ApiVersion DEFAULT = V0;
}
