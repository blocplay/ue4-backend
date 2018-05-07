package com.tokenplay.ue4.www.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalServiceID {
    @JsonProperty("ServiceID")
    final String serviceID;

    @JsonProperty("UserID")
    final String userID;
}
