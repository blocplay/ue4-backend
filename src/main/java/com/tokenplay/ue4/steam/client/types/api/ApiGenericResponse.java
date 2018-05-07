package com.tokenplay.ue4.steam.client.types.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class ApiGenericResponse {

    private ApiError error;
}
