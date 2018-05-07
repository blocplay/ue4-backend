package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4LoreHolidays;
import com.tokenplay.ue4.model.db.tables.records.LoreHolidaysRecord;
import com.tokenplay.ue4.model.repositories.HolidaysDB;

@Repository
@Transactional
public class JOOQHolidaysDB implements HolidaysDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQHolidaysDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreHolidaysRecord> findAll() {
        return jooq.selectFrom(Tue4LoreHolidays.LORE_HOLIDAYS).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public LoreHolidaysRecord findById(String id) {
        return jooq.selectFrom(Tue4LoreHolidays.LORE_HOLIDAYS).where(Tue4LoreHolidays.LORE_HOLIDAYS.LHOL_ID.equalIgnoreCase(id)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public LoreHolidaysRecord findByName(String name) {
        return jooq.selectFrom(Tue4LoreHolidays.LORE_HOLIDAYS).where(Tue4LoreHolidays.LORE_HOLIDAYS.LHOL_NAME.equalIgnoreCase(name)).fetchOne();
    }
}
