package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4Mapcycle;
import com.tokenplay.ue4.model.db.tables.records.MapcycleRecord;
import com.tokenplay.ue4.model.repositories.MapCycleDB;

@Repository
@Transactional
public class JOOQMapCycleDB implements MapCycleDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQMapCycleDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<MapcycleRecord> findAll() {
        return jooq.selectFrom(Tue4Mapcycle.MAPCYCLE).where(Tue4Mapcycle.MAPCYCLE.MAP_SRV_ID.isNull()).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<MapcycleRecord> findBySrvId(String srvId) {
        return jooq.selectFrom(Tue4Mapcycle.MAPCYCLE).where(Tue4Mapcycle.MAPCYCLE.MAP_SRV_ID.eq(srvId)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public MapcycleRecord findByCycId(String cycId) {
        return jooq.selectFrom(Tue4Mapcycle.MAPCYCLE).where(Tue4Mapcycle.MAPCYCLE.MAP_ID.eq(cycId)).fetchOne();
    }
}
