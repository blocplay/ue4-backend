package com.tokenplay.ue4.www.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LoginResponse extends JSONResponse {
    @JsonProperty("LoginToken")
    final String loginToken;

    @JsonProperty("Callsign")
    final String callsign;

    @JsonProperty("Username")
    final String username;

    @JsonProperty("EncryptPwd")
    final String encryptPwd;

    @JsonProperty("ChatDisabled")
    final boolean chatDisabled;

    @JsonProperty("RequestsDisabled")
    final boolean requestsDisabled;

    @JsonProperty("Theme")
    final String theme;

    @JsonProperty("DefaultScheme")
    final String defaultScheme;

    @JsonProperty("UseCustomScheme")
    final boolean useCustomScheme;
}
