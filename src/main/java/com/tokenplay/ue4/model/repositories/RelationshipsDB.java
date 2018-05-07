package com.tokenplay.ue4.model.repositories;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.jooq.Result;

import lombok.Data;
import com.tokenplay.ue4.model.RelationshipKey;
import com.tokenplay.ue4.model.db.tables.records.MatchRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.model.db.tables.records.RelationshipRecord;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;

public interface RelationshipsDB {
    @Data
    public static final class FriendRecord {
        final PilotRecord friend;
        final RelationshipRecord relationship;
        final ServerRecord server;
        final MatchRecord match;
    }

    @Data
    public static final class IgnoredRecord {
        final PilotRecord friend;
        final RelationshipRecord relationship;
    }

    Result<RelationshipRecord> findAll();

    RelationshipRecord findById(RelationshipKey key);

    RelationshipRecord findByAnyId(RelationshipKey key);

    // *******************************************************************

    Pair<PilotRecord, List<FriendRecord>> findFriendsByPilotToken(String token);

    Pair<PilotRecord, List<IgnoredRecord>> findIgnoredByPilotToken(String token);
}
