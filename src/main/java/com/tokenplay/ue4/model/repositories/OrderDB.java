package com.tokenplay.ue4.model.repositories;

import java.util.List;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.BmOrderRecord;

public interface OrderDB {
    Result<BmOrderRecord> findAll();

    BmOrderRecord findById(String id);

    Result<BmOrderRecord> findByPilId(String pilId);

    List<BmOrderRecord> findByPilToken(String token);

    Result<BmOrderRecord> findByCorpId(String corpId);

    BmOrderRecord findByTransactionId(String transactionId);

    Result<BmOrderRecord> findByEquipmentId(String equipId);

}
