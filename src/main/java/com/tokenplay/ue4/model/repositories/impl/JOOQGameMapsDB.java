package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4Map;
import com.tokenplay.ue4.model.db.tables.records.MapRecord;
import com.tokenplay.ue4.model.repositories.GameMapsDB;

@Repository
@Transactional
public class JOOQGameMapsDB implements GameMapsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQGameMapsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<MapRecord> findAll() {
        return jooq.selectFrom(Tue4Map.MAP).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public MapRecord findById(String id) {
        return jooq.selectFrom(Tue4Map.MAP).where(Tue4Map.MAP.MAP_ID.eq(id)).fetchOne();
    }

}
