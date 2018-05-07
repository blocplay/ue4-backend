package com.tokenplay.ue4.model.repositories.impl;

import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.model.db.tables.Tue4MatchSession;
import com.tokenplay.ue4.model.db.tables.Tue4MatchSessionParticipant;
import com.tokenplay.ue4.model.db.tables.records.MatchSessionParticipantRecord;
import com.tokenplay.ue4.model.db.tables.records.MatchSessionRecord;
import com.tokenplay.ue4.model.repositories.MatchSessionParticipantsDB;
import com.tokenplay.ue4.model.repositories.MatchSessionsDB;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
@Slf4j
public class JOOQMatchSessionParticipantsDB implements MatchSessionParticipantsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQMatchSessionParticipantsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<MatchSessionParticipantRecord> findForMatchSessionAndPilot(UUID matchSessionId, String pilotId) {
        return jooq.selectFrom(Tue4MatchSessionParticipant.MATCH_SESSION_PARTICIPANT)
            .where(Tue4MatchSessionParticipant.MATCH_SESSION_PARTICIPANT.MSP_MS_ID.eq(matchSessionId))
            .and(Tue4MatchSessionParticipant.MATCH_SESSION_PARTICIPANT.MSP_PILOT_ID.eq(pilotId)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<MatchSessionParticipantRecord> findAllForMatchSession(UUID matchSessionId, boolean active) {
        return jooq.selectFrom(Tue4MatchSessionParticipant.MATCH_SESSION_PARTICIPANT)
            .where(Tue4MatchSessionParticipant.MATCH_SESSION_PARTICIPANT.MSP_MS_ID.eq(matchSessionId))
            .and(Tue4MatchSessionParticipant.MATCH_SESSION_PARTICIPANT.MSP_ACTIVE.eq(active)).fetch();
    }

    @Override
    public void deleteParticipant(UUID matchSessionId, String pilotId) {
        jooq.delete(Tue4MatchSessionParticipant.MATCH_SESSION_PARTICIPANT)
            .where(
                Tue4MatchSessionParticipant.MATCH_SESSION_PARTICIPANT.MSP_MS_ID.eq(matchSessionId).and(
                    Tue4MatchSessionParticipant.MATCH_SESSION_PARTICIPANT.MSP_PILOT_ID.eq(pilotId))).execute();
    }

    @Override
    public int updateToActive(UUID matchSessionId, String pilotId) {
        return jooq
            .update(Tue4MatchSessionParticipant.MATCH_SESSION_PARTICIPANT)
            .set(Tue4MatchSessionParticipant.MATCH_SESSION_PARTICIPANT.MSP_ACTIVE, true)
            .where(
                Tue4MatchSessionParticipant.MATCH_SESSION_PARTICIPANT.MSP_MS_ID.eq(matchSessionId).and(
                    Tue4MatchSessionParticipant.MATCH_SESSION_PARTICIPANT.MSP_PILOT_ID.eq(pilotId))).execute();
    }
}
