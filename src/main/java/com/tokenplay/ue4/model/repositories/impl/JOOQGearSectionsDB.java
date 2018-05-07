package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4GearSection;
import com.tokenplay.ue4.model.db.tables.records.GearSectionRecord;
import com.tokenplay.ue4.model.repositories.GearSectionsDB;

@Repository
@Transactional
public class JOOQGearSectionsDB implements GearSectionsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQGearSectionsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<GearSectionRecord> findAll() {
        return jooq.selectFrom(Tue4GearSection.GEAR_SECTION).fetch();
    }

    @Override
    public GearSectionRecord findById(String id) {
        return jooq.selectFrom(Tue4GearSection.GEAR_SECTION).where(Tue4GearSection.GEAR_SECTION.GES_ID.eq(id)).fetchOne();
    }
}
