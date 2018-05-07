package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.LoreCircuitsRecord;

public interface CircuitsDB {
    Result<LoreCircuitsRecord> findAll();

    LoreCircuitsRecord findById(String id);

    Result<LoreCircuitsRecord> findByPlanetId(String id);

    Result<LoreCircuitsRecord> findByHemisphere(String name);

    Result<LoreCircuitsRecord> findByClass(String name);

    Result<LoreCircuitsRecord> findByLeague(String name);

    LoreCircuitsRecord findByName(String name);

}
