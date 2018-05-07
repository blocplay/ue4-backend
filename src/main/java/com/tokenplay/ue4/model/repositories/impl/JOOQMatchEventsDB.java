package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4MatchEvent;
import com.tokenplay.ue4.model.db.tables.records.MatchEventRecord;
import com.tokenplay.ue4.model.repositories.MatchEventsDB;

@Repository
@Transactional
public class JOOQMatchEventsDB implements MatchEventsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQMatchEventsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<MatchEventRecord> findAll() {
        return jooq.selectFrom(Tue4MatchEvent.MATCH_EVENT).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public MatchEventRecord findById(String id) {
        return jooq.selectFrom(Tue4MatchEvent.MATCH_EVENT).where(Tue4MatchEvent.MATCH_EVENT.MEV_ID.eq(id)).fetchOne();
    }

}
