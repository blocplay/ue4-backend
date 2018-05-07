package com.tokenplay.ue4.model.repositories.impl;

import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4BmOrder;
import com.tokenplay.ue4.model.db.tables.Tue4Pilot;
import com.tokenplay.ue4.model.db.tables.records.BmOrderRecord;
import com.tokenplay.ue4.model.repositories.OrderDB;

@Repository
@Transactional
public class JOOQOrderDB implements OrderDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQOrderDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<BmOrderRecord> findAll() {
        return jooq.selectFrom(Tue4BmOrder.BM_ORDER).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public BmOrderRecord findById(String id) {
        return jooq.selectFrom(Tue4BmOrder.BM_ORDER).where(Tue4BmOrder.BM_ORDER.BM_ORDER_ID.equalIgnoreCase(id)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<BmOrderRecord> findByPilId(String pilId) {
        return jooq.selectFrom(Tue4BmOrder.BM_ORDER).where(Tue4BmOrder.BM_ORDER.PIL_ID.equalIgnoreCase(pilId)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public List<BmOrderRecord> findByPilToken(String token) {
        List<BmOrderRecord> orderList = new ArrayList<>();

        Result<Record> results =
            jooq.selectFrom(Tue4Pilot.PILOT.leftOuterJoin(Tue4BmOrder.BM_ORDER).on(Tue4Pilot.PILOT.PIL_ID.eq(Tue4BmOrder.BM_ORDER.PIL_ID)))
                .where(Tue4Pilot.PILOT.PIL_TOKEN.equal(token)).fetch();

        for (Record record : results) {
            BmOrderRecord trans = record.into(BmOrderRecord.class);
            orderList.add(trans);
        }

        return orderList;

    }

    @Transactional(readOnly = true)
    @Override
    public Result<BmOrderRecord> findByCorpId(String corpId) {
        return jooq.selectFrom(Tue4BmOrder.BM_ORDER).where(Tue4BmOrder.BM_ORDER.CORP_ID.equalIgnoreCase(corpId)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public BmOrderRecord findByTransactionId(String transactionId) {
        return jooq.selectFrom(Tue4BmOrder.BM_ORDER).where(Tue4BmOrder.BM_ORDER.BM_TRANSACTION_ID.equalIgnoreCase(transactionId)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<BmOrderRecord> findByEquipmentId(String equipId) {
        return jooq.selectFrom(Tue4BmOrder.BM_ORDER).where(Tue4BmOrder.BM_ORDER.BM_EQUIPMENT_ID.equalIgnoreCase(equipId)).fetch();
    }
}
