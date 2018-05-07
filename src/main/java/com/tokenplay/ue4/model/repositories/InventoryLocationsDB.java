package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.InventoryLocationRecord;

public interface InventoryLocationsDB {
    Result<InventoryLocationRecord> findAll();

    InventoryLocationRecord findById(String id);

    Result<InventoryLocationRecord> findByIdList(String id);

}
