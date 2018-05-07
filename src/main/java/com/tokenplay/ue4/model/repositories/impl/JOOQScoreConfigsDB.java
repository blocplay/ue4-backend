package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4ScoreConfig;
import com.tokenplay.ue4.model.db.tables.records.ScoreConfigRecord;
import com.tokenplay.ue4.model.repositories.ScoreConfigsDB;

@Repository
@Transactional
public class JOOQScoreConfigsDB implements ScoreConfigsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQScoreConfigsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<ScoreConfigRecord> findAll() {
        return jooq.selectFrom(Tue4ScoreConfig.SCORE_CONFIG).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public ScoreConfigRecord findById(String id) {
        return jooq.selectFrom(Tue4ScoreConfig.SCORE_CONFIG).where(Tue4ScoreConfig.SCORE_CONFIG.SCO_ID.eq(id)).fetchOne();
    }

}
