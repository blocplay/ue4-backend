package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4LoreStars;
import com.tokenplay.ue4.model.db.tables.records.LoreStarsRecord;
import com.tokenplay.ue4.model.repositories.StarsDB;

@Repository
@Transactional
public class JOOQStarsDB implements StarsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQStarsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreStarsRecord> findAll() {
        return jooq.selectFrom(Tue4LoreStars.LORE_STARS).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public LoreStarsRecord findById(String id) {
        return jooq.selectFrom(Tue4LoreStars.LORE_STARS).where(Tue4LoreStars.LORE_STARS.LSTAR_ID.equalIgnoreCase(id)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public LoreStarsRecord findByName(String name) {
        return jooq.selectFrom(Tue4LoreStars.LORE_STARS).where(Tue4LoreStars.LORE_STARS.LSTAR_ID.equalIgnoreCase(name)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreStarsRecord> findByStarType(String type) {
        return jooq.selectFrom(Tue4LoreStars.LORE_STARS).where(Tue4LoreStars.LORE_STARS.LSTAR_ID.equalIgnoreCase(type)).fetch();
    }


}
