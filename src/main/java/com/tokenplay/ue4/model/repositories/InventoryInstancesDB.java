package com.tokenplay.ue4.model.repositories;

import java.util.List;

import org.apache.commons.lang3.tuple.Triple;
import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.pojos.InventoryInstance;
import com.tokenplay.ue4.model.db.tables.pojos.InventoryLocation;
import com.tokenplay.ue4.model.db.tables.pojos.InventoryObject;
import com.tokenplay.ue4.model.db.tables.records.InventoryInstanceRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;

public interface InventoryInstancesDB {
    List<Triple<InventoryInstance, InventoryObject, InventoryLocation>> findAll(PilotRecord pilot, String id);

    Triple<InventoryInstanceRecord, InventoryObject, InventoryLocation> findById(String id);

    void deleteAllEquipment(PilotRecord pilot, String id);

    Result<InventoryInstanceRecord> findByGeID(String id);
}
