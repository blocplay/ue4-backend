package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.ScoreConfigRecord;

public interface ScoreConfigsDB {
    Result<ScoreConfigRecord> findAll();

    ScoreConfigRecord findById(String id);

}
