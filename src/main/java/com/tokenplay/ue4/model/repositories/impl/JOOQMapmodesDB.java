package com.tokenplay.ue4.model.repositories.impl;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4GameMode;
import com.tokenplay.ue4.model.db.tables.Tue4Map;
import com.tokenplay.ue4.model.db.tables.Tue4Mapmode;
import com.tokenplay.ue4.model.db.tables.records.MapmodeRecord;
import com.tokenplay.ue4.model.repositories.MapmodesDB;

@Repository
@Transactional
public class JOOQMapmodesDB implements MapmodesDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQMapmodesDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<MapmodeRecord> findAll() {
        return jooq.selectFrom(Tue4Mapmode.MAPMODE).where(Tue4Mapmode.MAPMODE.MAM_SRV_ID.isNull()).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<MapmodeRecord> findAll(String srvId) {
        return jooq.selectFrom(Tue4Mapmode.MAPMODE).where(Tue4Mapmode.MAPMODE.MAM_SRV_ID.eq(srvId)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public MapmodeRecord findById(String srvId, String mamId) {
        Condition cond;
        if (srvId != null) {
            cond = Tue4Mapmode.MAPMODE.MAM_SRV_ID.eq(srvId);
        } else {
            cond = Tue4Mapmode.MAPMODE.MAM_SRV_ID.isNull();
        }
        return jooq.selectFrom(Tue4Mapmode.MAPMODE).where(Tue4Mapmode.MAPMODE.MAM_ID.eq(mamId).and(cond)).fetchOne();
    }

    /**
     * Find the enabled map modes for the global map cycle (no server specified)
     */
    @Transactional(readOnly = true)
    @Override
    public Result<Record> findEnabled() {
        return jooq
            .selectFrom(
                Tue4Mapmode.MAPMODE.join(Tue4Map.MAP).on(Tue4Mapmode.MAPMODE.MAM_MAP_ID.eq(Tue4Map.MAP.MAP_ID)).join(Tue4GameMode.GAME_MODE)
                    .on(Tue4Mapmode.MAPMODE.MAM_GAM_ID.eq(Tue4GameMode.GAME_MODE.GAM_ID)))
            .where(Tue4Mapmode.MAPMODE.MAM_ENABLED.eq(Boolean.TRUE).and(Tue4Mapmode.MAPMODE.MAM_SRV_ID.isNull())).fetch();
    }

    /**
     * Find the enabled map modes for the global map cycle (no server specified)
     */
    @Transactional(readOnly = true)
    @Override
    public Result<Record> findServerEnabled(String serverId) {
        return jooq
            .selectFrom(
                Tue4Mapmode.MAPMODE.join(Tue4Map.MAP).on(Tue4Mapmode.MAPMODE.MAM_MAP_ID.eq(Tue4Map.MAP.MAP_ID)).join(Tue4GameMode.GAME_MODE)
                    .on(Tue4Mapmode.MAPMODE.MAM_GAM_ID.eq(Tue4GameMode.GAME_MODE.GAM_ID)))
            .where(Tue4Mapmode.MAPMODE.MAM_ENABLED.eq(Boolean.TRUE).and(Tue4Mapmode.MAPMODE.MAM_SRV_ID.eq(serverId))).fetch();
    }

    @Override
    public Record withMapAndMode(String srvId, String mamId) {
        Condition cond;
        if (srvId != null) {
            cond = Tue4Mapmode.MAPMODE.MAM_SRV_ID.eq(srvId);
        } else {
            cond = Tue4Mapmode.MAPMODE.MAM_SRV_ID.isNull();
        }
        return jooq
            .selectFrom(
                Tue4Mapmode.MAPMODE.join(Tue4Map.MAP).on(Tue4Mapmode.MAPMODE.MAM_MAP_ID.eq(Tue4Map.MAP.MAP_ID)).join(Tue4GameMode.GAME_MODE)
                    .on(Tue4Mapmode.MAPMODE.MAM_GAM_ID.eq(Tue4GameMode.GAME_MODE.GAM_ID))).where(Tue4Mapmode.MAPMODE.MAM_ID.eq(mamId).and(cond))
            .fetchOne();
    }

    @Override
    public MapmodeRecord findMapMode(String srvId, String mapId, String gameMode, Boolean bots) {
        Condition cond;
        if (srvId != null) {
            cond = Tue4Mapmode.MAPMODE.MAM_SRV_ID.eq(srvId);
        } else {
            cond = Tue4Mapmode.MAPMODE.MAM_SRV_ID.isNull();
        }
        return jooq
            .selectFrom(Tue4Mapmode.MAPMODE)
            .where(
                Tue4Mapmode.MAPMODE.MAM_GAM_ID.eq(gameMode)
                    .and(Tue4Mapmode.MAPMODE.MAM_MAP_ID.eq(mapId).and(Tue4Mapmode.MAPMODE.MAM_AI_ENABLED.eq(bots))).and(cond)).fetchOne();
    }
}
