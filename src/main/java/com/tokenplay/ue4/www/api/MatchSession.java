package com.tokenplay.ue4.www.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MatchSession {

    @JsonProperty("ID")
    private String id;

    @JsonProperty("HOST")
    private String host;

    @JsonProperty("AL")
    private String server;

    @JsonProperty("CM")
    private boolean isCompetitive;

    @JsonProperty("CID")
    private long competitionId;

    @JsonProperty("SST")
    private int sessionStateIndex;

    @JsonProperty("MOTD")
    private String motd;

    @JsonProperty("VER")
    private String version;

    @JsonProperty("MAL")
    private String mapAlias;

    @JsonProperty("MMD")
    private String mapMode;

    @JsonProperty("TOD")
    private int timeOfDayIndex;

    @JsonProperty("MWT")
    private int weatherModeIndex;

    @JsonProperty("MAXP")
    private int playerCount;

    @JsonProperty("BOT")
    private boolean canHaveBots;

    @JsonProperty("TBC")
    private int targetBotCount;

    @JsonProperty("MAXBL")
    private int maxBotCount;

    @JsonProperty("MINBL")
    private int minBotCount;

    @JsonProperty("MTL")
    private long matchTimeLimit;

    @JsonProperty("MSL")
    private long matchScoreLimit;

    @JsonProperty("REG")
    private String regionCode;

    @JsonProperty("PLAYERS")
    private List<Player> players;
}
