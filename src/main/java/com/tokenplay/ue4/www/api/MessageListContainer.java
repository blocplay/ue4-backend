package com.tokenplay.ue4.www.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class MessageListContainer {

    @JsonProperty("Type")
    private int type;

    @JsonProperty("TimeStamp")
    private OffsetDateTime timestamp;

    @JsonProperty("Messages")
    private List<Message> messages;
}
