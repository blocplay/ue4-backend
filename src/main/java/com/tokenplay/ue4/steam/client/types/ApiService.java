package com.tokenplay.ue4.steam.client.types;

/**
 * Copyright (c) Hotelbeds Technology S.L.U. All rights reserved.
 */
public enum ApiService {

    DEVELOPMENT("http://localhost:8080/common-booking-service"),
    LIVE("https://api.hotelbeds.com/common-booking-service"),
    TEST("https://api.test.hotelbeds.com/common-booking-service");

    private String hotelApiPath;

    ApiService(final String hotelApiPath) {
        this.hotelApiPath = hotelApiPath;
    }

    public String getHotelApiPath(String alternativePath) {
        return alternativePath != null ? alternativePath : hotelApiPath;
    }

}
