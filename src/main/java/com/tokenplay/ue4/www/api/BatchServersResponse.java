package com.tokenplay.ue4.www.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BatchServersResponse extends JSONResponse {
    @JsonProperty("Command")
    final String command;

    @JsonProperty("Response")
    final List<JSONResponse> responses;
}
