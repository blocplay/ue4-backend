package com.tokenplay.ue4.logic;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.chat.CommunicationsManager;
import com.tokenplay.ue4.model.db.tables.Tue4MatchEvent;
import com.tokenplay.ue4.model.db.tables.Tue4Pilot;
import com.tokenplay.ue4.model.db.tables.pojos.Server;
import com.tokenplay.ue4.model.db.tables.records.BmAccountsRecord;
import com.tokenplay.ue4.model.db.tables.records.GameModeRecord;
import com.tokenplay.ue4.model.db.tables.records.GearInstanceRecord;
import com.tokenplay.ue4.model.db.tables.records.GearModelRecord;
import com.tokenplay.ue4.model.db.tables.records.InventoryInstanceRecord;
import com.tokenplay.ue4.model.db.tables.records.InventoryLocationRecord;
import com.tokenplay.ue4.model.db.tables.records.InventoryObjectRecord;
import com.tokenplay.ue4.model.db.tables.records.MapRecord;
import com.tokenplay.ue4.model.db.tables.records.MapcycleRecord;
import com.tokenplay.ue4.model.db.tables.records.MapmodeRecord;
import com.tokenplay.ue4.model.db.tables.records.MatchRecord;
import com.tokenplay.ue4.model.db.tables.records.ParticipationRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;
import com.tokenplay.ue4.model.db.tables.records.UsersRecord;
import com.tokenplay.ue4.model.repositories.AccountsDB;
import com.tokenplay.ue4.model.repositories.GameMapsDB;
import com.tokenplay.ue4.model.repositories.GameModesDB;
import com.tokenplay.ue4.model.repositories.GearInstancesDB;
import com.tokenplay.ue4.model.repositories.MapCycleDB;
import com.tokenplay.ue4.model.repositories.MapmodesDB;
import com.tokenplay.ue4.model.repositories.MatchEventsDB;
import com.tokenplay.ue4.model.repositories.MatchesDB;
import com.tokenplay.ue4.model.repositories.ParticipationsDB;
import com.tokenplay.ue4.model.repositories.PilotsDB;
import com.tokenplay.ue4.model.repositories.ServersDB;
import com.tokenplay.ue4.model.repositories.UiThemesDB;
import com.tokenplay.ue4.model.repositories.UsersDB;
import com.tokenplay.ue4.tasks.LeaderboardUpdater;
import com.tokenplay.ue4.tasks.LeaderboardUpdater.PilotStats;

@Component
@Transactional(readOnly = true)
@Slf4j
@Data
public class DataProvider {
    @Autowired
    DSLContext jooq;

    @Autowired
    ServersDB servers;

    @Autowired
    MatchesDB matches;

    @Autowired
    PilotsDB pilots;

    @Autowired
    ParticipationsDB participations;

    @Autowired
    UsersDB users;

    @Autowired
    MatchEventsDB events;

    @Autowired
    UiThemesDB themes;

    @Autowired
    MapmodesDB mapmodes;

    @Autowired
    GearInstancesDB gearInstances;

    @Autowired
    GameMapsDB gameMaps;

    @Autowired
    GameModesDB gameModes;

    @Autowired
    MapCycleDB mapCycles;

    @Autowired
    AccountsDB accounts;

    @Autowired
    CommunicationsManager communicationsManager;

    public OffsetDateTime getNow() {
        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(TimeZone.getDefault().getOffset(new Date().getTime()) / 1000);
        return OffsetDateTime.of(LocalDateTime.now(), zoneOffset);
    }

    public Timestamp getNowOld() {
        //return (Timestamp) servers.getNow();
        return new Timestamp(System.currentTimeMillis() - TimeZone.getDefault().getOffset(new Date().getTime()));
    }

    public Result<PilotRecord> findInactivePilots(long minutes) {
        OffsetDateTime timeInThePast = servers.getNow().minusHours(1);
        return pilots.findInactivePilots(timeInThePast);
    }

    public ServerRecord findServerById(String id) {
        return servers.findById(id);
    }

    ServerRecord findServerRecordById(String id) {
        return servers.findById(id);
    }

    MapRecord findGameMap(String id) {
        return gameMaps.findById(id);
    }

    GameModeRecord findGameMode(String id) {
        return gameModes.findById(id);
    }

    public Result<ServerRecord> SelectActiveServers() {
        return servers.findActiveServers();
    }

    public Result<Record> findNonSummarisedParticipations() {
        return participations.findNonSummarisedParticipations();
    }

    public Result<Record> findEnabledMapModes() {
        return mapmodes.findEnabled();
    }

    public Result<Record> findServerEnabled(String serverId) {
        return mapmodes.findServerEnabled(serverId);
    }

    public UsersRecord findUserByEmail(String email) {
        return users.findByEmail(email);
    }

    public Pair<GearInstanceRecord, GearModelRecord> findGearInstance(String id) {
        return gearInstances.findById(id);
    }

    public MapmodeRecord findMapMode(String srvId, String mamId) {
        return mapmodes.findById(srvId, mamId);
    }

    PilotRecord findPilotByToken(String token) {
        return pilots.findByToken(token);
    }

    List<ParticipationRecord> getActiveMatchForPilot(ServerRecord server, PilotRecord pilot) {
        return participations.getActiveMatchForPilot(server, pilot);
    }

    MatchRecord findMatch(String id) {
        return matches.findById(id);
    }

    public ParticipationRecord findParticipation(String id) {
        return participations.findById(id);
    }

    public void finishServer(Server server) {
        try {
            servers.stop(server);
            // Cleaning the server chat room and parting from it
            communicationsManager.emptyServerRoom(server);
        } catch (Exception e) {
            log.error("Error finishing {}'s  match abruptly.", server.getSrvAlias(), e);
        }
    }

    public ServerRecord getServer(String id) {
        return servers.findById(id);
    }

    int endCurrentMatchAndPrevious(Server server) {
        return matches.endCurrentMatchAndPrevious(server);
    }

    Pair<MatchRecord, ServerRecord> findMatchAndServer(String matchId) {
        return matches.findMatchAndServer(matchId);
    }

    void endMatch(MatchRecord match) {
        matches.endMatch(match);
    }

    Pair<PilotRecord, ServerRecord> findPilotAndServer(String token, String serverId) {
        return pilots.findPilotAndServer(token, serverId);
    }

    public void stop(Server server) {
        servers.stop(server);
    }

    Triple<PilotRecord, ServerRecord, MatchRecord> findPilotAndServerAndMatch(String token, String serverId) {
        return pilots.findPilotAndServerAndMatch(token, serverId);
    }

    public Result<GearInstanceRecord> findGearDefinitionsForPilot(String pilId) {
        return pilots.findGearDefinitionsForPilot(pilId);
    }

    Object jsonModelComplete(PilotRecord pilot, GearInstanceRecord instance, GearModelRecord model,
        List<Triple<InventoryInstanceRecord, InventoryObjectRecord, InventoryLocationRecord>> inventoryList) {
        return gearInstances.jsonModelComplete(pilot, instance, model, inventoryList);
    }

    public int endUnendedMatchesThatShould() {
        return matches.endUnendedMatchesThatShould();
    }

    Triple<GearInstanceRecord, GearModelRecord, List<Triple<InventoryInstanceRecord, InventoryObjectRecord, InventoryLocationRecord>>> fullGearInstance(
        String gearID) {
        return gearInstances.fullGearInstance(gearID);
    }

    List<Pair<GearInstanceRecord, GearModelRecord>> findByPilotId(String id) {
        return gearInstances.findByPilotId(id);
    }


    public List<Triple<GearInstanceRecord, GearModelRecord, List<Triple<InventoryInstanceRecord, InventoryObjectRecord, InventoryLocationRecord>>>> fullPilotGearInstances(
        String pilId) {
        return gearInstances.fullPilotGearInstances(pilId);
    }

    public void clean(PilotRecord pilot) {
        pilots.clean(pilot);
    }

    public String getSrvZMQAddress(ServerRecord server) {
        return servers.getSrvZMQAddress(server);
    }

    public Configuration configuration() {
        return jooq.configuration();
    }

    boolean isRegisteredForChampionship(PilotRecord pilot, String chaId) {
        return pilots.isRegisteredForChampionship(pilot, chaId);
    }

    public MapmodeRecord findMapMode(String queryServerId, String mapId, String gameMode, Boolean bots) {
        return mapmodes.findMapMode(queryServerId, mapId, gameMode, bots);
    }

    public void provideGears() {
        pilots.provideGears();
    }

    public void provideCredit() {
        //pilots.provideCredit();
    }

    Result<MapcycleRecord> findBySrvId(String srvId) {
        return mapCycles.findBySrvId(srvId);
    }

    public BmAccountsRecord findAccountById(String id) {
        return accounts.findById(id);
    }

    BmAccountsRecord findAccountByPilotId(String pilId) {
        return accounts.findByPilId(pilId);
    }

    BmAccountsRecord findAccountByCorpId(String corpId) {
        return accounts.findByCorpId(corpId);
    }

    @Async
    public void summariseMatch(MatchRecord match, ServerRecord server) {
        Map<String, BigDecimal> factors = new HashMap<>();
        Map<String, Integer> scoreByEvent = new HashMap<>();
        LeaderboardUpdater.obtainScoreConfiguration(this, "DEFAULT", factors, scoreByEvent);
        Result<Record> events = LeaderboardUpdater.getEventsFromPilotsFromOneMatch(getJooq(), match.getMchId());
        Map<String, PilotStats> pilotsById = new HashMap<>();
        for (Record record : events) {
            String pilotId = record.getValue(Tue4Pilot.PILOT.PIL_ID);
            String pilotCallsign = record.getValue(Tue4Pilot.PILOT.PIL_CALLSIGN);
            String event = record.getValue(Tue4MatchEvent.MATCH_EVENT.MEV_TYPE);
            Integer count = (Integer) record.getValue(LeaderboardUpdater.COUNT_COLUMN);
            PilotStats pilotStats = pilotsById.get(pilotId);
            if (pilotStats == null) {
                pilotStats = new PilotStats(pilotId, pilotCallsign);
                pilotsById.put(pilotId, pilotStats);
            }
            pilotStats.addEvents(event, count, scoreByEvent);
        }
        log.info("Match finished at server: {} \n --------------------", server.getSrvAlias());
        pilotsById.forEach((id, stats) -> {
            log.info("{}: kills:{}, deaths:{}, damage:{}", stats.getCallsign(), stats.getKills(), stats.getDeaths(), stats.getScore());
        });
    }
}
