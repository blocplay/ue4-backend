package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4GearSectionConfiguration;
import com.tokenplay.ue4.model.db.tables.records.GearSectionConfigurationRecord;
import com.tokenplay.ue4.model.repositories.GearSectionConfigurationsDB;

@Repository
@Transactional
public class JOOQGearSectionConfigurationsDB implements GearSectionConfigurationsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQGearSectionConfigurationsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<GearSectionConfigurationRecord> findAll(String sectionId) {
        return jooq.selectFrom(Tue4GearSectionConfiguration.GEAR_SECTION_CONFIGURATION)
            .where(Tue4GearSectionConfiguration.GEAR_SECTION_CONFIGURATION.GSC_GES_ID.eq(sectionId)).fetch();
    }

    @Override
    public GearSectionConfigurationRecord findById(String id) {
        return jooq.selectFrom(Tue4GearSectionConfiguration.GEAR_SECTION_CONFIGURATION)
            .where(Tue4GearSectionConfiguration.GEAR_SECTION_CONFIGURATION.GSC_ID.eq(id)).fetchOne();
    }
}
