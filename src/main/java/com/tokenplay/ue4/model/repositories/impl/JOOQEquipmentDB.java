package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4LoreEquipment;
import com.tokenplay.ue4.model.db.tables.records.LoreEquipmentRecord;
import com.tokenplay.ue4.model.repositories.EquipmentDB;

@Repository
@Transactional
public class JOOQEquipmentDB implements EquipmentDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQEquipmentDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreEquipmentRecord> findAll() {
        return jooq.selectFrom(Tue4LoreEquipment.LORE_EQUIPMENT).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public LoreEquipmentRecord findById(String id) {
        return jooq.selectFrom(Tue4LoreEquipment.LORE_EQUIPMENT).where(Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_ID.equalIgnoreCase(id)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public LoreEquipmentRecord findByName(String name) {
        return jooq.selectFrom(Tue4LoreEquipment.LORE_EQUIPMENT).where(Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_EQUIPMENT.equalIgnoreCase(name))
            .fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreEquipmentRecord> findByCode(String code) {
        return jooq.selectFrom(Tue4LoreEquipment.LORE_EQUIPMENT).where(Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_CODE.equalIgnoreCase(code)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreEquipmentRecord> findByType(String type) {
        return jooq.selectFrom(Tue4LoreEquipment.LORE_EQUIPMENT).where(Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_TYPE.equalIgnoreCase(type)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreEquipmentRecord> findByManufacturer(String manufacture) {
        return jooq.selectFrom(Tue4LoreEquipment.LORE_EQUIPMENT).where(Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_MANUFACTURE.equalIgnoreCase(manufacture))
            .fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreEquipmentRecord> findByManufacturerId(String manufacturerId) {
        return jooq.selectFrom(Tue4LoreEquipment.LORE_EQUIPMENT).where(Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_LCO_ID.equalIgnoreCase(manufacturerId))
            .fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreEquipmentRecord> findByFaction(String faction) {
        return jooq.selectFrom(Tue4LoreEquipment.LORE_EQUIPMENT).where(Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_FACTION.equalIgnoreCase(faction)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreEquipmentRecord> findByHemisphere(String hemisphere) {
        return jooq.selectFrom(Tue4LoreEquipment.LORE_EQUIPMENT).where(Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_FACTION.equalIgnoreCase(hemisphere))
            .fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreEquipmentRecord> findByGearId(String gearId) {
        return jooq.selectFrom(Tue4LoreEquipment.LORE_EQUIPMENT).where(Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_LGE_ID.equalIgnoreCase(gearId)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreEquipmentRecord> findByInventoryObjId(String inventoryObjId) {
        return jooq.selectFrom(Tue4LoreEquipment.LORE_EQUIPMENT).where(Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_INO_ID.equalIgnoreCase(inventoryObjId))
            .fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<LoreEquipmentRecord> findByStoreId(String storeId) {
        return jooq.selectFrom(Tue4LoreEquipment.LORE_EQUIPMENT).where(Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_STORE_ORDER_ID.equalIgnoreCase(storeId))
            .fetch();
    }
}
