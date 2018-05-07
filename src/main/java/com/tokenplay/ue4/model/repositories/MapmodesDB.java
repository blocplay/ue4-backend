package com.tokenplay.ue4.model.repositories;

import org.jooq.Record;
import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.MapmodeRecord;

public interface MapmodesDB {
    Result<MapmodeRecord> findAll();

    Result<MapmodeRecord> findAll(String srvId);

    MapmodeRecord findById(String srvId, String mamId);

    Record withMapAndMode(String srvId, String mamId);

    // *******************************************************************

    Result<Record> findEnabled();

    Result<Record> findServerEnabled(String serverId);

    MapmodeRecord findMapMode(String queryServerId, String mapId, String gameMode, Boolean bots);
}
