package com.tokenplay.ue4.model.repositories;

import java.util.List;

import com.tokenplay.ue4.model.db.tables.pojos.EventScore;
import com.tokenplay.ue4.model.db.tables.records.EventScoreRecord;

public interface EventScoresDB {

    List<EventScore> findAll(String scoId);

    EventScoreRecord findById(String id);

}
