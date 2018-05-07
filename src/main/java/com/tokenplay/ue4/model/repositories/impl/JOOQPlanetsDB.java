package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4LorePlanets;
import com.tokenplay.ue4.model.db.tables.records.LorePlanetsRecord;
import com.tokenplay.ue4.model.repositories.PlanetsDB;

@Repository
@Transactional
public class JOOQPlanetsDB implements PlanetsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQPlanetsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LorePlanetsRecord> findAll() {
        return jooq.selectFrom(Tue4LorePlanets.LORE_PLANETS).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public LorePlanetsRecord findById(String id) {
        return jooq.selectFrom(Tue4LorePlanets.LORE_PLANETS).where(Tue4LorePlanets.LORE_PLANETS.LPLA_ID.equalIgnoreCase(id)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LorePlanetsRecord> findByStarId(String id) {
        return jooq.selectFrom(Tue4LorePlanets.LORE_PLANETS).where(Tue4LorePlanets.LORE_PLANETS.LPLA_LSTAR_ID.equalIgnoreCase(id)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LorePlanetsRecord> findByOwner(String owner) {
        return jooq.selectFrom(Tue4LorePlanets.LORE_PLANETS).where(Tue4LorePlanets.LORE_PLANETS.LPLA_OWNER.equalIgnoreCase(owner)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public LorePlanetsRecord findByName(String name) {
        return jooq.selectFrom(Tue4LorePlanets.LORE_PLANETS).where(Tue4LorePlanets.LORE_PLANETS.LPLA_NAME.equalIgnoreCase(name)).fetchOne();
    }
}
