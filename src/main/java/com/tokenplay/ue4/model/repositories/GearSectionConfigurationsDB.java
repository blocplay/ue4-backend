package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.GearSectionConfigurationRecord;

public interface GearSectionConfigurationsDB {
    Result<GearSectionConfigurationRecord> findAll(String sectionId);

    GearSectionConfigurationRecord findById(String id);
}
