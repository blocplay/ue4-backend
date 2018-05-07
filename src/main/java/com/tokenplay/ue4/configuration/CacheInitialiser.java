package com.tokenplay.ue4.configuration;

import java.time.LocalDateTime;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.model.db.tables.pojos.Server;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;
import com.tokenplay.ue4.model.repositories.ServersDB;
import com.tokenplay.ue4.www.caching.LiveServer;

@Data
@Slf4j
public class CacheInitialiser {
    @Autowired
    private HazelcastInstance hazelcast;

    @Autowired
    private ServersDB servers;

    @PostConstruct
    public void init() {
        try {
            log.debug("Initialising");
            Cluster cluster = hazelcast.getCluster();
            if (cluster.getMembers().iterator().next().localMember()) {
                log.debug("Loading active servers from the database...");
                Map<String, LiveServer> liveServers = hazelcast.getMap(HazelCastConfiguration.LIVE_SERVERS_MAP_NAME);
                Result<ServerRecord> result = servers.findActiveServers();
                if (result != null) {
                    for (ServerRecord server : result) {
                        log.info("Server {} is alive, adding to cache...", server.getSrvAlias());
                        liveServers.put(server.getSrvId(), new LiveServer(server.into(Server.class), LocalDateTime.now()));
                    }
                } else {
                    log.info("Active servers list is null");
                }
            }
        } catch (final Throwable t) {
            log.error("Error", t);
        }
    }

}
