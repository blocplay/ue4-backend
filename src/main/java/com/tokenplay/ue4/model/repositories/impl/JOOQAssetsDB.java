package com.tokenplay.ue4.model.repositories.impl;

import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4BmAssets;
import com.tokenplay.ue4.model.db.tables.Tue4LoreEquipment;
import com.tokenplay.ue4.model.db.tables.Tue4Pilot;
import com.tokenplay.ue4.model.db.tables.records.BmAssetsRecord;
import com.tokenplay.ue4.model.repositories.AssetsDB;

@Repository
@Transactional
public class JOOQAssetsDB implements AssetsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQAssetsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<BmAssetsRecord> findAll() {
        return jooq.selectFrom(Tue4BmAssets.BM_ASSETS).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Record findById(String id) {
        return jooq
            .selectFrom(
                Tue4BmAssets.BM_ASSETS.join(Tue4LoreEquipment.LORE_EQUIPMENT).on(
                    Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_ID.eq(Tue4BmAssets.BM_ASSETS.BM_DEFAULT_ID)))
            .where(Tue4BmAssets.BM_ASSETS.BM_ASSET_ID.equalIgnoreCase(id)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<Record> findByPilId(String pilId) {
        return jooq
            .selectFrom(
                Tue4BmAssets.BM_ASSETS.join(Tue4LoreEquipment.LORE_EQUIPMENT).on(
                    Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_ID.eq(Tue4BmAssets.BM_ASSETS.BM_DEFAULT_ID)))
            .where(Tue4BmAssets.BM_ASSETS.BM_PIL_ID.equalIgnoreCase(pilId)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public List<BmAssetsRecord> findByPilToken(String token) {
        List<BmAssetsRecord> assetList = new ArrayList<>();

        Result<Record> results =
            jooq.selectFrom(Tue4Pilot.PILOT.leftOuterJoin(Tue4BmAssets.BM_ASSETS).on(Tue4Pilot.PILOT.PIL_ID.eq(Tue4BmAssets.BM_ASSETS.BM_PIL_ID)))
                .where(Tue4Pilot.PILOT.PIL_TOKEN.equal(token)).fetch();


        for (Record record : results) {
            BmAssetsRecord assets = record.into(BmAssetsRecord.class);
            assetList.add(assets);
        }
        return assetList;


    }

    @Transactional(readOnly = true)
    @Override
    public Result<BmAssetsRecord> findByCorpId(String corpId) {
        return jooq.select(Tue4BmAssets.BM_ASSETS.fields()).from(Tue4BmAssets.BM_ASSETS).join(Tue4LoreEquipment.LORE_EQUIPMENT)
            .on(Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_ID.eq(Tue4BmAssets.BM_ASSETS.BM_DEFAULT_ID))
            .where(Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_LCO_ID.equalIgnoreCase(corpId)).fetch().into(Tue4BmAssets.BM_ASSETS);
    }

    @Transactional(readOnly = true)
    @Override
    public Result<BmAssetsRecord> findByCode(String code) {
        return jooq.select(Tue4BmAssets.BM_ASSETS.fields()).from(Tue4BmAssets.BM_ASSETS).join(Tue4LoreEquipment.LORE_EQUIPMENT)
            .on(Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_ID.eq(Tue4BmAssets.BM_ASSETS.BM_DEFAULT_ID))
            .where(Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_CODE.equalIgnoreCase(code)).fetch().into(Tue4BmAssets.BM_ASSETS);
    }

    @Transactional(readOnly = true)
    @Override
    public Result<BmAssetsRecord> findByType(String type) {
        return jooq.select(Tue4BmAssets.BM_ASSETS.fields()).from(Tue4BmAssets.BM_ASSETS).join(Tue4LoreEquipment.LORE_EQUIPMENT)
            .on(Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_ID.eq(Tue4BmAssets.BM_ASSETS.BM_DEFAULT_ID))
            .where(Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_TYPE.equalIgnoreCase(type)).fetch().into(Tue4BmAssets.BM_ASSETS);
    }

    @Transactional(readOnly = true)
    @Override
    public Result<Record> findByPilIdAndType(String pilId, String type) {
        return jooq
            .selectFrom(
                Tue4BmAssets.BM_ASSETS.join(Tue4LoreEquipment.LORE_EQUIPMENT).on(
                    Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_ID.eq(Tue4BmAssets.BM_ASSETS.BM_DEFAULT_ID)))
            .where(Tue4BmAssets.BM_ASSETS.BM_PIL_ID.equalIgnoreCase(pilId).and(Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_TYPE.equalIgnoreCase(type)))
            .fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<Record> findByPilIdAndCode(String pilId, String code) {
        return jooq
            .selectFrom(
                Tue4BmAssets.BM_ASSETS.join(Tue4LoreEquipment.LORE_EQUIPMENT).on(
                    Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_ID.eq(Tue4BmAssets.BM_ASSETS.BM_DEFAULT_ID)))
            .where(Tue4BmAssets.BM_ASSETS.BM_PIL_ID.equalIgnoreCase(pilId).and(Tue4LoreEquipment.LORE_EQUIPMENT.LEQ_CODE.equalIgnoreCase(code)))
            .fetch();
    }
}
