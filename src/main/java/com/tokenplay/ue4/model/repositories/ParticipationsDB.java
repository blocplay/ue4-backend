package com.tokenplay.ue4.model.repositories;

import java.util.List;

import org.jooq.Record;
import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.records.ParticipationRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;

public interface ParticipationsDB {
    Result<ParticipationRecord> findAll();

    ParticipationRecord findById(String id);

    // *******************************************************************

    Result<Record> findNonSummarisedParticipations();

    List<ParticipationRecord> getActiveMatchForPilot(ServerRecord server, PilotRecord pilot);

    int endParticipationsByPilotId(String pilId);
}
