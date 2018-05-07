package com.tokenplay.ue4.www.api;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClusterPopulationResponse extends JSONResponse {
    private Map<String, Long> serversByZone;
}
