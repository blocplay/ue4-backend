package com.tokenplay.ue4.model.repositories;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jooq.Record;
import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.GearInstanceRecord;
import com.tokenplay.ue4.model.db.tables.records.MatchRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;
import com.tokenplay.ue4.model.db.tables.records.UsersRecord;

public interface PilotsDB {
    Result<PilotRecord> findAll();

    PilotRecord findById(String id);

    // *******************************************************************

    PilotRecord findByToken(String token);

    PilotRecord findByCallsign(String callsign);

    Pair<PilotRecord, UsersRecord> findBySteamId(String steamId);

    PilotRecord findByUserId(Long id);

    Pair<PilotRecord, UsersRecord> findByIdWithUser(String id);

    Result<Record> findByTokenWithFullTheme(String token);

    Object[] pilotStatsByToken(String token);

    Result<PilotRecord> findInactivePilots(OffsetDateTime when);

    void clean(PilotRecord pilot);

    Record findPrivateServerStatus(String token, String id);

    Result<Record> findMatchesStatByToken(String token);

    Record findMatchStatusByToken(String token);

    Pair<PilotRecord, UsersRecord> findPilotAndUser(String token);

    Pair<PilotRecord, ServerRecord> findPilotAndServer(String token, String serverId);

    Triple<PilotRecord, ServerRecord, MatchRecord> findPilotAndServerAndMatch(String token, String serverId);

    Result<GearInstanceRecord> findGearDefinitionsForPilot(String pilId);

    boolean isRegisteredForChampionship(PilotRecord pilot, String chaId);

    void provideGears();

    void provideCredit();

    UsersRecord findUserFromSteamId(String steamId);

    List<String> getAuthorisedPilotsForServer(String srvId);


}
