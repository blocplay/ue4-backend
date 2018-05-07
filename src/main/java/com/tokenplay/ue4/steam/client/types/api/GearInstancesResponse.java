package com.tokenplay.ue4.steam.client.types.api;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GearInstancesResponse {
    @JsonProperty("Collections")
    private Map<String, GearInstanceResponse> instancesList;
}
