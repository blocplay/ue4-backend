package com.tokenplay.ue4.model.repositories.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4EventScore;
import com.tokenplay.ue4.model.db.tables.pojos.EventScore;
import com.tokenplay.ue4.model.db.tables.records.EventScoreRecord;
import com.tokenplay.ue4.model.repositories.EventScoresDB;

@Repository
@Transactional
public class JOOQEventScoresDB implements EventScoresDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQEventScoresDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventScore> findAll(String scoId) {
        Result<EventScoreRecord> records = jooq.selectFrom(Tue4EventScore.EVENT_SCORE).where(Tue4EventScore.EVENT_SCORE.ESC_SCO_ID.eq(scoId)).fetch();
        return records.into(EventScore.class);
    }

    @Transactional(readOnly = true)
    @Override
    public EventScoreRecord findById(String id) {
        return jooq.selectFrom(Tue4EventScore.EVENT_SCORE).where(Tue4EventScore.EVENT_SCORE.ESC_ID.eq(id)).fetchOne();
    }

}
