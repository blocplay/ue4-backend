package com.tokenplay.ue4.www.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public abstract class AbstractMatchSessionMessagePayload extends AbstractMessagePayload {

    @JsonProperty("MatchSessionId")
    private String matchSessionId;

}
