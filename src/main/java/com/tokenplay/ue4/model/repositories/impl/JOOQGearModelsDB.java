package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4GearModel;
import com.tokenplay.ue4.model.db.tables.records.GearModelRecord;
import com.tokenplay.ue4.model.repositories.GearModelsDB;

@Repository
@Transactional
public class JOOQGearModelsDB implements GearModelsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQGearModelsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<GearModelRecord> findAll() {
        return jooq.selectFrom(Tue4GearModel.GEAR_MODEL).fetch();
    }

    @Override
    public GearModelRecord findById(String id) {
        return jooq.selectFrom(Tue4GearModel.GEAR_MODEL).where(Tue4GearModel.GEAR_MODEL.GEM_ID.eq(id)).fetchOne();
    }
}
