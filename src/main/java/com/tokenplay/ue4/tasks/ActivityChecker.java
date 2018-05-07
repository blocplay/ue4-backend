package com.tokenplay.ue4.tasks;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.configuration.BackendConfiguration;
import com.tokenplay.ue4.configuration.HazelCastConfiguration;
import com.tokenplay.ue4.logic.DataProvider;
import com.tokenplay.ue4.model.db.tables.records.MatchEventRecord;
import com.tokenplay.ue4.model.db.tables.records.ParticipationRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;
import com.tokenplay.ue4.www.caching.LiveServer;

@Slf4j
@Data
@Component
public class ActivityChecker {
    public static String prefix = "tcp://";

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private HazelcastInstance hazelcast;

    @Autowired
    BackendConfiguration backendConfiguration;

    @Autowired
    private DataProvider dataProvider;

    @Autowired
    private PlatformTransactionManager transactionManager;

    public static DateTimeFormatter DTF = DateTimeFormatter.ofPattern("MMM-dd-yyyy HH:mm:ss", Locale.ENGLISH);

    public static SimpleDateFormat SDF = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss z", Locale.ENGLISH);

    @Scheduled(fixedDelay = 15_000, initialDelay = 30_000)
    protected void execute() {
        if (backendConfiguration.isTasksEnabled()) {
            // We will just execute the task in the first member of the cluster, in
            // case there is more than one
            Cluster cluster = this.hazelcast.getCluster();
            if (cluster.getMembers().iterator().next().localMember()) {
                log.debug("Performing task in this cluster node {}...", this.hazelcast.getCluster().getLocalMember().getUuid());
                try {
                    final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                            checkServers(120);
                            checkPilots(5);
                            endUnfinishedMatches();
                            summariseParticipations();
                        }
                    });
                } catch (Exception e) {
                    log.error("Error checking activity {}", e);
                }
            }
        } else {
            log.debug("Background tasks are disabled, nothing to do");
        }
    }

    /**
     * Cleans all the dataProvider that have not updated their check in a given
     * period specified in seconds
     */
    private void checkServers(int seconds) {
        try {
            Map<String, LiveServer> liveServers = hazelcast.getMap(HazelCastConfiguration.LIVE_SERVERS_MAP_NAME);
            LocalDateTime cacheThreshold = LocalDateTime.now().minusSeconds(seconds);
            for (LiveServer liveServer : liveServers.values()) {
                ServerRecord server = dataProvider.findServerById(liveServer.getServer().getSrvId());

                log.debug("Checking server {}/{} against threshold {}", new Object[] {
                    liveServer.getServer().getSrvAlias(), liveServer.getLastUpdate(), cacheThreshold});

                if (liveServer.getLastUpdate().isBefore(cacheThreshold)) {

                    log.info("{} server is going to be cleaned {}<{}", new Object[] {
                        liveServer.getServer().getSrvAlias(), liveServer.getLastUpdate(), cacheThreshold});

                    dataProvider.finishServer(liveServer.getServer());

                    server.setSrvVisible(false);
                } else {
                    server.setSrvVisible(true);
                }

                server.store();
            }

        } catch (Exception e) {
            log.error("Error checking servers {}", e);
        }
    }

    /**
     * Cleans all the pilots that have not updated their ping in a given period
     * specified in minutes
     * 
     * @param minutes
     */
    private void checkPilots(int minutes) {
        log.debug("Checking pilots");
        try {
            for (PilotRecord pilot : dataProvider.findInactivePilots(minutes)) {
                dataProvider.clean(pilot);
                log.info("Cleaned {} for inactivity.", pilot.getPilCallsign());
            }
        } catch (Exception e) {
            log.error("Error checking pilots {}", e);
        }
    }


    /*
     * private void checkServerVisibility()
     * {
     * Context context = null;
     * try
     * {
     * context = ZMQ.context(1);
     * log.debug("Checking servers visibility");
     * Result<ServerRecord> serversList = dataProvider.SelectActiveServers();
     * for (ServerRecord server : serversList)
     * {
     * boolean visible = false;
     * ZMQ.Socket socket = null;
     * try
     * {
     * socket = getSocket(context, ZMQ.REQ);
     * log.debug("Checking server {} ", server.getSrvAlias());
     * try
     * {
     * log.debug("Connecting to server {} ({})...", server.getSrvAlias(), prefix
     * + dataProvider.getSrvZMQAddress(server));
     * socket.connect(prefix + dataProvider.getSrvZMQAddress(server));
     * Map<String, Object> theMap = new HashMap<>();
     * theMap.put("Type", "Ping");
     * String requestData = mapper.writeValueAsString(theMap);
     * log.debug("Sending message... {} to {}", requestData, server.getSrvAlias());
     * socket.send(requestData, 0);
     * log.debug("Ping sent to server {}. Waiting for response...", server.getSrvAlias());
     * if (socket.recv(0) != null)
     * {
     * log.debug("Response to ping request received from server {}. Updating DB...",
     * server.getSrvAlias());
     * visible = true;
     * }
     * else
     * {
     * log.debug("We could not connect to server {}.", server.getSrvAlias());
     * }
     * }
     * catch (exception e)
     * {
     * if (!Thread.currentThread().isInterrupted())
     * {
     * log.error("Error checking server {}:{}", server.getSrvAlias(), e);
     * }
     * }
     * }
     * catch (Throwable e)
     * {
     * log.error("Error connecting to server {}:{}", server.getSrvAlias(), e);
     * }
     * finally
     * {
     * if (socket != null)
     * {
     * socket.close();
     * }
     * }
     * try
     * {
     * log.debug("Updating server visibility to {}:{} ", server.getSrvAlias(), visible);
     * server.setSrvVisible(visible);
     * server.store();
     * }
     * catch (exception e)
     * {
     * log.error("Error updating visibility for server {}", server.getSrvAlias(), e);
     * }
     * }
     * }
     * finally
     * {
     * if (context != null)
     * {
     * context.term();
     * }
     * }
     * }
     */



    private void endUnfinishedMatches() {
        try {
            int unendedMatches = dataProvider.endUnendedMatchesThatShould();
            if (unendedMatches > 0) {
                log.debug("Ended {} matches that should have been ended", unendedMatches);
            }
        } catch (Exception e) {
            log.error("Error ending unfinished matches {}", e);
        }
    }

    private void summariseParticipations() {
        try {
            Result<Record> records = dataProvider.findNonSummarisedParticipations();
            log.debug("Summarising {} participations...", records.size());
            Map<String, Long> participationKills = new HashMap<>();
            Map<String, Long> participationDeaths = new HashMap<>();
            for (Record record : records) {
                ParticipationRecord participation = record.into(ParticipationRecord.class);
                MatchEventRecord event = record.into(MatchEventRecord.class);
                long kills = 0;
                long deaths = 0;
                if (participationKills.containsKey(participation.getParId())) {
                    kills = participationKills.get(participation.getParId());
                }
                if (participationDeaths.containsKey(participation.getParId())) {
                    deaths = participationDeaths.get(participation.getParId());
                }
                if (participation.getParId().equals(event.getMevParIdSource())) {
                    kills++;
                } else {
                    deaths++;
                }
                participationKills.put(participation.getParId(), kills);
                participationDeaths.put(participation.getParId(), deaths);
            }
            for (Record record : records) {
                ParticipationRecord participation = record.into(ParticipationRecord.class);
                if (participationKills.containsKey(participation.getParId())) {
                    participation.setParKills(participationKills.get(participation.getParId()));
                    participation.setParDeaths(participationDeaths.get(participation.getParId()));
                    participation.setParDateSummary(dataProvider.getNow());
                    participationKills.remove(participation.getParId());
                    participationDeaths.remove(participation.getParId());
                    participation.store();
                }
            }

        } catch (Exception e) {
            log.error("Error summarising participations {}", e);
        }
    }

    public static ZMQ.Socket getSocket(Context context, int type) {
        ZMQ.Socket socket;
        socket = context.socket(type);
        socket.setSendTimeOut(5000);
        socket.setReceiveTimeOut(5000);
        socket.setLinger(0);
        return socket;
    }
}
