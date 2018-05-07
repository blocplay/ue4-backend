package com.tokenplay.ue4.www.api;

import lombok.Data;

@Data
public class ServerStatusData {
    private String srvAlias;
    private String srvName;
    private String srvId;
    private String srvDescription;
    private String srvRunningVersion;
    private String championshipName;
    private Long srvMaxPlayers;
    private boolean srvDevelopment;
    private boolean registeredForChampionship;
    private boolean secured;
    private boolean permitted;
    private String map;
    private String mode;
    private String dateInit;
    private Long activePilots;
    private Long botsAmount;

    public Long getPilotCount() {
        return activePilots;
    }

    public void setPilotCount(Long pilotCount) {
        activePilots = pilotCount;
    }
}
