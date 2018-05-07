package com.tokenplay.ue4.www.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ServerDataSpec {
    @JsonProperty("ID")
    private String id;

    @JsonProperty("CH")
    private String championship;

    @JsonProperty("PASS")
    private String password;

    @JsonProperty("BLOCK")
    private boolean securityEnabled;

    @JsonProperty("ALLWD")
    private String allowedUsers;

    @JsonProperty("EM")
    private String email;

    @JsonProperty("AL")
    private String alias;

    @JsonProperty("DEVP")
    private String developmentPassword;

    @JsonProperty("DMTL")
    private Long defaultMapTimeLimit;

    @JsonProperty("DE")
    private String description;

    @JsonProperty("DEV")
    private Boolean development;

    @JsonProperty("MCLI")
    private Long mapCycleLastIndex;

    @JsonProperty("MAXB")
    private Long maxBots;

    @JsonProperty("MAXP")
    private Long maxPlayers;

    @JsonProperty("MT")
    private Long matchTime;

    @JsonProperty("SMPO")
    private Long overrideMapPlayerCount;

    @JsonProperty("VER")
    private String version;

    @JsonProperty("VIS")
    private Boolean visible;

    @JsonProperty("SH")
    private Boolean shuffle;

    @JsonProperty("HP")
    private String ue4Port;

    @JsonProperty("ZP")
    private String zmqPort;

    @JsonProperty("IP")
    private String ip;

    @JsonProperty("UCYL")
    private Boolean useCustomCycle;
}
