package com.tokenplay.ue4.steam.client.types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.tokenplay.ue4.steam.client.types.api.ApiError;

@Data
@EqualsAndHashCode(callSuper = false)
public class ApiException extends Exception {

    public static final long serialVersionUID = 1L;

    private final ApiError error;

    public ApiException(ApiError apiError) {
        this(apiError, null);
    }

    public ApiException(ApiError error, Throwable throwable) {
        super("HotelbedsApiException (Error " + error.getCode() + " while performing operation", throwable);
        this.error = error;
    }


}
