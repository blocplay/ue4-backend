package com.tokenplay.ue4.model.repositories;

import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.LoreGearsRecord;

;

public interface GearsDB {
    Result<LoreGearsRecord> findAll();

    LoreGearsRecord findById(String id);

    LoreGearsRecord findByName(String name);

    Result<LoreGearsRecord> findByFaction(String faction);

    Result<LoreGearsRecord> findByHemisphere(String hemisphere);

    LoreGearsRecord findByGemId(String gemId);

    Result<LoreGearsRecord> findByClass(String gearClass);

    Result<LoreGearsRecord> findByPledge(String pledge);

    Result<LoreGearsRecord> findByCore(String core);

    Result<LoreGearsRecord> findByBacker(String backer);

}
