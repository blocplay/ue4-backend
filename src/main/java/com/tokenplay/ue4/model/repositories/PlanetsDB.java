package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.LorePlanetsRecord;

public interface PlanetsDB {
    Result<LorePlanetsRecord> findAll();

    LorePlanetsRecord findById(String id);

    Result<LorePlanetsRecord> findByStarId(String id);

    Result<LorePlanetsRecord> findByOwner(String owner);

    LorePlanetsRecord findByName(String name);

}
