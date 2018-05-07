package com.tokenplay.ue4.model.repositories.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.HazelcastInstance;

import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.chat.CommunicationsManager;
import com.tokenplay.ue4.configuration.HazelCastConfiguration;
import com.tokenplay.ue4.model.db.tables.Tue4Championship;
import com.tokenplay.ue4.model.db.tables.Tue4Match;
import com.tokenplay.ue4.model.db.tables.Tue4Participation;
import com.tokenplay.ue4.model.db.tables.Tue4Pilot;
import com.tokenplay.ue4.model.db.tables.Tue4Registration;
import com.tokenplay.ue4.model.db.tables.Tue4Server;
import com.tokenplay.ue4.model.db.tables.pojos.Server;
import com.tokenplay.ue4.model.db.tables.records.MatchRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;
import com.tokenplay.ue4.model.repositories.MatchesDB;
import com.tokenplay.ue4.model.repositories.ServersDB;
import com.tokenplay.ue4.www.caching.LiveServer;

@Slf4j
@Repository
@Transactional
public class JOOQServersDB implements ServersDB {
    private final DSLContext jooq;

    private final HazelcastInstance hazelcast;

    @Autowired
    MatchesDB matches;

    @Autowired
    CommunicationsManager communicationsManager;

    @Autowired
    public JOOQServersDB(DSLContext jooq, HazelcastInstance hazelcast) {
        this.jooq = jooq;
        this.hazelcast = hazelcast;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<ServerRecord> findAll() {
        return jooq.selectFrom(Tue4Server.SERVER).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public ServerRecord findById(String id) {
        return jooq.selectFrom(Tue4Server.SERVER).where(Tue4Server.SERVER.SRV_ID.eq(id)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<ServerRecord> findByPilId(String pilId) {
        return jooq.selectFrom(Tue4Server.SERVER).where(Tue4Server.SERVER.SRV_PIL_ID.eq(pilId)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<ServerRecord> findByIp(String ip) {
        return jooq.selectFrom(Tue4Server.SERVER).where(Tue4Server.SERVER.SRV_IP.eq(ip)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<ServerRecord> findActiveServers() {
        return jooq.selectFrom(Tue4Server.SERVER).where(Tue4Server.SERVER.SRV_STATUS.eq(ServersDB.SERVER_ACTIVE_STATUS)).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Server> findActiveServersRaw() {
        Result<ServerRecord> records =
            jooq.selectFrom(Tue4Server.SERVER).where(Tue4Server.SERVER.SRV_STATUS.eq(ServersDB.SERVER_ACTIVE_STATUS)).fetch();
        return records.into(Server.class);
    }

    @Transactional(readOnly = true)
    @Override
    public OffsetDateTime getNow() {
        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(TimeZone.getDefault().getOffset(new Date().getTime()) / 1000);
        return OffsetDateTime.of(LocalDateTime.now(), zoneOffset);
    }

    public Timestamp getNowOld() {
        return new Timestamp(System.currentTimeMillis() - TimeZone.getDefault().getOffset(new Date().getTime()));
    }

    @Override
    public void stop(Server server) {
        // Kick all pilots
        jooq.update(Tue4Pilot.PILOT).set(Tue4Pilot.PILOT.PIL_SRV_ID, (String) null).where(Tue4Pilot.PILOT.PIL_SRV_ID.eq(server.getSrvId())).execute();
        // Stop the current match, if any
        matches.endServerMatch(server.getSrvId());
        // Clean the server
        jooq.update(Tue4Server.SERVER).set(Tue4Server.SERVER.SRV_STATUS, SERVER_INACTIVE_STATUS).set(Tue4Server.SERVER.SRV_MCH_ID, (String) null)
            .set(Tue4Server.SERVER.SRV_NUM_PLAYERS, Long.valueOf(0)).where(Tue4Server.SERVER.SRV_ID.eq(server.getSrvId())).execute();
        // Remove it also from the cache of active servers
        Map<String, LiveServer> LiveServers = hazelcast.getMap(HazelCastConfiguration.LIVE_SERVERS_MAP_NAME);
        LiveServers.remove(server.getSrvId());
        communicationsManager.emptyServerRoom(server);
        log.info("Server {} stopped.", server.getSrvAlias());

    }

    @Override
    public String getSrvZMQAddress(ServerRecord server) {
        StringBuilder theSB = new StringBuilder(server.getSrvIp());
        theSB.append(":");
        if (StringUtils.isNotBlank(server.getSrvZmqPort())) {
            theSB.append(server.getSrvZmqPort());
        } else {
            theSB.append(ServersDB.DEFAULT_ZMQ_PORT);
        }
        return theSB.toString();
    }

    @Override
    public String getSrvUE4Address(ServerRecord server) {
        StringBuilder theSB = new StringBuilder();
        theSB.append(server.getSrvIp());

        if (StringUtils.isNotBlank(server.getSrvUe4Port())) {
            theSB.append(":");
            theSB.append(server.getSrvUe4Port());
        }
        return theSB.toString();
    }

    @Override
    public String getInvisibleUE4Address(ServerRecord server) {
        StringBuilder theSB = new StringBuilder("127.0.0.1");
        if (StringUtils.isNotBlank(server.getSrvUe4Port())) {
            theSB.append(":");
            theSB.append(server.getSrvUe4Port());
        }
        return theSB.toString();
    }

    @Override
    public Result<ServerRecord> findByIpAndUe4Port(String ip, String port) {
        Condition cond = Tue4Server.SERVER.SRV_IP.eq(ip);
        if (port != null) {
            cond = cond.and(Tue4Server.SERVER.SRV_UE4_PORT.eq(port));
        } else {
            cond = cond.and(Tue4Server.SERVER.SRV_UE4_PORT.isNull());
        }
        return jooq.selectFrom(Tue4Server.SERVER).where(Tue4Server.SERVER.SRV_IP.eq(ip).and(cond)).fetch();
    }

    @Override
    public Record findByIdWithPilotCount(String id) {
        List<Field<?>> columns = new LinkedList<>();
        columns.addAll(Arrays.asList(Tue4Server.SERVER.fields()));
        columns.addAll(Arrays.asList(Tue4Match.MATCH.fields()));
        columns.addAll(Arrays.asList(Tue4Championship.CHAMPIONSHIP.fields()));
        List<Field<?>> total = new LinkedList<>();
        total.addAll(columns);
        total.add(Tue4Pilot.PILOT.PIL_ID.countDistinct().as("PILOTS"));
        return jooq.select(total).from(Tue4Server.SERVER).leftOuterJoin(Tue4Match.MATCH).on(Tue4Match.MATCH.MCH_ID.eq(Tue4Server.SERVER.SRV_MCH_ID))
            .leftOuterJoin(Tue4Championship.CHAMPIONSHIP).on(Tue4Championship.CHAMPIONSHIP.CHA_ID.eq(Tue4Server.SERVER.SRV_CHA_ID))
            .leftOuterJoin(Tue4Pilot.PILOT)
            .on(Tue4Pilot.PILOT.PIL_SRV_ID.eq(Tue4Server.SERVER.SRV_ID).and(Tue4Pilot.PILOT.PIL_OFF_LIMITS.eq(Boolean.FALSE)))
            .where(Tue4Server.SERVER.SRV_ID.eq(id)).groupBy(columns).fetchOne();
    }

    @Override
    public boolean isInChampionship(ServerRecord server, MatchRecord match) {
        boolean serverIsSetToChampionship = StringUtils.isNotBlank(server.getSrvChaId());
        boolean currentlyRunningAMatch = StringUtils.isNotBlank(match.getMchId());
        boolean matchIsSetToChampionship = StringUtils.isNotBlank(match.getMchChaId());
        return (currentlyRunningAMatch && matchIsSetToChampionship) || serverIsSetToChampionship;
        // return (currentlyRunningAMatch && matchIsSetToChampionship)
        // || (!currentlyRunningAMatch && serverIsSetToChampionship);
    }

    @Override
    public void kickIdlePilots(ServerRecord server, String reason) {
        // Select the pilots in the server that have no participation and no
        // registration in the server's championship
        Result<Record> records =
            jooq.select()
                .from(Tue4Pilot.PILOT)
                .join(Tue4Server.SERVER)
                .on(Tue4Pilot.PILOT.PIL_SRV_ID.eq(Tue4Server.SERVER.SRV_ID))
                .leftOuterJoin(Tue4Participation.PARTICIPATION)
                .on(Tue4Participation.PARTICIPATION.PAR_MCH_ID.eq(Tue4Server.SERVER.SRV_MCH_ID).and(
                    Tue4Participation.PARTICIPATION.PAR_PIL_ID.eq(Tue4Pilot.PILOT.PIL_ID)))
                .leftOuterJoin(Tue4Registration.REGISTRATION)
                .on(Tue4Registration.REGISTRATION.REG_PIL_ID.eq(Tue4Pilot.PILOT.PIL_ID).and(
                    Tue4Registration.REGISTRATION.REG_CHA_ID.eq(server.getSrvChaId())))
                .where(
                    Tue4Server.SERVER.SRV_ID.eq(server.getSrvId()).and(Tue4Participation.PARTICIPATION.PAR_ID.isNull())
                        .and(Tue4Registration.REGISTRATION.REG_PIL_ID.isNull())).fetch();
        for (PilotRecord pilot : records.into(PilotRecord.class)) {
            log.info("Pilot {} kicked from server {}", pilot.getPilCallsign(), server.getSrvAlias());
            pilot.setPilLeaveReason(reason);
            pilot.setPilLeaveDate(getNow());
            pilot.setPilSrvId(null);
            pilot.store();
        }
    }

    public static void main(String[] args) {
        Timestamp oldTimestamp = new Timestamp(System.currentTimeMillis() - TimeZone.getDefault().getOffset(new Date().getTime()));
        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(TimeZone.getDefault().getOffset(new Date().getTime()) / 1000);
        OffsetDateTime stuff = OffsetDateTime.of(LocalDateTime.now(), zoneOffset);

        System.out.println("Old scheme: " + oldTimestamp.toString());
        System.out.println("New scheme: " + stuff.toString());
    }
}
