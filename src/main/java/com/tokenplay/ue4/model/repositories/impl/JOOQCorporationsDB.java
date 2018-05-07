package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4LoreCorporations;
import com.tokenplay.ue4.model.db.tables.records.LoreCorporationsRecord;
import com.tokenplay.ue4.model.repositories.CorporationsDB;

@Repository
@Transactional
public class JOOQCorporationsDB implements CorporationsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQCorporationsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreCorporationsRecord> findAll() {
        return jooq.selectFrom(Tue4LoreCorporations.LORE_CORPORATIONS).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public LoreCorporationsRecord findById(String id) {
        return jooq.selectFrom(Tue4LoreCorporations.LORE_CORPORATIONS).where(Tue4LoreCorporations.LORE_CORPORATIONS.LCO_ID.equalIgnoreCase(id))
            .fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public LoreCorporationsRecord findByName(String name) {
        return jooq.selectFrom(Tue4LoreCorporations.LORE_CORPORATIONS).where(Tue4LoreCorporations.LORE_CORPORATIONS.LCO_NAME.equalIgnoreCase(name))
            .fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreCorporationsRecord> findByIndustry(String industry) {
        return jooq.selectFrom(Tue4LoreCorporations.LORE_CORPORATIONS)
            .where(Tue4LoreCorporations.LORE_CORPORATIONS.LCO_INDUSTRY.equalIgnoreCase(industry)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreCorporationsRecord> findByHemisphere(String hemisphere) {
        return jooq.selectFrom(Tue4LoreCorporations.LORE_CORPORATIONS)
            .where(Tue4LoreCorporations.LORE_CORPORATIONS.LCO_HEMISPHERE.equalIgnoreCase(hemisphere)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreCorporationsRecord> findByLocation(String location) {
        return jooq.selectFrom(Tue4LoreCorporations.LORE_CORPORATIONS)
            .where(Tue4LoreCorporations.LORE_CORPORATIONS.LCO_LOCATION.equalIgnoreCase(location)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreCorporationsRecord> findByLocationId(String locationId) {
        return jooq.selectFrom(Tue4LoreCorporations.LORE_CORPORATIONS)
            .where(Tue4LoreCorporations.LORE_CORPORATIONS.LLOC_ID.equalIgnoreCase(locationId)).fetch();
    }
}
