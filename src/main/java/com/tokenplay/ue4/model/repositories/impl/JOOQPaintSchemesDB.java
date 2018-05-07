package com.tokenplay.ue4.model.repositories.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4PaintScheme;
import com.tokenplay.ue4.model.db.tables.pojos.PaintScheme;
import com.tokenplay.ue4.model.db.tables.records.PaintSchemeRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.model.repositories.PaintSchemesDB;

@Repository
@Transactional
public class JOOQPaintSchemesDB implements PaintSchemesDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQPaintSchemesDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public List<PaintScheme> findAll(PilotRecord pilot) {
        Result<PaintSchemeRecord> records =
            jooq.selectFrom(Tue4PaintScheme.PAINT_SCHEME).where(Tue4PaintScheme.PAINT_SCHEME.PIS_PIL_ID.eq(pilot.getPilId())).fetch();
        return records.into(PaintScheme.class);
    }

    @Transactional(readOnly = true)
    @Override
    public PaintSchemeRecord findById(String id) {
        return jooq.selectFrom(Tue4PaintScheme.PAINT_SCHEME).where(Tue4PaintScheme.PAINT_SCHEME.PIS_ID.eq(id)).fetchOne();
    }

}
