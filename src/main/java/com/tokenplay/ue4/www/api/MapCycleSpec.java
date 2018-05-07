package com.tokenplay.ue4.www.api;

import lombok.Data;

@Data
public class MapCycleSpec {
    private final String mapName;

    private final String modeName;

    private final boolean botsEnabled;

    private final Long matchTime;
}
