package com.tokenplay.ue4.steam.client.types.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppOwnership {
    @JsonProperty("ownsapp")
    private boolean ownerOfTheApp;

    private boolean permanent;

    @JsonProperty("ownersteamid")
    private String ownerSteamId;

    private String result;
}
