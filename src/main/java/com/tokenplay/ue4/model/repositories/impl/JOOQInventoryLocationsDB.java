package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4InventoryLocation;
import com.tokenplay.ue4.model.db.tables.records.InventoryLocationRecord;
import com.tokenplay.ue4.model.repositories.InventoryLocationsDB;

@Repository
@Transactional
public class JOOQInventoryLocationsDB implements InventoryLocationsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQInventoryLocationsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<InventoryLocationRecord> findAll() {
        return jooq.selectFrom(Tue4InventoryLocation.INVENTORY_LOCATION).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public InventoryLocationRecord findById(String id) {
        return jooq.selectFrom(Tue4InventoryLocation.INVENTORY_LOCATION).where(Tue4InventoryLocation.INVENTORY_LOCATION.INL_ID.eq(id)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<InventoryLocationRecord> findByIdList(String id) {
        Result<InventoryLocationRecord> locations =
            jooq.selectFrom(Tue4InventoryLocation.INVENTORY_LOCATION).where(Tue4InventoryLocation.INVENTORY_LOCATION.INL_ID.eq(id)).fetch();
        return locations;
    }

}
