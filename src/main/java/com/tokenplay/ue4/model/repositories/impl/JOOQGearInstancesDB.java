package com.tokenplay.ue4.model.repositories.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.GearSpecification;
import com.tokenplay.ue4.model.db.tables.Tue4GearInstance;
import com.tokenplay.ue4.model.db.tables.Tue4GearModel;
import com.tokenplay.ue4.model.db.tables.Tue4GearSection;
import com.tokenplay.ue4.model.db.tables.Tue4InventoryInstance;
import com.tokenplay.ue4.model.db.tables.Tue4InventoryLocation;
import com.tokenplay.ue4.model.db.tables.Tue4InventoryObject;
import com.tokenplay.ue4.model.db.tables.Tue4Pilot;
import com.tokenplay.ue4.model.db.tables.pojos.GearInstance;
import com.tokenplay.ue4.model.db.tables.pojos.GearModel;
import com.tokenplay.ue4.model.db.tables.records.GearInstanceRecord;
import com.tokenplay.ue4.model.db.tables.records.GearModelRecord;
import com.tokenplay.ue4.model.db.tables.records.GearSectionRecord;
import com.tokenplay.ue4.model.db.tables.records.InventoryInstanceRecord;
import com.tokenplay.ue4.model.db.tables.records.InventoryLocationRecord;
import com.tokenplay.ue4.model.db.tables.records.InventoryObjectRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.model.repositories.GearInstancesDB;

@Repository
@Transactional
public class JOOQGearInstancesDB implements GearInstancesDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQGearInstancesDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public List<GearSpecification> findAll(PilotRecord pilot) {
        List<GearSpecification> gears = null;
        Result<Record> records =
            jooq.selectFrom(
                Tue4GearInstance.GEAR_INSTANCE.join(Tue4GearModel.GEAR_MODEL).on(
                    Tue4GearModel.GEAR_MODEL.GEM_ID.eq(Tue4GearInstance.GEAR_INSTANCE.GEI_GEM_ID)))
                .where(Tue4GearInstance.GEAR_INSTANCE.GEI_PIL_ID.eq(pilot.getPilId())).fetch();
        if (records != null && !records.isEmpty()) {
            gears = new ArrayList<>(records.size());
            for (Record record : records) {
                GearInstanceRecord instance = record.into(GearInstanceRecord.class);
                GearModelRecord model = record.into(GearModelRecord.class);
                gears.add(new GearSpecification(instance.into(GearInstance.class), model.into(GearModel.class)));
            }
        }
        return gears;
    }

    @Transactional(readOnly = true)
    @Override
    public GearSpecification findSpecificationById(String id) {
        GearSpecification result = null;
        Record record =
            jooq.selectFrom(
                Tue4GearInstance.GEAR_INSTANCE.join(Tue4GearModel.GEAR_MODEL).on(
                    Tue4GearModel.GEAR_MODEL.GEM_ID.eq(Tue4GearInstance.GEAR_INSTANCE.GEI_GEM_ID)))
                .where(Tue4GearInstance.GEAR_INSTANCE.GEI_ID.eq(id)).fetchOne();
        if (record != null) {
            GearInstanceRecord instance = record.into(GearInstanceRecord.class);
            GearModelRecord model = record.into(GearModelRecord.class);
            result = new GearSpecification(instance.into(GearInstance.class), model.into(GearModel.class));
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public Pair<GearInstanceRecord, GearModelRecord> findById(String id) {
        Pair<GearInstanceRecord, GearModelRecord> result = null;
        Record record =
            jooq.selectFrom(
                Tue4GearInstance.GEAR_INSTANCE.join(Tue4GearModel.GEAR_MODEL).on(
                    Tue4GearModel.GEAR_MODEL.GEM_ID.eq(Tue4GearInstance.GEAR_INSTANCE.GEI_GEM_ID)))
                .where(Tue4GearInstance.GEAR_INSTANCE.GEI_ID.eq(id)).fetchOne();
        if (record != null) {
            result =
                new ImmutablePair<GearInstanceRecord, GearModelRecord>(record.into(GearInstanceRecord.class), record.into(GearModelRecord.class));
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public GearInstanceRecord findByIdAndPilotToken(String id, String token) {
        GearInstanceRecord gearInstanceRecord = null;
        Record record =
            jooq.selectFrom(
                Tue4GearInstance.GEAR_INSTANCE.join(Tue4Pilot.PILOT).on(Tue4GearInstance.GEAR_INSTANCE.GEI_PIL_ID.eq(Tue4Pilot.PILOT.PIL_ID)))
                .where(Tue4GearInstance.GEAR_INSTANCE.GEI_ID.eq(id).and(Tue4Pilot.PILOT.PIL_TOKEN.eq(token))).fetchOne();
        if (record != null) {
            gearInstanceRecord = record.into(GearInstanceRecord.class);
        }
        return gearInstanceRecord;
    }

    /*
     * @Transactional(readOnly = true)
     * 
     * @Override
     * public Result<GearInstanceRecord> findByPilotId(String id)
     * {
     * Result<GearInstanceRecord> record = jooq.selectFrom(Tue4GearInstance.GEAR_INSTANCE.join(Tue4Pilot.PILOT)
     * .on(Tue4GearInstance.GEAR_INSTANCE.GEI_PIL_ID.eq(Tue4Pilot.PILOT.PIL_ID)))
     * .where(Tue4Pilot.PILOT.PIL_ID.eq(id))
     * .fetch();
     * 
     * return record;
     * }
     */

    @Transactional(readOnly = true)
    @Override
    public List<Pair<GearInstanceRecord, GearModelRecord>> findByPilotId(String id) {
        Result<Record> gears =
            jooq.selectFrom(
                Tue4GearInstance.GEAR_INSTANCE.join(Tue4GearModel.GEAR_MODEL).on(
                    Tue4GearModel.GEAR_MODEL.GEM_ID.eq(Tue4GearInstance.GEAR_INSTANCE.GEI_GEM_ID)))
                .where(Tue4GearInstance.GEAR_INSTANCE.GEI_PIL_ID.eq(id)).orderBy(Tue4GearInstance.GEAR_INSTANCE.GEI_NAME).fetch();
        List<Pair<GearInstanceRecord, GearModelRecord>> instancesByPilot;
        if (gears != null) {
            instancesByPilot =
                gears
                    .stream()
                    .map(
                        record -> {
                            return new ImmutablePair<GearInstanceRecord, GearModelRecord>(record.into(GearInstanceRecord.class), record
                                .into(GearModelRecord.class));
                        }).collect(Collectors.toList());
        } else {
            instancesByPilot = Collections.emptyList();
        }
        return instancesByPilot;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Pair<GearInstanceRecord, GearModelRecord>> findByPilotToken(String token) {
        Result<Record> gears =
            jooq.selectFrom(
                Tue4GearInstance.GEAR_INSTANCE.join(Tue4GearModel.GEAR_MODEL)
                    .on(Tue4GearModel.GEAR_MODEL.GEM_ID.eq(Tue4GearInstance.GEAR_INSTANCE.GEI_GEM_ID)).join(Tue4Pilot.PILOT)
                    .on(Tue4Pilot.PILOT.PIL_ID.eq(Tue4GearInstance.GEAR_INSTANCE.GEI_PIL_ID))).where(Tue4Pilot.PILOT.PIL_TOKEN.eq(token))
                .orderBy(Tue4GearInstance.GEAR_INSTANCE.GEI_NAME).fetch();
        List<Pair<GearInstanceRecord, GearModelRecord>> instancesByPilot;
        if (gears != null) {
            instancesByPilot =
                gears
                    .stream()
                    .map(
                        record -> {
                            return new ImmutablePair<GearInstanceRecord, GearModelRecord>(record.into(GearInstanceRecord.class), record
                                .into(GearModelRecord.class));
                        }).collect(Collectors.toList());
        } else {
            instancesByPilot = Collections.emptyList();
        }
        return instancesByPilot;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Result<Record>> findAllInstances(String token) {
        List<Result<Record>> record =
            jooq.selectFrom(
                Tue4GearInstance.GEAR_INSTANCE.join(Tue4InventoryInstance.INVENTORY_INSTANCE)
                    .on(Tue4InventoryInstance.INVENTORY_INSTANCE.INI_GEI_ID.eq(Tue4GearInstance.GEAR_INSTANCE.GEI_ID))
                    .join(Tue4InventoryObject.INVENTORY_OBJECT)
                    .on(Tue4InventoryInstance.INVENTORY_INSTANCE.INI_INO_ID.eq(Tue4InventoryObject.INVENTORY_OBJECT.INO_ID))
                    .join(Tue4InventoryLocation.INVENTORY_LOCATION)
                    .on(Tue4InventoryInstance.INVENTORY_INSTANCE.INI_INL_ID.eq(Tue4InventoryLocation.INVENTORY_LOCATION.INL_ID))
                    .join(Tue4Pilot.PILOT).on(Tue4GearInstance.GEAR_INSTANCE.GEI_PIL_ID.eq(Tue4Pilot.PILOT.PIL_ID)))
                .where(Tue4Pilot.PILOT.PIL_TOKEN.eq(token)).orderBy(Tue4GearInstance.GEAR_INSTANCE.GEI_NAME).fetchMany();
        return record;

    }

    @Override
    public Map<String, Object> jsonModelComplete(PilotRecord pilot, GearInstanceRecord instance, GearModelRecord model,
        List<Triple<InventoryInstanceRecord, InventoryObjectRecord, InventoryLocationRecord>> inventoryList) {
        Map<String, String> sectionNames = new HashMap<>();
        for (GearSectionRecord gearSection : jooq.selectFrom(Tue4GearSection.GEAR_SECTION)) {
            sectionNames.put(gearSection.getGesId(), gearSection.getGesName());
        }
        //
        Map<String, Object> theMap = new HashMap<>();
        theMap.put("Id", instance.getGeiId());
        theMap.put("Name", instance.getGeiName());
        theMap.put("Engine",
            instance.getGeiGesEngine() != null ? sectionNames.get(instance.getGeiGesEngine()) : sectionNames.get(model.getGemGesEngine()));
        //
        theMap.put("EngineFuelTank",
            instance.getGeiGesFuelTank() != null ? sectionNames.get(instance.getGeiGesFuelTank()) : sectionNames.get(model.getGemGesFuelTank()));
        theMap
            .put(
                "EnginePylonRight",
                instance.getGeiGesPylonRight() != null ? sectionNames.get(instance.getGeiGesPylonRight()) : sectionNames.get(model
                    .getGemGesPylonRight()));
        theMap.put("EnginePylonLeft",
            instance.getGeiGesPylonLeft() != null ? sectionNames.get(instance.getGeiGesPylonLeft()) : sectionNames.get(model.getGemGesPylonLeft()));
        theMap.put("Head", instance.getGeiGesHead() != null ? sectionNames.get(instance.getGeiGesHead()) : sectionNames.get(model.getGemGesHead()));
        theMap.put("Hip", instance.getGeiGesHip() != null ? sectionNames.get(instance.getGeiGesHip()) : sectionNames.get(model.getGemGesHip()));
        theMap.put("Torso",
            instance.getGeiGesTorso() != null ? sectionNames.get(instance.getGeiGesTorso()) : sectionNames.get(model.getGemGesTorso()));
        theMap.put("FootRight",
            instance.getGeiGesFootRight() != null ? sectionNames.get(instance.getGeiGesFootRight()) : sectionNames.get(model.getGemGesFootRight()));
        theMap.put("FootLeft",
            instance.getGeiGesFootLeft() != null ? sectionNames.get(instance.getGeiGesFootLeft()) : sectionNames.get(model.getGemGesFootLeft()));
        theMap.put(
            "UpperLegRight",
            instance.getGeiGesUpperLegRight() != null ? sectionNames.get(instance.getGeiGesUpperLegRight()) : sectionNames.get(model
                .getGemGesUpperLegRight()));
        theMap.put(
            "UpperLegLeft",
            instance.getGeiGesUpperLegLeft() != null ? sectionNames.get(instance.getGeiGesUpperLegLeft()) : sectionNames.get(model
                .getGemGesUpperLegLeft()));
        theMap.put(
            "LowerLegRight",
            instance.getGeiGesLowerLegRight() != null ? sectionNames.get(instance.getGeiGesLowerLegRight()) : sectionNames.get(model
                .getGemGesLowerLegRight()));
        theMap.put(
            "LowerLegLeft",
            instance.getGeiGesLowerLegLeft() != null ? sectionNames.get(instance.getGeiGesLowerLegLeft()) : sectionNames.get(model
                .getGemGesLowerLegLeft()));
        theMap.put("HandRight",
            instance.getGeiGesHandRight() != null ? sectionNames.get(instance.getGeiGesHandRight()) : sectionNames.get(model.getGemGesHandRight()));
        theMap.put("HandLeft",
            instance.getGeiGesHandLeft() != null ? sectionNames.get(instance.getGeiGesHandLeft()) : sectionNames.get(model.getGemGesHandLeft()));
        theMap.put(
            "LowerArmRight",
            instance.getGeiGesLowerArmRight() != null ? sectionNames.get(instance.getGeiGesLowerArmRight()) : sectionNames.get(model
                .getGemGesLowerArmRight()));
        theMap.put(
            "LowerArmLeft",
            instance.getGeiGesLowerArmLeft() != null ? sectionNames.get(instance.getGeiGesLowerArmLeft()) : sectionNames.get(model
                .getGemGesLowerArmLeft()));
        theMap.put(
            "UpperArmRight",
            instance.getGeiGesUpperArmRight() != null ? sectionNames.get(instance.getGeiGesUpperArmRight()) : sectionNames.get(model
                .getGemGesUpperArmRight()));
        theMap.put(
            "UpperArmLeft",
            instance.getGeiGesUpperArmLeft() != null ? sectionNames.get(instance.getGeiGesUpperArmLeft()) : sectionNames.get(model
                .getGemGesUpperArmLeft()));
        theMap.put(
            "ShoulderRight",
            instance.getGeiGesShoulderRight() != null ? sectionNames.get(instance.getGeiGesShoulderRight()) : sectionNames.get(model
                .getGemGesShoulderRight()));
        theMap.put(
            "ShoulderLeft",
            instance.getGeiGesShoulderLeft() != null ? sectionNames.get(instance.getGeiGesShoulderLeft()) : sectionNames.get(model
                .getGemGesShoulderLeft()));
        //
        if (inventoryList != null && !inventoryList.isEmpty()) {
            Object[] inventoryObjectList = new Object[inventoryList.size()];
            int i = 0;
            for (Triple<InventoryInstanceRecord, InventoryObjectRecord, InventoryLocationRecord> triple : inventoryList) {
                inventoryObjectList[i++] = toJsonMap(triple.getLeft(), triple.getMiddle(), triple.getRight());
            }
            theMap.put("Inventory", inventoryObjectList);
        }
        //
        if (instance.getGeiUseCustomScheme() && instance.getGeiDefaultScheme() != null) {
            theMap.put("GearColorScheme", instance.getGeiDefaultScheme());
        } else if (pilot != null && pilot.getPilUseCustomScheme() && pilot.getPilDefaultScheme() != null) {
            theMap.put("GearColorScheme", pilot.getPilDefaultScheme());
        }
        return theMap;
    }

    public Map<String, Object> toJsonMap(InventoryObjectRecord model) {
        Map<String, Object> theMap = new HashMap<>();
        theMap.put("Id", model.getInoId());
        theMap.put("Name", model.getInoName());
        return theMap;
    }

    public Map<String, Object> toJsonMap(InventoryInstanceRecord ini, InventoryObjectRecord model, InventoryLocationRecord location) {
        Map<String, Object> theMap = new HashMap<>();
        theMap.put("Id", ini.getIniId());
        Map<String, Object> modelMap = toJsonMap(model);
        modelMap.remove("Id");
        theMap.putAll(modelMap);
        if (location != null) {
            theMap.put("Location", location.getInlName());
        }
        return theMap;
    }

    @Override
    public Triple<GearInstanceRecord, GearModelRecord, List<Triple<InventoryInstanceRecord, InventoryObjectRecord, InventoryLocationRecord>>> fullGearInstance(
        String gearID) {
        Triple<GearInstanceRecord, GearModelRecord, List<Triple<InventoryInstanceRecord, InventoryObjectRecord, InventoryLocationRecord>>> result;
        Result<Record> records =
            jooq.select()
                .from(Tue4GearInstance.GEAR_INSTANCE)
                .join(Tue4GearModel.GEAR_MODEL)
                .on(Tue4GearInstance.GEAR_INSTANCE.GEI_GEM_ID.eq(Tue4GearModel.GEAR_MODEL.GEM_ID))
                .leftOuterJoin(
                    Tue4InventoryInstance.INVENTORY_INSTANCE.join(Tue4InventoryObject.INVENTORY_OBJECT)
                        .on(Tue4InventoryInstance.INVENTORY_INSTANCE.INI_INO_ID.eq(Tue4InventoryObject.INVENTORY_OBJECT.INO_ID))
                        .join(Tue4InventoryLocation.INVENTORY_LOCATION)
                        .on(Tue4InventoryInstance.INVENTORY_INSTANCE.INI_INL_ID.eq(Tue4InventoryLocation.INVENTORY_LOCATION.INL_ID)))
                .on(Tue4InventoryInstance.INVENTORY_INSTANCE.INI_GEI_ID.eq(Tue4GearInstance.GEAR_INSTANCE.GEI_ID))
                .where(Tue4GearInstance.GEAR_INSTANCE.GEI_ID.eq(gearID)).fetch();
        if (records != null && records.size() > 0) {
            GearInstanceRecord instance = null;
            GearModelRecord model = null;
            List<Triple<InventoryInstanceRecord, InventoryObjectRecord, InventoryLocationRecord>> inventory = new ArrayList<>(records.size());
            for (Record record : records) {
                if (instance == null) {
                    instance = record.into(GearInstanceRecord.class);
                    model = record.into(GearModelRecord.class);
                }
                if (record.getValue(Tue4InventoryInstance.INVENTORY_INSTANCE.INI_ID) != null) {
                    InventoryInstanceRecord inventoryInstance = record.into(InventoryInstanceRecord.class);
                    InventoryObjectRecord object = record.into(InventoryObjectRecord.class);
                    InventoryLocationRecord location = record.into(InventoryLocationRecord.class);
                    inventory.add(new ImmutableTriple<>(inventoryInstance, object, location));
                }
            }
            result = new ImmutableTriple<>(instance, model, inventory);
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public List<Triple<GearInstanceRecord, GearModelRecord, List<Triple<InventoryInstanceRecord, InventoryObjectRecord, InventoryLocationRecord>>>> fullPilotGearInstances(
        String pilId) {
        List<Triple<GearInstanceRecord, GearModelRecord, List<Triple<InventoryInstanceRecord, InventoryObjectRecord, InventoryLocationRecord>>>> result;
        Result<Record> records =
            jooq.select()
                .from(Tue4GearInstance.GEAR_INSTANCE)
                .join(Tue4GearModel.GEAR_MODEL)
                .on(Tue4GearInstance.GEAR_INSTANCE.GEI_GEM_ID.eq(Tue4GearModel.GEAR_MODEL.GEM_ID))
                .leftOuterJoin(
                    Tue4InventoryInstance.INVENTORY_INSTANCE.join(Tue4InventoryObject.INVENTORY_OBJECT)
                        .on(Tue4InventoryInstance.INVENTORY_INSTANCE.INI_INO_ID.eq(Tue4InventoryObject.INVENTORY_OBJECT.INO_ID))
                        .join(Tue4InventoryLocation.INVENTORY_LOCATION)
                        .on(Tue4InventoryInstance.INVENTORY_INSTANCE.INI_INL_ID.eq(Tue4InventoryLocation.INVENTORY_LOCATION.INL_ID)))
                .on(Tue4InventoryInstance.INVENTORY_INSTANCE.INI_GEI_ID.eq(Tue4GearInstance.GEAR_INSTANCE.GEI_ID))
                .where(Tue4GearInstance.GEAR_INSTANCE.GEI_PIL_ID.eq(pilId)).orderBy(Tue4GearInstance.GEAR_INSTANCE.GEI_ID).fetch();
        if (records != null && records.size() > 0) {
            result = new ArrayList<>();
            String previousGeiId = null;
            String currentGeiId = null;
            GearInstanceRecord instance = null;
            GearModelRecord model = null;
            List<Triple<InventoryInstanceRecord, InventoryObjectRecord, InventoryLocationRecord>> inventory = null;
            for (Record record : records) {
                currentGeiId = record.getValue(Tue4GearInstance.GEAR_INSTANCE.GEI_ID);
                if (!currentGeiId.equals(previousGeiId)) {
                    instance = record.into(GearInstanceRecord.class);
                    model = record.into(GearModelRecord.class);
                    inventory = new ArrayList<>();
                    result.add(new ImmutableTriple<>(instance, model, inventory));
                }
                if (record.getValue(Tue4InventoryInstance.INVENTORY_INSTANCE.INI_ID) != null) {
                    InventoryInstanceRecord inventoryInstance = record.into(InventoryInstanceRecord.class);
                    InventoryObjectRecord object = record.into(InventoryObjectRecord.class);
                    InventoryLocationRecord location = record.into(InventoryLocationRecord.class);
                    inventory.add(new ImmutableTriple<>(inventoryInstance, object, location));
                }
                previousGeiId = currentGeiId;
            }
        } else {
            result = null;
        }
        return result;
    }
}
