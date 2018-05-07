package com.tokenplay.ue4.www.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MatchSessionCreationResponse extends JSONResponse {
    @JsonProperty("MatchSessionId")
    final String matchSessionId;
}
