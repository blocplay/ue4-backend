package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.MatchEventRecord;

public interface MatchEventsDB {
    Result<MatchEventRecord> findAll();

    MatchEventRecord findById(String id);

}
