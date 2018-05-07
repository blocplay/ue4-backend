package com.tokenplay.ue4.model.repositories.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4InventoryInstance;
import com.tokenplay.ue4.model.db.tables.Tue4InventoryLocation;
import com.tokenplay.ue4.model.db.tables.Tue4InventoryObject;
import com.tokenplay.ue4.model.db.tables.pojos.InventoryInstance;
import com.tokenplay.ue4.model.db.tables.pojos.InventoryLocation;
import com.tokenplay.ue4.model.db.tables.pojos.InventoryObject;
import com.tokenplay.ue4.model.db.tables.records.InventoryInstanceRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.model.repositories.InventoryInstancesDB;

@Repository
@Transactional
public class JOOQInventoryInstancesDB implements InventoryInstancesDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQInventoryInstancesDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Triple<InventoryInstance, InventoryObject, InventoryLocation>> findAll(PilotRecord pilot, String id) {
        List<Triple<InventoryInstance, InventoryObject, InventoryLocation>> inventoryList = new ArrayList<>();
        Result<Record> results =
            jooq.selectFrom(
                Tue4InventoryInstance.INVENTORY_INSTANCE.join(Tue4InventoryObject.INVENTORY_OBJECT)
                    .on(Tue4InventoryObject.INVENTORY_OBJECT.INO_ID.eq(Tue4InventoryInstance.INVENTORY_INSTANCE.INI_INO_ID))
                    .join(Tue4InventoryLocation.INVENTORY_LOCATION)
                    .on(Tue4InventoryLocation.INVENTORY_LOCATION.INL_ID.eq(Tue4InventoryInstance.INVENTORY_INSTANCE.INI_INL_ID)))
                .where(
                    Tue4InventoryInstance.INVENTORY_INSTANCE.INI_PIL_ID.eq(pilot.getPilId()).and(
                        Tue4InventoryInstance.INVENTORY_INSTANCE.INI_GEI_ID.eq(id))).fetch();
        for (Record record : results) {
            InventoryInstance inventoryInstance = record.into(InventoryInstance.class);
            InventoryObject object = record.into(InventoryObject.class);
            InventoryLocation location = record.into(InventoryLocation.class);
            inventoryList.add(new ImmutableTriple<>(inventoryInstance, object, location));
        }
        return inventoryList;
    }

    @Transactional(readOnly = true)
    @Override
    public Triple<InventoryInstanceRecord, InventoryObject, InventoryLocation> findById(String id) {
        Record record =
            jooq.selectFrom(
                Tue4InventoryInstance.INVENTORY_INSTANCE.join(Tue4InventoryObject.INVENTORY_OBJECT)
                    .on(Tue4InventoryObject.INVENTORY_OBJECT.INO_ID.eq(Tue4InventoryInstance.INVENTORY_INSTANCE.INI_INO_ID))
                    .join(Tue4InventoryLocation.INVENTORY_LOCATION)
                    .on(Tue4InventoryLocation.INVENTORY_LOCATION.INL_ID.eq(Tue4InventoryInstance.INVENTORY_INSTANCE.INI_INL_ID)))
                .where(Tue4InventoryInstance.INVENTORY_INSTANCE.INI_ID.eq(id)).fetchOne();
        if (record != null) {
            InventoryInstanceRecord inventoryInstance = record.into(InventoryInstanceRecord.class);
            InventoryObject object = record.into(InventoryObject.class);
            InventoryLocation location = record.into(InventoryLocation.class);
            return new ImmutableTriple<>(inventoryInstance, object, location);
        } else {
            return null;
        }
    }

    @Override
    public void deleteAllEquipment(PilotRecord pilot, String id) {
        jooq.delete(Tue4InventoryInstance.INVENTORY_INSTANCE)
            .where(
                Tue4InventoryInstance.INVENTORY_INSTANCE.INI_PIL_ID.eq(pilot.getPilId()).and(
                    Tue4InventoryInstance.INVENTORY_INSTANCE.INI_GEI_ID.eq(id))).execute();
    }


    @Transactional(readOnly = true)
    @Override
    public Result<InventoryInstanceRecord> findByGeID(String id) {
        Result<InventoryInstanceRecord> inventory = null;
        inventory =
            jooq.selectFrom(Tue4InventoryInstance.INVENTORY_INSTANCE).where(Tue4InventoryInstance.INVENTORY_INSTANCE.INI_GEI_ID.eq(id)).fetch();
        return inventory;
    }
}
