package com.tokenplay.ue4.steam.client.types.api;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AuthenticateUserTicketRS extends ApiGenericResponse {
    private AuthenticateUserTicketResponse response;
}
