package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.InventoryObjectRecord;

public interface InventoryObjectsDB {
    Result<InventoryObjectRecord> findAll();

    InventoryObjectRecord findById(String id);

    Result<InventoryObjectRecord> findByIdList(String id);

}
