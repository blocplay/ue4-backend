package com.tokenplay.ue4.steam.client.types.api;

import lombok.Data;

@Data
public class AuthenticateUserTicketResponse {
    SteamApiErrorResponse error;
    SteamApiSessionResponse params;
}
