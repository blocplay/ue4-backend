package com.tokenplay.ue4.model.repositories;

import java.util.List;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.BmTransactionsRecord;

public interface TransactionsDB {
    Result<BmTransactionsRecord> findAll();

    BmTransactionsRecord findById(String id);

    Result<BmTransactionsRecord> findByTransferId(String transferId);

    Result<BmTransactionsRecord> findByAccountId(String accountId);

    Result<BmTransactionsRecord> findByPaymentStatus(String status);

    List<BmTransactionsRecord> findByPilToken(String token);

    List<BmTransactionsRecord> findByCorpId(String corpId);

    List<BmTransactionsRecord> findByPilId(String pilId);

}
