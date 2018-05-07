package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4LoreCircuits;
import com.tokenplay.ue4.model.db.tables.records.LoreCircuitsRecord;
import com.tokenplay.ue4.model.repositories.CircuitsDB;

@Repository
@Transactional
public class JOOQCircuitsDB implements CircuitsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQCircuitsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreCircuitsRecord> findAll() {
        return jooq.selectFrom(Tue4LoreCircuits.LORE_CIRCUITS).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public LoreCircuitsRecord findById(String id) {
        return jooq.selectFrom(Tue4LoreCircuits.LORE_CIRCUITS).where(Tue4LoreCircuits.LORE_CIRCUITS.LCIR_ID.equalIgnoreCase(id)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreCircuitsRecord> findByPlanetId(String id) {
        return jooq.selectFrom(Tue4LoreCircuits.LORE_CIRCUITS).where(Tue4LoreCircuits.LORE_CIRCUITS.LCIR_LPLA_ID.equalIgnoreCase(id)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreCircuitsRecord> findByHemisphere(String name) {
        return jooq.selectFrom(Tue4LoreCircuits.LORE_CIRCUITS).where(Tue4LoreCircuits.LORE_CIRCUITS.LCIR_HEMISPHERE.equalIgnoreCase(name)).fetch();

    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreCircuitsRecord> findByClass(String name) {
        return jooq.selectFrom(Tue4LoreCircuits.LORE_CIRCUITS).where(Tue4LoreCircuits.LORE_CIRCUITS.LCIR_CLASS.equalIgnoreCase(name)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreCircuitsRecord> findByLeague(String name) {
        return jooq.selectFrom(Tue4LoreCircuits.LORE_CIRCUITS).where(Tue4LoreCircuits.LORE_CIRCUITS.LCIR_LEAGUE.equalIgnoreCase(name)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public LoreCircuitsRecord findByName(String name) {
        return jooq.selectFrom(Tue4LoreCircuits.LORE_CIRCUITS).where(Tue4LoreCircuits.LORE_CIRCUITS.LCIR_NAME.equalIgnoreCase(name)).fetchOne();
    }
}
