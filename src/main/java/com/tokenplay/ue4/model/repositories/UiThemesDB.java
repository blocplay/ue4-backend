package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.UiThemeRecord;

public interface UiThemesDB {
    Result<UiThemeRecord> findAll();

    UiThemeRecord findById(String id);

}
