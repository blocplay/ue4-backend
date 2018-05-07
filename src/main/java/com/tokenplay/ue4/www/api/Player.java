package com.tokenplay.ue4.www.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Player {

    @JsonProperty("PID")
    private String id;

    @JsonProperty("PTM")
    private int teamIndex;
}
