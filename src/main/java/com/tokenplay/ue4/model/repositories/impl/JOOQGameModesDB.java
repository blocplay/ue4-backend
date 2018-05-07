package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4GameMode;
import com.tokenplay.ue4.model.db.tables.records.GameModeRecord;
import com.tokenplay.ue4.model.repositories.GameModesDB;

@Repository
@Transactional
public class JOOQGameModesDB implements GameModesDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQGameModesDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<GameModeRecord> findAll() {
        return jooq.selectFrom(Tue4GameMode.GAME_MODE).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public GameModeRecord findById(String id) {
        return jooq.selectFrom(Tue4GameMode.GAME_MODE).where(Tue4GameMode.GAME_MODE.GAM_ID.eq(id)).fetchOne();
    }

}
