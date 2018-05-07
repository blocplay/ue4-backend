package com.tokenplay.ue4.www.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class FindMatchSessionResponse extends JSONResponse {
    @JsonProperty("MatchSession")
    final MatchSession matchSession;
}
