package com.tokenplay.ue4.model.repositories;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jooq.Record;
import org.jooq.Result;

import com.tokenplay.ue4.model.GearSpecification;
import com.tokenplay.ue4.model.db.tables.records.GearInstanceRecord;
import com.tokenplay.ue4.model.db.tables.records.GearModelRecord;
import com.tokenplay.ue4.model.db.tables.records.InventoryInstanceRecord;
import com.tokenplay.ue4.model.db.tables.records.InventoryLocationRecord;
import com.tokenplay.ue4.model.db.tables.records.InventoryObjectRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;

public interface GearInstancesDB {
    List<GearSpecification> findAll(PilotRecord pilot);

    Pair<GearInstanceRecord, GearModelRecord> findById(String id);

    Map<String, Object> jsonModelComplete(PilotRecord pilot, GearInstanceRecord instance, GearModelRecord model,
        List<Triple<InventoryInstanceRecord, InventoryObjectRecord, InventoryLocationRecord>> inventoryList);

    Triple<GearInstanceRecord, GearModelRecord, List<Triple<InventoryInstanceRecord, InventoryObjectRecord, InventoryLocationRecord>>> fullGearInstance(
        String gearID);

    List<Triple<GearInstanceRecord, GearModelRecord, List<Triple<InventoryInstanceRecord, InventoryObjectRecord, InventoryLocationRecord>>>> fullPilotGearInstances(
        String pilId);

    GearSpecification findSpecificationById(String id);

    GearInstanceRecord findByIdAndPilotToken(String id, String token);

    List<Result<Record>> findAllInstances(String token);

    List<Pair<GearInstanceRecord, GearModelRecord>> findByPilotId(String id);

    List<Pair<GearInstanceRecord, GearModelRecord>> findByPilotToken(String token);
}
