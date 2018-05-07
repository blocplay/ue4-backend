package com.tokenplay.ue4.steam.client.types.api;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CheckAppOwnershipRS extends ApiGenericResponse {
    private AppOwnership appownership;

    public boolean ownsGame() {
        return appownership != null && appownership.isOwnerOfTheApp();
    }
}
