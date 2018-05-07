package com.tokenplay.ue4.www.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JoinServerResponse extends JSONResponse {
    @JsonProperty("ServerIP")
    final String ip;

    @JsonProperty("ServerID")
    final String id;

    @JsonProperty("ServerAlias")
    final String alias;

    @JsonProperty("ServerName")
    final String name;
}
