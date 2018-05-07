package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.LoreHolidaysRecord;

;

public interface HolidaysDB {
    Result<LoreHolidaysRecord> findAll();

    LoreHolidaysRecord findById(String id);

    LoreHolidaysRecord findByName(String name);

}
