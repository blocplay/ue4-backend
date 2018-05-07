package com.tokenplay.ue4.www.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LaunchMessagePayload {

    @JsonProperty("MID")
    private String matchSessionId;

    @JsonProperty("SID")
    private String serverId;

    @JsonProperty("HP")
    private String port;

    @JsonProperty("ZP")
    private String zmqPort;

    @JsonProperty("IP")
    private String urlOrIp;
}
