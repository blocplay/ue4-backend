package com.tokenplay.ue4.model.repositories;

import java.time.OffsetDateTime;
import java.util.List;

import org.jooq.Record;
import org.jooq.Result;

import com.tokenplay.ue4.model.db.tables.pojos.Server;
import com.tokenplay.ue4.model.db.tables.records.MatchRecord;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;

public interface ServersDB {
    String DEFAULT_UE4_PORT = "7777";

    String DEFAULT_ZMQ_PORT = "10000";

    String SERVER_ACTIVE_STATUS = "ON";

    String SERVER_INACTIVE_STATUS = "OFF";

    Result<ServerRecord> findAll();

    ServerRecord findById(String id);

    // *******************************************************************

    Result<ServerRecord> findByIp(String ip);

    Result<ServerRecord> findActiveServers();

    List<Server> findActiveServersRaw();

    OffsetDateTime getNow();

    void stop(Server server);

    String getSrvZMQAddress(ServerRecord server);

    String getSrvUE4Address(ServerRecord server);

    String getInvisibleUE4Address(ServerRecord server);

    Result<ServerRecord> findByIpAndUe4Port(String ip, String port);

    Record findByIdWithPilotCount(String id);

    boolean isInChampionship(ServerRecord server, MatchRecord match);

    void kickIdlePilots(ServerRecord server, String reason);

    Result<ServerRecord> findByPilId(String pilId);
}
