package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.LoreCorporationsRecord;

public interface CorporationsDB {
    Result<LoreCorporationsRecord> findAll();

    LoreCorporationsRecord findById(String id);

    LoreCorporationsRecord findByName(String name);

    Result<LoreCorporationsRecord> findByIndustry(String industry);

    Result<LoreCorporationsRecord> findByHemisphere(String hemisphere);

    Result<LoreCorporationsRecord> findByLocation(String location);

    Result<LoreCorporationsRecord> findByLocationId(String locationId);

}
