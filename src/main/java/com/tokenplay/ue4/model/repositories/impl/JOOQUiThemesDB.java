package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4UiTheme;
import com.tokenplay.ue4.model.db.tables.records.UiThemeRecord;
import com.tokenplay.ue4.model.repositories.UiThemesDB;

@Repository
@Transactional
public class JOOQUiThemesDB implements UiThemesDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQUiThemesDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<UiThemeRecord> findAll() {
        return jooq.selectFrom(Tue4UiTheme.UI_THEME).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public UiThemeRecord findById(String id) {
        return jooq.selectFrom(Tue4UiTheme.UI_THEME).where(Tue4UiTheme.UI_THEME.UTH_ID.eq(id)).fetchOne();
    }

}
