package com.tokenplay.ue4.www.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class JSONResponse {
    @JsonProperty("Success")
    boolean success = false;

    @JsonProperty("Error")
    String error;
}
