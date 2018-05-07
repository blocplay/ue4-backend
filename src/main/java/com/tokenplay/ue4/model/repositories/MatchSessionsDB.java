package com.tokenplay.ue4.model.repositories;

import com.tokenplay.ue4.model.db.tables.records.MatchSessionParticipantRecord;
import com.tokenplay.ue4.model.db.tables.records.MatchSessionRecord;
import org.jooq.Result;

import java.util.List;
import java.util.UUID;

public interface MatchSessionsDB {
    Result<MatchSessionRecord> findAll();

    MatchSessionRecord findById(UUID id);

    // *******************************************************************

    List<MatchSessionParticipantRecord> findParticipantsById(UUID id);
}
