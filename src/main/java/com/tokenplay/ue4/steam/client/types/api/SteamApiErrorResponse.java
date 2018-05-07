package com.tokenplay.ue4.steam.client.types.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SteamApiErrorResponse {

    @JsonProperty("errorcode")
    int code;

    @JsonProperty("errordesc")
    String description;
}
