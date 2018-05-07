package com.tokenplay.ue4.www.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServerStatusResponse extends JSONResponse {
    @JsonProperty("current_server_name")
    final String serverName;

    @JsonProperty("current_map_name")
    final String mapName;

    @JsonProperty("current_game_mode")
    final String gameMode;

    @JsonProperty("current_max_players")
    final long maxPlayers;

    @JsonProperty("current_number_pilots")
    final int numberOfPilots;

    @JsonProperty("current_match_init_date")
    private final String matchInitDate;
}
