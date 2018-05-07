package com.tokenplay.ue4.steam.client.types;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import com.tokenplay.ue4.steam.client.types.api.ApiGenericResponse;
import com.tokenplay.ue4.steam.client.types.api.AuthenticateUserTicketRS;
import com.tokenplay.ue4.steam.client.types.api.CheckAppOwnershipRS;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ApiPath<Response extends ApiGenericResponse> {

    private final String urlTemplate;
    private final AllowedMethod allowedMethod;
    private final Class<Response> responseClass;
    private List<String> allowedParams = Collections.emptyList();

    public String getUrl(String apiPath) {
        return getUrl(null, apiPath);
    }

    public String getUrl(Map<String, String> params, String apiPath) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put("path", apiPath);
        StrSubstitutor strSubstitutor = new StrSubstitutor(params);
        return strSubstitutor.replace(urlTemplate);
    }

    ///
    public static final ApiPath<CheckAppOwnershipRS> CHECK_APP_OWNERSHIP = new ApiPath<>("${path}/ISteamUser/CheckAppOwnership/v1/",
        AllowedMethod.GET, CheckAppOwnershipRS.class, Arrays.asList("key", "steamid", "appid"));

    ///
    public static final ApiPath<AuthenticateUserTicketRS> CHECK_SESSION = new ApiPath<>("${path}/ISteamUserAuth/AuthenticateUserTicket/v1/",
        AllowedMethod.GET, AuthenticateUserTicketRS.class, Arrays.asList("key", "appid", "ticket"));
}
