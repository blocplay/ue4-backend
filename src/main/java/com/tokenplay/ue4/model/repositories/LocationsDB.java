package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.LoreLocationsRecord;

;

public interface LocationsDB {
    Result<LoreLocationsRecord> findAll();

    LoreLocationsRecord findById(String id);

    Result<LoreLocationsRecord> findByStarId(String id);

    Result<LoreLocationsRecord> findByPlanetId(String id);

    Result<LoreLocationsRecord> findByHemisphere(String name);

    Result<LoreLocationsRecord> findByClass(String name);

    Result<LoreLocationsRecord> findByLeague(String name);

    LoreLocationsRecord findByName(String name);

}
