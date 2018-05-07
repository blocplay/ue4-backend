package com.tokenplay.ue4.model.repositories;

import java.util.List;

import org.jooq.Record;
import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.BmAssetsRecord;

public interface AssetsDB {
    Result<BmAssetsRecord> findAll();

    Record findById(String id);

    Result<Record> findByPilId(String pilId);

    List<BmAssetsRecord> findByPilToken(String token);

    Result<BmAssetsRecord> findByCorpId(String corpId);

    Result<BmAssetsRecord> findByCode(String code);

    Result<BmAssetsRecord> findByType(String type);

    Result<Record> findByPilIdAndType(String pilId, String type);

    Result<Record> findByPilIdAndCode(String pilId, String code);

}
