package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4LoreGears;
import com.tokenplay.ue4.model.db.tables.records.LoreGearsRecord;
import com.tokenplay.ue4.model.repositories.GearsDB;

@Repository
@Transactional
public class JOOQGearsDB implements GearsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQGearsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreGearsRecord> findAll() {
        return jooq.selectFrom(Tue4LoreGears.LORE_GEARS).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public LoreGearsRecord findById(String id) {
        return jooq.selectFrom(Tue4LoreGears.LORE_GEARS).where(Tue4LoreGears.LORE_GEARS.LGE_ID.equalIgnoreCase(id)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public LoreGearsRecord findByName(String name) {
        return jooq.selectFrom(Tue4LoreGears.LORE_GEARS).where(Tue4LoreGears.LORE_GEARS.LGE_NAME.equalIgnoreCase(name)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreGearsRecord> findByFaction(String faction) {
        return jooq.selectFrom(Tue4LoreGears.LORE_GEARS).where(Tue4LoreGears.LORE_GEARS.LGE_FACTIONS.equalIgnoreCase(faction)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreGearsRecord> findByHemisphere(String hemisphere) {
        return jooq.selectFrom(Tue4LoreGears.LORE_GEARS).where(Tue4LoreGears.LORE_GEARS.LGE_FACTIONS.equalIgnoreCase(hemisphere)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public LoreGearsRecord findByGemId(String gemId) {
        return jooq.selectFrom(Tue4LoreGears.LORE_GEARS).where(Tue4LoreGears.LORE_GEARS.LGE_GEM_ID.equalIgnoreCase(gemId)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreGearsRecord> findByClass(String gearClass) {
        return jooq.selectFrom(Tue4LoreGears.LORE_GEARS).where(Tue4LoreGears.LORE_GEARS.LGE_CLASS.equalIgnoreCase(gearClass)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreGearsRecord> findByPledge(String pledge) {
        return jooq.selectFrom(Tue4LoreGears.LORE_GEARS).where(Tue4LoreGears.LORE_GEARS.LGE_PLEDGE_ID.eq(pledge)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreGearsRecord> findByCore(String core) {
        return jooq.selectFrom(Tue4LoreGears.LORE_GEARS).where(Tue4LoreGears.LORE_GEARS.LGE_CORE.eq(core)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreGearsRecord> findByBacker(String backer) {
        return jooq.selectFrom(Tue4LoreGears.LORE_GEARS).where(Tue4LoreGears.LORE_GEARS.LGE_CORE.eq(backer)).fetch();
    }
}
