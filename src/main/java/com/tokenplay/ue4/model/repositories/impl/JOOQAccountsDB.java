package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4BmAccounts;
import com.tokenplay.ue4.model.db.tables.Tue4Pilot;
import com.tokenplay.ue4.model.db.tables.records.BmAccountsRecord;
import com.tokenplay.ue4.model.repositories.AccountsDB;

@Repository
@Transactional
public class JOOQAccountsDB implements AccountsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQAccountsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<BmAccountsRecord> findAll() {
        return jooq.selectFrom(Tue4BmAccounts.BM_ACCOUNTS).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public BmAccountsRecord findById(String id) {
        return jooq.selectFrom(Tue4BmAccounts.BM_ACCOUNTS).where(Tue4BmAccounts.BM_ACCOUNTS.BM_ACCOUNTS_ID.equalIgnoreCase(id)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public BmAccountsRecord findByPilId(String pilId) {
        return jooq.selectFrom(Tue4BmAccounts.BM_ACCOUNTS).where(Tue4BmAccounts.BM_ACCOUNTS.PIL_ID.equalIgnoreCase(pilId)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public BmAccountsRecord findByPilToken(String token) {
        BmAccountsRecord account = null;

        Record record =
            jooq.selectFrom(
                Tue4Pilot.PILOT.leftOuterJoin(Tue4BmAccounts.BM_ACCOUNTS).on(Tue4Pilot.PILOT.PIL_ID.eq(Tue4BmAccounts.BM_ACCOUNTS.PIL_ID)))
                .where(Tue4Pilot.PILOT.PIL_TOKEN.equal(token)).fetchOne();

        if (record != null && record.size() > 0) {
            account = record.into(BmAccountsRecord.class);
        }

        return account;
    }

    @Transactional(readOnly = true)
    @Override
    public BmAccountsRecord findByCorpId(String corpId) {
        return jooq.selectFrom(Tue4BmAccounts.BM_ACCOUNTS).where(Tue4BmAccounts.BM_ACCOUNTS.BM_CORP_ID.equalIgnoreCase(corpId)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<BmAccountsRecord> findByStatus(String status) {
        return jooq.selectFrom(Tue4BmAccounts.BM_ACCOUNTS).where(Tue4BmAccounts.BM_ACCOUNTS.BM_ACCSTATUS.equalIgnoreCase(status)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<BmAccountsRecord> findByType(String type) {
        return jooq.selectFrom(Tue4BmAccounts.BM_ACCOUNTS).where(Tue4BmAccounts.BM_ACCOUNTS.BM_ACCOUNTTYPE.equalIgnoreCase(type)).fetch();
    }
}
