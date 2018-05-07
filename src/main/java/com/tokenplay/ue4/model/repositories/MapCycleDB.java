package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.MapcycleRecord;

public interface MapCycleDB {
    Result<MapcycleRecord> findAll();

    Result<MapcycleRecord> findBySrvId(String srvId);

    MapcycleRecord findByCycId(String cycleId);

}
