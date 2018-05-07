package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.LoreStarsRecord;

public interface StarsDB {
    Result<LoreStarsRecord> findAll();

    LoreStarsRecord findById(String id);

    LoreStarsRecord findByName(String name);

    Result<LoreStarsRecord> findByStarType(String type);

}
