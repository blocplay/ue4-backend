package com.tokenplay.ue4.www.api;

import java.sql.Timestamp;
import java.text.Collator;
import java.time.OffsetDateTime;
import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;
import com.tokenplay.ue4.www.controllers.ui.ClientInterfaceAPI;

@Data
public class ServerData {
    private final String alias;

    private final String name;

    private final String id;

    private final String description;

    private final String version;

    private final String mapName;

    private final String gameMode;

    private final String status;

    private final OffsetDateTime matchInitDate;

    private final long maxPlayers;

    private final long numberOfPilots;

    private final long ingamePilots;

    private final long numberOfBots;

    private final boolean development;

    private final String championship;

    private final boolean registeredForChampionship;

    @JsonValue
    public Object[] getData() {
        return new Object[] {
            alias, name, id, "", "", description, version, mapName, gameMode, status, "",
            matchInitDate != null ? ClientInterfaceAPI.df.format(matchInitDate) : "", maxPlayers, numberOfPilots, ingamePilots, numberOfBots,
            development, championship, registeredForChampionship};
    }

    public static final Comparator<ServerData> DESC_BY_NAME = new Comparator<ServerData>() {
        @Override
        public int compare(ServerData md1, ServerData md2) {
            return Collator.getInstance().compare(md1.getAlias(), md2.getAlias());
        }
    };

    public static final Comparator<ServerData> DESC_BY_INIT_DATE = new Comparator<ServerData>() {
        @Override
        public int compare(ServerData md1, ServerData md2) {
            if (md1.getMatchInitDate().isBefore(md2.getMatchInitDate())) {
                return 1;
            } else {
                return -1;
            }
        }
    };

}
