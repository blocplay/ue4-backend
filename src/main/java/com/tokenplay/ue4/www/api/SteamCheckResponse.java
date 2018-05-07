package com.tokenplay.ue4.www.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SteamCheckResponse extends JSONResponse {
    @JsonProperty("Username")
    final String username;
}
