package com.tokenplay.ue4.www.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PilotStatsResponse extends JSONResponse {
    public static DecimalFormat ratioFormatter = new DecimalFormat("##.##");

    @JsonProperty("num_matches")
    final Number matches;

    @JsonProperty("num_kills")
    final Number kills;

    @JsonProperty("num_deaths")
    final Number deaths;

    @JsonProperty("kd_ratio")
    public String getKdRatio() {
        if (kills != null && kills.longValue() > 0) {
            BigDecimal killsB = new BigDecimal(kills.longValue());
            BigDecimal deathsB = new BigDecimal(deaths != null ? deaths.longValue() : 0);
            return ratioFormatter.format(killsB.divide(deathsB, 2, RoundingMode.HALF_UP));
        } else {
            return "0.0";
        }
    }
}
