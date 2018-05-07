package com.tokenplay.ue4.www.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterServerResponse extends JSONResponse {
    @JsonProperty("ID")
    final String id;

    @JsonProperty("AL")
    final String alias;

    @JsonProperty("DE")
    final String description;

    @JsonProperty("MT")
    final long matchTime;

    @JsonProperty("DEV")
    final boolean devServer;

    @JsonProperty("MAXB")
    final long maxBots;

    @JsonProperty("MAXP")
    final long maxPlayers;

    @JsonProperty("SMPO")
    final long overrideMapPlayerCount;

}
