package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4InventoryObject;
import com.tokenplay.ue4.model.db.tables.records.InventoryObjectRecord;
import com.tokenplay.ue4.model.repositories.InventoryObjectsDB;

@Repository
@Transactional
public class JOOQInventoryObjectsDB implements InventoryObjectsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQInventoryObjectsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<InventoryObjectRecord> findAll() {
        return jooq.selectFrom(Tue4InventoryObject.INVENTORY_OBJECT).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public InventoryObjectRecord findById(String id) {
        return jooq.selectFrom(Tue4InventoryObject.INVENTORY_OBJECT).where(Tue4InventoryObject.INVENTORY_OBJECT.INO_ID.eq(id)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<InventoryObjectRecord> findByIdList(String id) {
        Result<InventoryObjectRecord> objects =
            jooq.selectFrom(Tue4InventoryObject.INVENTORY_OBJECT).where(Tue4InventoryObject.INVENTORY_OBJECT.INO_ID.eq(id)).fetch();
        return objects;
    }

}
