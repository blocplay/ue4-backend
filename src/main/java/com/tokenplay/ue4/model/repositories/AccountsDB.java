package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.BmAccountsRecord;

public interface AccountsDB {
    Result<BmAccountsRecord> findAll();

    BmAccountsRecord findById(String id);

    BmAccountsRecord findByPilId(String pilId);

    BmAccountsRecord findByPilToken(String token);

    BmAccountsRecord findByCorpId(String corpId);

    Result<BmAccountsRecord> findByStatus(String status);

    Result<BmAccountsRecord> findByType(String type);

}
