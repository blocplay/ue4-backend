package com.tokenplay.ue4.model.repositories.impl;

import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.model.db.tables.*;
import com.tokenplay.ue4.model.db.tables.pojos.Server;
import com.tokenplay.ue4.model.db.tables.records.MatchRecord;
import com.tokenplay.ue4.model.db.tables.records.MatchSessionParticipantRecord;
import com.tokenplay.ue4.model.db.tables.records.MatchSessionRecord;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;
import com.tokenplay.ue4.model.repositories.MatchSessionsDB;
import com.tokenplay.ue4.model.repositories.MatchesDB;
import com.tokenplay.ue4.model.repositories.ServersDB;
import com.tokenplay.ue4.www.controllers.ui.ClientInterfaceAPI;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional
@Slf4j
public class JOOQMatchSessionsDB implements MatchSessionsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQMatchSessionsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<MatchSessionRecord> findAll() {
        return jooq.selectFrom(Tue4MatchSession.MATCH_SESSION).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public MatchSessionRecord findById(UUID id) {
        return jooq.selectFrom(Tue4MatchSession.MATCH_SESSION).where(Tue4MatchSession.MATCH_SESSION.MS_ID.eq(id)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public List<MatchSessionParticipantRecord> findParticipantsById(UUID id) {
        return jooq.selectFrom(Tue4MatchSessionParticipant.MATCH_SESSION_PARTICIPANT)
            .where(Tue4MatchSessionParticipant.MATCH_SESSION_PARTICIPANT.MSP_MS_ID.eq(id)).fetch();
    }
}
