package com.tokenplay.ue4.www.api;

import java.time.OffsetDateTime;
import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;
import com.tokenplay.ue4.www.controllers.ui.ClientInterfaceAPI;

@Data
public class MatchData {
    private final String id;

    private final OffsetDateTime initDate;

    private final OffsetDateTime endDate;

    private final String serverName;

    private final String mapName;

    private int kills;

    private int deaths;

    private String getInitDateString() {
        return initDate != null ? ClientInterfaceAPI.df.format(initDate) : "";
    }

    private String getEndDateString() {
        return endDate != null ? ClientInterfaceAPI.df.format(endDate) : "";
    }

    @JsonValue
    public Object[] getData() {
        return new Object[] {
            id, getInitDateString(), getEndDateString(), serverName, mapName, kills, deaths};
    }

    public static final Comparator<MatchData> DESC_BY_INIT_DATE = new Comparator<MatchData>() {

        @Override
        public int compare(MatchData md1, MatchData md2) {
            if (md1.getInitDate().isBefore(md2.getInitDate())) {
                return 1;
            } else {
                return -1;
            }
        }

    };

}
