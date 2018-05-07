package com.tokenplay.ue4.model.repositories;

import org.apache.commons.lang3.tuple.Pair;
import org.jooq.Record;
import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.pojos.Server;
import com.tokenplay.ue4.model.db.tables.records.MatchRecord;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;

public interface MatchesDB {
    Result<MatchRecord> findAll();

    MatchRecord findById(String id);

    // *******************************************************************

    void endServerMatch(String srvId);

    void reactivateMatch(MatchRecord match);

    void endMatch(MatchRecord match);

    String getDateInitString(MatchRecord match);

    Result<Record> listActiveServers(String pilLastIp);

    Pair<MatchRecord, ServerRecord> findMatchAndServer(String matchId);

    int endCurrentMatchAndPrevious(Server server);

    int endUnendedMatchesThatShould();

}
