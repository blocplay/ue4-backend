package com.tokenplay.ue4.model.repositories.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.RelationshipKey;
import com.tokenplay.ue4.model.db.tables.Tue4Match;
import com.tokenplay.ue4.model.db.tables.Tue4Pilot;
import com.tokenplay.ue4.model.db.tables.Tue4Relationship;
import com.tokenplay.ue4.model.db.tables.Tue4Server;
import com.tokenplay.ue4.model.db.tables.records.MatchRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.model.db.tables.records.RelationshipRecord;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;
import com.tokenplay.ue4.model.repositories.RelationshipsDB;
import com.tokenplay.ue4.www.api.Friend;

@Repository
@Transactional
public class JOOQRelationshipsDB implements RelationshipsDB {
    private final DSLContext jooq;

    @Autowired
    public JOOQRelationshipsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<RelationshipRecord> findAll() {
        return jooq.selectFrom(Tue4Relationship.RELATIONSHIP).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public RelationshipRecord findById(RelationshipKey key) {
        return jooq
            .selectFrom(Tue4Relationship.RELATIONSHIP)
            .where(
                Tue4Relationship.RELATIONSHIP.REL_PIL_ID_SOURCE.eq(key.getSourcePilotId()).and(
                    Tue4Relationship.RELATIONSHIP.REL_PIL_ID_TARGET.eq(key.getTargetPilotId()))).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public RelationshipRecord findByAnyId(RelationshipKey key) {
        return jooq
            .selectFrom(Tue4Relationship.RELATIONSHIP)
            .where(
                Tue4Relationship.RELATIONSHIP.REL_PIL_ID_SOURCE.eq(key.getSourcePilotId()).and(
                    Tue4Relationship.RELATIONSHIP.REL_PIL_ID_TARGET.eq(key.getTargetPilotId())))
            .or(Tue4Relationship.RELATIONSHIP.REL_PIL_ID_TARGET.eq(key.getSourcePilotId()).and(
                Tue4Relationship.RELATIONSHIP.REL_PIL_ID_SOURCE.eq(key.getTargetPilotId()))).fetchOne();
    }

    @Override
    public Pair<PilotRecord, List<FriendRecord>> findFriendsByPilotToken(String token) {
        Pair<PilotRecord, List<FriendRecord>> result = null;

        Tue4Relationship tRelationship = Tue4Relationship.RELATIONSHIP.as("rel");
        Tue4Pilot sourceT = Tue4Pilot.PILOT.as("source");
        Tue4Pilot targetT = Tue4Pilot.PILOT.as("target");

        Result<Record> recordResults1 =
            jooq.selectFrom(
                tRelationship
                    .join(sourceT)
                    .on(tRelationship.REL_PIL_ID_SOURCE.eq(sourceT.PIL_ID))
                    .join(
                        targetT.leftOuterJoin(
                            Tue4Server.SERVER.leftOuterJoin(Tue4Match.MATCH).on(
                                Tue4Match.MATCH.MCH_ID.eq(Tue4Server.SERVER.SRV_MCH_ID).and(Tue4Match.MATCH.MCH_DATE_END.isNull()))).on(
                            Tue4Server.SERVER.SRV_ID.eq(targetT.PIL_SRV_ID))).on(tRelationship.REL_PIL_ID_TARGET.eq(targetT.PIL_ID)))
                .where(sourceT.PIL_TOKEN.eq(token).or(sourceT.PIL_ID.eq(JOOQPilotsDB.TEST_PILOT_ID).and(sourceT.PIL_ID.eq(token))))
                .and(tRelationship.REL_STATUS.notEqual(Friend.STATUS_IGNORED)).fetch();
        Result<Record> recordResults2 =
            jooq.selectFrom(
                tRelationship
                    .join(sourceT)
                    .on(tRelationship.REL_PIL_ID_TARGET.eq(sourceT.PIL_ID))
                    .join(
                        targetT.leftOuterJoin(
                            Tue4Server.SERVER.leftOuterJoin(Tue4Match.MATCH).on(
                                Tue4Match.MATCH.MCH_ID.eq(Tue4Server.SERVER.SRV_MCH_ID).and(Tue4Match.MATCH.MCH_DATE_END.isNull()))).on(
                            Tue4Server.SERVER.SRV_ID.eq(targetT.PIL_SRV_ID))).on(tRelationship.REL_PIL_ID_SOURCE.eq(targetT.PIL_ID)))
                .where(sourceT.PIL_TOKEN.eq(token).or(sourceT.PIL_ID.eq(JOOQPilotsDB.TEST_PILOT_ID).and(sourceT.PIL_ID.eq(token))))
                .and(tRelationship.REL_STATUS.notEqual(Friend.STATUS_IGNORED)).fetch();
        recordResults1.addAll(recordResults2);
        if (recordResults1.isNotEmpty()) {
            PilotRecord source = null;
            List<FriendRecord> friends = new ArrayList<>(recordResults1.size());
            for (Record record : recordResults1) {
                if (source == null) {
                    source = record.into(sourceT);
                }
                PilotRecord target = null;
                RelationshipRecord relationship = null;
                ServerRecord server = null;
                MatchRecord match = null;
                if (record.getValue(targetT.PIL_ID) != null) {
                    target = record.into(targetT);
                    relationship = record.into(RelationshipRecord.class);
                    if (record.getValue(Tue4Server.SERVER.SRV_ID) != null) {
                        server = record.into(ServerRecord.class);
                        if (record.getValue(Tue4Match.MATCH.MCH_ID) != null) {
                            match = record.into(MatchRecord.class);
                        }
                    }
                }
                friends.add(new FriendRecord(target, relationship, server, match));
            }
            result = MutablePair.of(source, friends);
        }
        return result;
    }

    @Override
    public Pair<PilotRecord, List<IgnoredRecord>> findIgnoredByPilotToken(String token) {
        Pair<PilotRecord, List<IgnoredRecord>> result = null;

        Tue4Relationship tRelationship = Tue4Relationship.RELATIONSHIP.as("rel");
        Tue4Pilot sourceT = Tue4Pilot.PILOT.as("source");
        Tue4Pilot targetT = Tue4Pilot.PILOT.as("target");

        Result<Record> recordResults1 =
            jooq.selectFrom(
                tRelationship.join(sourceT).on(tRelationship.REL_PIL_ID_SOURCE.eq(sourceT.PIL_ID)).join(targetT)
                    .on(tRelationship.REL_PIL_ID_TARGET.eq(targetT.PIL_ID)))
                .where(sourceT.PIL_TOKEN.eq(token).or(sourceT.PIL_ID.eq(JOOQPilotsDB.TEST_PILOT_ID).and(sourceT.PIL_ID.eq(token))))
                .and(tRelationship.REL_STATUS.eq(Friend.STATUS_IGNORED)).fetch();
        Result<Record> recordResults2 =
            jooq.selectFrom(
                tRelationship.join(sourceT).on(tRelationship.REL_PIL_ID_TARGET.eq(sourceT.PIL_ID)).join(targetT)
                    .on(tRelationship.REL_PIL_ID_SOURCE.eq(targetT.PIL_ID)))
                .where(sourceT.PIL_TOKEN.eq(token).or(sourceT.PIL_ID.eq(JOOQPilotsDB.TEST_PILOT_ID).and(sourceT.PIL_ID.eq(token))))
                .and(tRelationship.REL_STATUS.eq(Friend.STATUS_IGNORED)).fetch();
        recordResults1.addAll(recordResults2);
        if (recordResults1.isNotEmpty()) {
            PilotRecord source = null;
            List<IgnoredRecord> ignored = new ArrayList<>(recordResults1.size());
            result = new MutablePair<>();
            for (Record record : recordResults1) {
                if (source == null) {
                    source = record.into(sourceT);
                }
                PilotRecord target = null;
                RelationshipRecord relationship = null;
                if (record.getValue(targetT.PIL_ID) != null) {
                    target = record.into(targetT);
                    relationship = record.into(RelationshipRecord.class);
                }
                ignored.add(new IgnoredRecord(target, relationship));
            }
            result = MutablePair.of(source, ignored);
        }
        return result;
    }
}
