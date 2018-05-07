package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.LoreEquipmentRecord;

public interface EquipmentDB {
    Result<LoreEquipmentRecord> findAll();

    LoreEquipmentRecord findById(String id);

    LoreEquipmentRecord findByName(String name);

    Result<LoreEquipmentRecord> findByCode(String code);

    Result<LoreEquipmentRecord> findByType(String type);

    Result<LoreEquipmentRecord> findByManufacturer(String manufacture);

    Result<LoreEquipmentRecord> findByManufacturerId(String manufacturerId);

    Result<LoreEquipmentRecord> findByFaction(String faction);

    Result<LoreEquipmentRecord> findByHemisphere(String hemisphere);

    Result<LoreEquipmentRecord> findByGearId(String gearId);

    Result<LoreEquipmentRecord> findByInventoryObjId(String inventoryObjId);

    Result<LoreEquipmentRecord> findByStoreId(String storeId);

    /*
     * Result<LoreEquipmentRecord> findByThreat(int numItems, boolean orderItemsAsc);
     * 
     * Result<LoreEquipmentRecord> findByTec(int numItems, boolean orderItemsAsc);
     * 
     * Result<LoreEquipmentRecord> findByCost(int numItems, boolean orderItemsAsc);
     */

}
