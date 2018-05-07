package com.tokenplay.ue4.model.repositories.impl;

import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4BmAccounts;
import com.tokenplay.ue4.model.db.tables.Tue4BmTransactions;
import com.tokenplay.ue4.model.db.tables.Tue4LoreCorporations;
import com.tokenplay.ue4.model.db.tables.Tue4Pilot;
import com.tokenplay.ue4.model.db.tables.records.BmTransactionsRecord;
import com.tokenplay.ue4.model.repositories.TransactionsDB;

@Repository
@Transactional
public class JOOQTransactionsDB implements TransactionsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQTransactionsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<BmTransactionsRecord> findAll() {
        return jooq.selectFrom(Tue4BmTransactions.BM_TRANSACTIONS).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public BmTransactionsRecord findById(String id) {
        return jooq.selectFrom(Tue4BmTransactions.BM_TRANSACTIONS).where(Tue4BmTransactions.BM_TRANSACTIONS.BM_TRANSACTIONS_ID.equalIgnoreCase(id))
            .fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<BmTransactionsRecord> findByTransferId(String transferId) {
        return jooq.selectFrom(Tue4BmTransactions.BM_TRANSACTIONS)
            .where(Tue4BmTransactions.BM_TRANSACTIONS.BM_TRANSFER_ID.equalIgnoreCase(transferId)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<BmTransactionsRecord> findByAccountId(String accountId) {
        return jooq.selectFrom(Tue4BmTransactions.BM_TRANSACTIONS)
            .where(Tue4BmTransactions.BM_TRANSACTIONS.BM_ACCOUNTS_ID.equalIgnoreCase(accountId)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<BmTransactionsRecord> findByPaymentStatus(String status) {
        return jooq.selectFrom(Tue4BmTransactions.BM_TRANSACTIONS).where(Tue4BmTransactions.BM_TRANSACTIONS.BM_PAYMENTSTATUS.equalIgnoreCase(status))
            .fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public List<BmTransactionsRecord> findByPilToken(String token) {
        List<BmTransactionsRecord> transList = new ArrayList<>();

        Result<Record> results =
            jooq.select().from(Tue4BmTransactions.BM_TRANSACTIONS).join(Tue4BmAccounts.BM_ACCOUNTS)
                .on(Tue4BmTransactions.BM_TRANSACTIONS.BM_ACCOUNTS_ID.equal(Tue4BmAccounts.BM_ACCOUNTS.BM_ACCOUNTS_ID)).join(Tue4Pilot.PILOT)
                .on(Tue4BmAccounts.BM_ACCOUNTS.PIL_ID.equal(Tue4Pilot.PILOT.PIL_ID)).where(Tue4Pilot.PILOT.PIL_TOKEN.equal(token)).fetch();

        for (Record record : results) {
            BmTransactionsRecord trans = record.into(BmTransactionsRecord.class);
            transList.add(trans);
        }

        return transList;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BmTransactionsRecord> findByCorpId(String corpId) {
        List<BmTransactionsRecord> transList = new ArrayList<>();

        Result<Record> results =
            jooq.select().from(Tue4BmTransactions.BM_TRANSACTIONS).join(Tue4BmAccounts.BM_ACCOUNTS)
                .on(Tue4BmTransactions.BM_TRANSACTIONS.BM_ACCOUNTS_ID.equal(Tue4BmAccounts.BM_ACCOUNTS.BM_ACCOUNTS_ID))
                .join(Tue4LoreCorporations.LORE_CORPORATIONS)
                .on(Tue4BmAccounts.BM_ACCOUNTS.BM_CORP_ID.equal(Tue4LoreCorporations.LORE_CORPORATIONS.LCO_ID))
                .where(Tue4LoreCorporations.LORE_CORPORATIONS.LCO_ID.equal(corpId)).fetch();

        for (Record record : results) {
            BmTransactionsRecord trans = record.into(BmTransactionsRecord.class);
            transList.add(trans);
        }

        return transList;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BmTransactionsRecord> findByPilId(String pilId) {
        List<BmTransactionsRecord> transList = new ArrayList<>();

        Result<Record> results =
            jooq.select().from(Tue4BmTransactions.BM_TRANSACTIONS).join(Tue4BmAccounts.BM_ACCOUNTS)
                .on(Tue4BmTransactions.BM_TRANSACTIONS.BM_ACCOUNTS_ID.equal(Tue4BmAccounts.BM_ACCOUNTS.BM_ACCOUNTS_ID)).join(Tue4Pilot.PILOT)
                .on(Tue4BmAccounts.BM_ACCOUNTS.PIL_ID.equal(Tue4Pilot.PILOT.PIL_ID)).where(Tue4Pilot.PILOT.PIL_ID.equal(pilId)).fetch();

        for (Record record : results) {
            BmTransactionsRecord trans = record.into(BmTransactionsRecord.class);
            transList.add(trans);
        }

        return transList;
    }
}
