package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4LoreLocations;
import com.tokenplay.ue4.model.db.tables.records.LoreLocationsRecord;
import com.tokenplay.ue4.model.repositories.LocationsDB;

@Repository
@Transactional
public class JOOQLocationsDB implements LocationsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQLocationsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreLocationsRecord> findAll() {
        return jooq.selectFrom(Tue4LoreLocations.LORE_LOCATIONS).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public LoreLocationsRecord findById(String id) {
        return jooq.selectFrom(Tue4LoreLocations.LORE_LOCATIONS).where(Tue4LoreLocations.LORE_LOCATIONS.LLOC_ID.equalIgnoreCase(id)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreLocationsRecord> findByPlanetId(String id) {
        return jooq.selectFrom(Tue4LoreLocations.LORE_LOCATIONS).where(Tue4LoreLocations.LORE_LOCATIONS.LLOC_LPLA_ID.equalIgnoreCase(id)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreLocationsRecord> findByStarId(String id) {
        return jooq.selectFrom(Tue4LoreLocations.LORE_LOCATIONS).where(Tue4LoreLocations.LORE_LOCATIONS.LLOC_STAR_ID.equalIgnoreCase(id)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreLocationsRecord> findByHemisphere(String name) {
        return jooq.selectFrom(Tue4LoreLocations.LORE_LOCATIONS).where(Tue4LoreLocations.LORE_LOCATIONS.LLOC_HEMISPHERE.equalIgnoreCase(name))
            .fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreLocationsRecord> findByClass(String name) {
        return jooq.selectFrom(Tue4LoreLocations.LORE_LOCATIONS).where(Tue4LoreLocations.LORE_LOCATIONS.LLOC_CLASS.equalIgnoreCase(name)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreLocationsRecord> findByLeague(String name) {
        return jooq.selectFrom(Tue4LoreLocations.LORE_LOCATIONS).where(Tue4LoreLocations.LORE_LOCATIONS.LLOC_LEAGUE.equalIgnoreCase(name)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public LoreLocationsRecord findByName(String name) {
        return jooq.selectFrom(Tue4LoreLocations.LORE_LOCATIONS).where(Tue4LoreLocations.LORE_LOCATIONS.LLOC_NAME.equalIgnoreCase(name)).fetchOne();
    }
}
