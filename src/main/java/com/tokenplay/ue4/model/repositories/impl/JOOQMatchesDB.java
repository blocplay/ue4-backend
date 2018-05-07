package com.tokenplay.ue4.model.repositories.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.model.db.tables.Tue4Championship;
import com.tokenplay.ue4.model.db.tables.Tue4Match;
import com.tokenplay.ue4.model.db.tables.Tue4Participation;
import com.tokenplay.ue4.model.db.tables.Tue4Pilot;
import com.tokenplay.ue4.model.db.tables.Tue4Server;
import com.tokenplay.ue4.model.db.tables.pojos.Server;
import com.tokenplay.ue4.model.db.tables.records.MatchRecord;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;
import com.tokenplay.ue4.model.repositories.MatchesDB;
import com.tokenplay.ue4.model.repositories.ServersDB;
import com.tokenplay.ue4.www.controllers.ui.ClientInterfaceAPI;

@Repository
@Transactional
@Slf4j
public class JOOQMatchesDB implements MatchesDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQMatchesDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<MatchRecord> findAll() {
        return jooq.selectFrom(Tue4Match.MATCH).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public MatchRecord findById(String id) {
        return jooq.selectFrom(Tue4Match.MATCH).where(Tue4Match.MATCH.MCH_ID.eq(id)).fetchOne();
    }

    @Override
    public String getDateInitString(MatchRecord match) {
        return ((SimpleDateFormat) ClientInterfaceAPI.df.clone()).format(match.getMchDateInit());
    }

    @Override
    public Result<Record> listActiveServers(String pilLastIp) {
        List<Field<?>> columns = new LinkedList<>();
        columns.addAll(Arrays.asList(Tue4Server.SERVER.fields()));
        columns.addAll(Arrays.asList(Tue4Match.MATCH.fields()));
        columns.addAll(Arrays.asList(Tue4Championship.CHAMPIONSHIP.fields()));
        List<Field<?>> total = new LinkedList<>();
        total.addAll(columns);
        total.add(Tue4Pilot.PILOT.PIL_ID.countDistinct().as("PILOTS"));
        total.add(Tue4Participation.PARTICIPATION.PAR_ID.countDistinct().as("ACTIVE_PILOTS"));

        Condition visibilityCondition = Tue4Server.SERVER.SRV_VISIBLE.eq(true);
        if (pilLastIp != null) {
            visibilityCondition = visibilityCondition.or(Tue4Server.SERVER.SRV_IP.eq(pilLastIp));
        }

        return jooq
            .select(total)
            .from(Tue4Server.SERVER)
            .leftOuterJoin(Tue4Match.MATCH)
            .on(Tue4Server.SERVER.SRV_MCH_ID.eq(Tue4Match.MATCH.MCH_ID))
            .leftOuterJoin(Tue4Championship.CHAMPIONSHIP)
            .on(Tue4Championship.CHAMPIONSHIP.CHA_ID.eq(Tue4Server.SERVER.SRV_CHA_ID).or(
                Tue4Championship.CHAMPIONSHIP.CHA_ID.eq(Tue4Match.MATCH.MCH_CHA_ID)))
            .leftOuterJoin(Tue4Pilot.PILOT)
            .on(Tue4Pilot.PILOT.PIL_SRV_ID.eq(Tue4Server.SERVER.SRV_ID).and(Tue4Pilot.PILOT.PIL_OFF_LIMITS.eq(Boolean.FALSE)))
            .leftOuterJoin(Tue4Participation.PARTICIPATION)
            .on(Tue4Participation.PARTICIPATION.PAR_MCH_ID.eq(Tue4Match.MATCH.MCH_ID).and(Tue4Participation.PARTICIPATION.PAR_DATE_END.isNull())
                .and(Tue4Participation.PARTICIPATION.PAR_PIL_ID.eq(Tue4Pilot.PILOT.PIL_ID)))
            .where(Tue4Server.SERVER.SRV_STATUS.eq(ServersDB.SERVER_ACTIVE_STATUS).and(visibilityCondition)).groupBy(columns)
            .orderBy(Tue4Server.SERVER.SRV_NUM_PLAYERS).fetch();
    }

    @Override
    public Pair<MatchRecord, ServerRecord> findMatchAndServer(String matchId) {
        final Pair<MatchRecord, ServerRecord> result;
        Record record =
            jooq.selectFrom(Tue4Match.MATCH.join(Tue4Server.SERVER).on(Tue4Match.MATCH.MCH_SRV_ID.eq(Tue4Server.SERVER.SRV_ID)))
                .where(Tue4Match.MATCH.MCH_ID.eq(matchId)).fetchOne();
        if (record != null && record.size() > 0) {
            MatchRecord match = record.into(MatchRecord.class);
            ServerRecord server = record.into(ServerRecord.class);
            result = new ImmutablePair<>(match, server);
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public void endServerMatch(String srvId) {
        Record record =
            jooq.select().from(Tue4Match.MATCH).join(Tue4Server.SERVER).on(Tue4Server.SERVER.SRV_MCH_ID.eq(Tue4Match.MATCH.MCH_ID))
                .where(Tue4Server.SERVER.SRV_ID.eq(srvId)).fetchOne();
        if (record != null) {
            MatchRecord match = record.into(MatchRecord.class);
            log.info("Match {} finished", match.getMchId());
            endMatch(match);
        }
    }

    @Override
    public void reactivateMatch(MatchRecord match) {
        final OffsetDateTime currentTimestamp = match.getMchDateEnd();
        jooq.update(Tue4Match.MATCH).set(Tue4Match.MATCH.MCH_DATE_END, (OffsetDateTime) null).where(Tue4Match.MATCH.MCH_ID.eq(match.getMchId()))
            .execute();
        jooq.update(Tue4Participation.PARTICIPATION)
            .set(Tue4Participation.PARTICIPATION.PAR_DATE_END, (OffsetDateTime) null)
            .where(
                Tue4Participation.PARTICIPATION.PAR_MCH_ID.eq(match.getMchId())
                    .and(Tue4Participation.PARTICIPATION.PAR_DATE_END.eq(currentTimestamp))).execute();
        jooq.update(Tue4Server.SERVER).set(Tue4Server.SERVER.SRV_MCH_ID, match.getMchId()).where(Tue4Server.SERVER.SRV_ID.eq(match.getMchSrvId()))
            .execute();
    }

    @Override
    public void endMatch(MatchRecord match) {
        final Field<OffsetDateTime> currentTimestamp = DSL.currentOffsetDateTime();
        jooq.update(Tue4Match.MATCH).set(Tue4Match.MATCH.MCH_DATE_END, currentTimestamp).set(Tue4Match.MATCH.MCH_BOTS_AMOUNT, 0L)
            .where(Tue4Match.MATCH.MCH_ID.eq(match.getMchId())).execute();
        jooq.update(Tue4Participation.PARTICIPATION).set(Tue4Participation.PARTICIPATION.PAR_DATE_END, currentTimestamp)
            .where(Tue4Participation.PARTICIPATION.PAR_MCH_ID.eq(match.getMchId())).execute();
        jooq.update(Tue4Server.SERVER).set(Tue4Server.SERVER.SRV_MCH_ID, (String) null).where(Tue4Server.SERVER.SRV_MCH_ID.eq(match.getMchId()))
            .execute();
    }

    @Override
    public int endUnendedMatchesThatShould() {
        jooq.update(Tue4Server.SERVER).set(Tue4Server.SERVER.SRV_MCH_ID, (String) null)
            .where(Tue4Server.SERVER.SRV_STATUS.eq(ServersDB.SERVER_INACTIVE_STATUS)).execute();
        Result<Record> result =
            jooq.select()
                .from(Tue4Match.MATCH)
                .join(Tue4Server.SERVER)
                .on(Tue4Server.SERVER.SRV_ID.eq(Tue4Match.MATCH.MCH_SRV_ID))
                .where(
                    Tue4Match.MATCH.MCH_DATE_END.isNull().and(
                        Tue4Server.SERVER.SRV_MCH_ID.isNull().or(Tue4Server.SERVER.SRV_MCH_ID.ne(Tue4Match.MATCH.MCH_ID)))).fetch();
        for (Record record : result) {
            MatchRecord match = record.into(MatchRecord.class);
            ServerRecord server = record.into(ServerRecord.class);
            log.info("Global scan found match {} from server {} that had to be finished abruptly.", new Object[] {
                match.getMchId(), server.getSrvAlias()});
            endMatch(match);
        }
        return result.size();
    }

    @Override
    public int endCurrentMatchAndPrevious(Server server) {
        endServerMatch(server.getSrvId());
        Result<MatchRecord> result =
            jooq.selectFrom(Tue4Match.MATCH).where(Tue4Match.MATCH.MCH_DATE_END.isNull().and(Tue4Match.MATCH.MCH_SRV_ID.eq(server.getSrvId())))
                .fetch();
        for (MatchRecord match : result) {
            log.info("Previous match {} from server {} finished", new Object[] {
                match.getMchId(), server.getSrvAlias()});
            endMatch(match);
        }
        return result.size();
    }
}
