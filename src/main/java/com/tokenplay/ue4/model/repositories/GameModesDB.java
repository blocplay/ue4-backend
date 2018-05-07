package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.GameModeRecord;

public interface GameModesDB {
    Result<GameModeRecord> findAll();

    GameModeRecord findById(String id);

}
