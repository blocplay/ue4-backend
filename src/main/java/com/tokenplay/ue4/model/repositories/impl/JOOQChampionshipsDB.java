package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4Championship;
import com.tokenplay.ue4.model.db.tables.Tue4Pilot;
import com.tokenplay.ue4.model.db.tables.Tue4Registration;
import com.tokenplay.ue4.model.db.tables.records.ChampionshipRecord;
import com.tokenplay.ue4.model.repositories.ChampionshipsDB;

@Repository
@Transactional
public class JOOQChampionshipsDB implements ChampionshipsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQChampionshipsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<ChampionshipRecord> findAll() {
        return jooq.selectFrom(Tue4Championship.CHAMPIONSHIP).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public ChampionshipRecord findById(String id) {
        return jooq.selectFrom(Tue4Championship.CHAMPIONSHIP).where(Tue4Championship.CHAMPIONSHIP.CHA_ID.eq(id)).fetchOne();
    }

    @Override
    public Result<Record> getPilots(ChampionshipRecord championship) {
        return jooq
            .select()
            .from(Tue4Pilot.PILOT)
            .join(Tue4Registration.REGISTRATION)
            .on(Tue4Registration.REGISTRATION.REG_PIL_ID.eq(Tue4Pilot.PILOT.PIL_ID).and(
                Tue4Registration.REGISTRATION.REG_CHA_ID.eq(championship.getChaId()))).fetch();
    }

}
