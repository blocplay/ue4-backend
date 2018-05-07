package com.tokenplay.ue4.model.repositories;

import com.tokenplay.ue4.model.db.tables.records.MatchSessionParticipantRecord;
import com.tokenplay.ue4.model.db.tables.records.MatchSessionRecord;
import org.jooq.Result;

import java.util.List;
import java.util.UUID;

public interface MatchSessionParticipantsDB {

    Result<MatchSessionParticipantRecord> findForMatchSessionAndPilot(UUID matchSessionId, String pilotId);

    Result<MatchSessionParticipantRecord> findAllForMatchSession(UUID matchSessionId, boolean active);

    void deleteParticipant(UUID matchSessionId, String pilotId);

    int updateToActive(UUID matchSessionId, String pilotId);
}
