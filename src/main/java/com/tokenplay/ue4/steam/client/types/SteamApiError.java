package com.tokenplay.ue4.steam.client.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Data
@AllArgsConstructor
public class SteamApiError {

    private String code;
    private String message;


}
