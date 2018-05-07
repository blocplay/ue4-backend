package com.tokenplay.ue4.model.repositories;

import org.jooq.Record;
import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.ChampionshipRecord;

public interface ChampionshipsDB {
    Result<ChampionshipRecord> findAll();

    ChampionshipRecord findById(String id);

    Result<Record> getPilots(ChampionshipRecord championship);

}
