package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.GearSectionRecord;

public interface GearSectionsDB {
    Result<GearSectionRecord> findAll();

    GearSectionRecord findById(String id);
}
