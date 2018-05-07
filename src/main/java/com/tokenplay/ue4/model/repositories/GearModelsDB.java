package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.GearModelRecord;

public interface GearModelsDB {
    Result<GearModelRecord> findAll();

    GearModelRecord findById(String id);
}
