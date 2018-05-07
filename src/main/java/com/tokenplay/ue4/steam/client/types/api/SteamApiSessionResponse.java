package com.tokenplay.ue4.steam.client.types.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SteamApiSessionResponse {

    @JsonProperty("result")
    String result;

    @JsonProperty("steamid")
    String steamId;

    @JsonProperty("ownersteamid")
    String ownerSteamId;

    @JsonProperty("vacbanned")
    boolean vacBanned;

    @JsonProperty("publisherbanned")
    boolean publisherBanned;
}
