package com.tokenplay.ue4.model.repositories.impl;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SelectOffsetStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tokenplay.ue4.model.db.tables.Tue4Match;
import com.tokenplay.ue4.model.db.tables.Tue4MatchEvent;
import com.tokenplay.ue4.model.db.tables.Tue4Participation;
import com.tokenplay.ue4.model.db.tables.records.ParticipationRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;
import com.tokenplay.ue4.model.repositories.ParticipationsDB;

@Repository
@Transactional
public class JOOQParticipationsDB implements ParticipationsDB {
    private static final int PARTICIPATIONS_TO_SUMMARISE_STEP = 500;
    private final DSLContext jooq;

    @Autowired
    public JOOQParticipationsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<ParticipationRecord> findAll() {
        return jooq.selectFrom(Tue4Participation.PARTICIPATION).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public ParticipationRecord findById(String id) {
        return jooq.selectFrom(Tue4Participation.PARTICIPATION).where(Tue4Participation.PARTICIPATION.PAR_ID.eq(id)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public Result<Record> findNonSummarisedParticipations() {
        Tue4Participation par = Tue4Participation.PARTICIPATION.as("PAR");
        Tue4Participation p = Tue4Participation.PARTICIPATION.as("P");

        SelectOffsetStep<Record1<String>> subselect =
            jooq.select(p.PAR_ID).from(p.join(Tue4Match.MATCH).on(p.PAR_MCH_ID.eq(Tue4Match.MATCH.MCH_ID)))
                .where(Tue4Match.MATCH.MCH_DATE_END.isNotNull().and(p.PAR_DATE_SUMMARY.isNull())).limit(PARTICIPATIONS_TO_SUMMARISE_STEP);

        @SuppressWarnings("unchecked")
        Result<Record> result =
            jooq.select()
                .from(par.join(subselect).on(par.PAR_ID.eq((Field<String>) subselect.field("PAR_ID"))))
                .leftOuterJoin(Tue4MatchEvent.MATCH_EVENT)
                .on(Tue4MatchEvent.MATCH_EVENT.MEV_PAR_ID_SOURCE.eq(par.PAR_ID).or(Tue4MatchEvent.MATCH_EVENT.MEV_PAR_ID_TARGET.eq(par.PAR_ID))
                    .and(Tue4MatchEvent.MATCH_EVENT.MEV_TYPE.eq("KILL_GEAR"))).fetch();
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRecord> getActiveMatchForPilot(ServerRecord server, PilotRecord pilot) {
        return jooq
            .select()
            .from(Tue4Participation.PARTICIPATION)
            .join(Tue4Match.MATCH)
            .on(Tue4Participation.PARTICIPATION.PAR_MCH_ID.eq(Tue4Match.MATCH.MCH_ID))
            .where(
                Tue4Participation.PARTICIPATION.PAR_PIL_ID.eq(pilot.getPilId()).and(Tue4Match.MATCH.MCH_SRV_ID.eq(server.getSrvId()))
                    .and(Tue4Match.MATCH.MCH_SRV_ID.eq(pilot.getPilSrvId())).and(Tue4Match.MATCH.MCH_DATE_END.isNull())).fetch()
            .into(ParticipationRecord.class);
    }

    @Override
    public int endParticipationsByPilotId(String pilId) {
        return jooq.update(Tue4Participation.PARTICIPATION).set(Tue4Participation.PARTICIPATION.PAR_DATE_END, DSL.currentOffsetDateTime())
            .where(Tue4Participation.PARTICIPATION.PAR_PIL_ID.eq(pilId).and(Tue4Participation.PARTICIPATION.PAR_DATE_END.isNull())).execute();
    }
}
