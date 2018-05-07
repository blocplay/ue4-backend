package com.tokenplay.ue4.steam.client.types.api;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import com.tokenplay.ue4.model.db.tables.records.GearInstanceRecord;
import com.tokenplay.ue4.model.db.tables.records.GearModelRecord;

@Data
@JsonInclude(Include.NON_NULL)
public class GearInstanceResponse {
    @JsonProperty("Name")
    private String geiName;
    @JsonProperty("ICID")
    private String geiId;
    @JsonProperty("DCID")
    private String geiGemId;
    @JsonProperty("PSD")
    private String geiDefaultScheme;
    @JsonProperty("SENG")
    private String geiGesEngine;
    @JsonProperty("SFL")
    private String geiGesFootLeft;
    @JsonProperty("SFR")
    private String geiGesFootRight;
    @JsonProperty("SEFT")
    private String geiGesFuelTank;
    @JsonProperty("SHL")
    private String geiGesHandLeft;
    @JsonProperty("SHR")
    private String geiGesHandRight;
    @JsonProperty("SHED")
    private String geiGesHead;
    @JsonProperty("SHIP")
    private String geiGesHip;
    @JsonProperty("SADL")
    private String geiGesLowerArmLeft;
    @JsonProperty("SADR")
    private String geiGesLowerArmRight;
    @JsonProperty("SLDL")
    private String geiGesLowerLegLeft;
    @JsonProperty("SLDR")
    private String geiGesLowerLegRight;
    @JsonProperty("SEPL")
    private String geiGesPylonLeft;
    @JsonProperty("SEPR")
    private String geiGesPylonRight;
    @JsonProperty("SSHL")
    private String geiGesShoulderLeft;
    @JsonProperty("SSHR")
    private String geiGesShoulderRight;
    @JsonProperty("STOR")
    private String geiGesTorso;
    @JsonProperty("SAUL")
    private String geiGesUpperArmLeft;
    @JsonProperty("SAUR")
    private String geiGesUpperArmRight;
    @JsonProperty("SLUL")
    private String geiGesUpperLegLeft;
    @JsonProperty("SLUR")
    private String geiGesUpperLegRight;
    @JsonProperty("MEHL")
    private String iiGesEngineHeavyMountLeft;
    @JsonProperty("MEHR")
    private String iiGesEngineHeavyMountRight;
    @JsonProperty("MFT")
    private String iiGesFuelTankStorage;
    @JsonProperty("MLC")
    private String iiGesLeftCollarMount;
    @JsonProperty("MRC")
    private String iiGesRightCollarMount;
    @JsonProperty("MULOL")
    private String iiGesUpperLegOutLeft;
    @JsonProperty("MULOR")
    private String iiGesUpperLegOutRight;

    public static GearInstanceResponse of(Pair<GearInstanceRecord, GearModelRecord> pair) {
        GearInstanceResponse response = new GearInstanceResponse();
        GearInstanceRecord gearInstanceRecord = pair.getLeft();
        GearModelRecord gearModelRecord = pair.getRight();
        response.setGeiName(gearInstanceRecord.getGeiName());
        response.setGeiId(gearInstanceRecord.getGeiId());
        response.setGeiGemId(gearInstanceRecord.getGeiGemId());
        response.setGeiDefaultScheme(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiDefaultScheme(), ""));
        response.setGeiGesEngine(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesEngine(), gearModelRecord.getGemGesEngine()));
        response.setGeiGesFootLeft(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesFootLeft(), gearModelRecord.getGemGesFootLeft()));
        response.setGeiGesFootRight(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesFootRight(), gearModelRecord.getGemGesFootRight()));
        response.setGeiGesFuelTank(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesFuelTank(), gearModelRecord.getGemGesFuelTank()));
        response.setGeiGesHandLeft(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesHandLeft(), gearModelRecord.getGemGesHandLeft()));
        response.setGeiGesHandRight(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesHandRight(), gearModelRecord.getGemGesHandRight()));
        response.setGeiGesHead(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesHead(), gearModelRecord.getGemGesHead()));
        response.setGeiGesHip(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesHip(), gearModelRecord.getGemGesHip()));
        response
            .setGeiGesLowerArmLeft(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesLowerArmLeft(), gearModelRecord.getGemGesLowerArmLeft()));
        response.setGeiGesLowerArmRight(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesLowerArmRight(),
            gearModelRecord.getGemGesLowerArmRight()));
        response
            .setGeiGesLowerLegLeft(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesLowerLegLeft(), gearModelRecord.getGemGesLowerLegLeft()));
        response.setGeiGesLowerLegRight(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesLowerLegRight(),
            gearModelRecord.getGemGesLowerLegRight()));
        response.setGeiGesPylonLeft(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesPylonLeft(), gearModelRecord.getGemGesPylonLeft()));
        response.setGeiGesPylonRight(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesPylonRight(), gearModelRecord.getGemGesPylonRight()));
        response
            .setGeiGesShoulderLeft(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesShoulderLeft(), gearModelRecord.getGemGesShoulderLeft()));
        response.setGeiGesShoulderRight(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesShoulderRight(),
            gearModelRecord.getGemGesShoulderRight()));
        response.setGeiGesTorso(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesTorso(), gearModelRecord.getGemGesTorso()));
        response
            .setGeiGesUpperArmLeft(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesUpperArmLeft(), gearModelRecord.getGemGesUpperArmLeft()));
        response.setGeiGesUpperArmRight(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesUpperArmRight(),
            gearModelRecord.getGemGesUpperArmRight()));
        response
            .setGeiGesUpperLegLeft(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesUpperLegLeft(), gearModelRecord.getGemGesUpperLegLeft()));
        response.setGeiGesUpperLegRight(StringUtils.defaultIfBlank(gearInstanceRecord.getGeiGesUpperLegRight(),
            gearModelRecord.getGemGesUpperLegRight()));
        response.setIiGesEngineHeavyMountLeft(StringUtils.defaultIfBlank(gearInstanceRecord.getIiGesEngineHeavyMountLeft(), ""));
        response.setIiGesEngineHeavyMountRight(StringUtils.defaultIfBlank(gearInstanceRecord.getIiGesEngineHeavyMountRight(), ""));
        response.setIiGesFuelTankStorage(StringUtils.defaultIfBlank(gearInstanceRecord.getIiGesFuelTankStorage(), ""));
        response.setIiGesLeftCollarMount(StringUtils.defaultIfBlank(gearInstanceRecord.getIiGesLeftCollarMount(), ""));
        response.setIiGesRightCollarMount(StringUtils.defaultIfBlank(gearInstanceRecord.getIiGesRightCollarMount(), ""));
        response.setIiGesUpperLegOutLeft(StringUtils.defaultIfBlank(gearInstanceRecord.getIiGesUpperLegOutLeft(), ""));
        response.setIiGesUpperLegOutRight(StringUtils.defaultIfBlank(gearInstanceRecord.getIiGesUpperLegOutRight(), ""));
        return response;
    }
}
