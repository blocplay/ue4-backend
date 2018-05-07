package com.tokenplay.ue4.www.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RefreshResponse extends JSONResponse {
    @JsonProperty("LoginToken")
    final String loginToken;

    @JsonProperty("LeaveServerReason")
    final String leaveServerReason;
}
