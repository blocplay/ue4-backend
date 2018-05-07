package com.tokenplay.ue4.www.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import com.tokenplay.ue4.www.controllers.MessageType;

import java.time.OffsetDateTime;

@Data
public class Message {

    @JsonProperty("Sender")
    private String sender;

    @JsonProperty("Type")
    private int type;

    @JsonProperty("Time")
    private OffsetDateTime time;

    @JsonProperty("Expiration")
    private float expiration;

    @JsonProperty("Payload")
    private MessagePayload payload;
}
