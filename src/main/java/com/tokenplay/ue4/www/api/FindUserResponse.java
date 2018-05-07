package com.tokenplay.ue4.www.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class FindUserResponse extends JSONResponse {
    @JsonProperty("ExternalServiceIDs")
    final List<ExternalServiceID> externalServiceIDs;

    @JsonProperty("Name")
    final String name;

    @JsonProperty("UserID")
    final String userId;

    @JsonProperty("Callsign")
    final String callsign;
}
