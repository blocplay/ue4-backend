package com.tokenplay.ue4.www.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
abstract class AbstractMessagePayload implements MessagePayload {
    @JsonProperty("Message")
    private String message;
}
