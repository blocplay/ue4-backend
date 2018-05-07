package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.MapRecord;

public interface GameMapsDB {
    Result<MapRecord> findAll();

    MapRecord findById(String id);

}
