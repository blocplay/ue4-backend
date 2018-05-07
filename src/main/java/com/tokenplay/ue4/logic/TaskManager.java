package com.tokenplay.ue4.logic;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jooq.Result;
import org.jooq.tools.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.HazelcastInstance;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.chat.CommunicationsManager;
import com.tokenplay.ue4.configuration.HazelCastConfiguration;
import com.tokenplay.ue4.model.db.tables.pojos.Server;
import com.tokenplay.ue4.model.db.tables.records.BmAccountsRecord;
import com.tokenplay.ue4.model.db.tables.records.BmTransactionsRecord;
import com.tokenplay.ue4.model.db.tables.records.GameModeRecord;
import com.tokenplay.ue4.model.db.tables.records.GearInstanceRecord;
import com.tokenplay.ue4.model.db.tables.records.GearModelRecord;
import com.tokenplay.ue4.model.db.tables.records.InventoryInstanceRecord;
import com.tokenplay.ue4.model.db.tables.records.InventoryLocationRecord;
import com.tokenplay.ue4.model.db.tables.records.InventoryObjectRecord;
import com.tokenplay.ue4.model.db.tables.records.MapRecord;
import com.tokenplay.ue4.model.db.tables.records.MapcycleRecord;
import com.tokenplay.ue4.model.db.tables.records.MatchEventRecord;
import com.tokenplay.ue4.model.db.tables.records.MatchRecord;
import com.tokenplay.ue4.model.db.tables.records.ParticipationRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;
import com.tokenplay.ue4.model.repositories.ServersDB;
import com.tokenplay.ue4.www.caching.LiveServer;
import com.tokenplay.ue4.www.controllers.CommonAPI;

@Slf4j
@Component
@Transactional
@Data
public class TaskManager {
    private static final String MATCH_ID_PARAM = "MatchID";

    private static final String BOTS_AMOUNT_PARAM = "BotsAmount";

    private static final String GAME_MODE_PARAM = "GameMode";

    private static final String BOTS_PARAM = "Bots";

    private static final String MATCH_TIME_PARAM = "MatchTime";

    private static final String MAP_NAME_PARAM = "MapName";

    private static final String GEAR_ID_PARAM = "GearId";

    public static final String VERSION_PARAM = "version";

    @Autowired
    CommunicationsManager communicationsManager;

    public static final String SERVER_ID_PARAM = "ServerId";
    private static final String USERTOKEN_PARAM = "Usertoken";

    public abstract static class CommsAction {
        public abstract Map perform(Map request, Map<String, Object> response, DataProvider dataProvider, HazelcastInstance hazelcast);
    }

    private final CommsAction serverStartedAction = new CommsAction() {
        @Override
        public Map perform(Map request, Map<String, Object> response, DataProvider dataProvider, HazelcastInstance hazelcast) {
            String serverId = (String) request.get(TaskManager.SERVER_ID_PARAM);
            String version = (String) request.get(TaskManager.VERSION_PARAM);
            String serverName = "Unknown";
            String status = "Error";
            try {
                ServerRecord server = dataProvider.findServerRecordById(serverId);
                if (server != null) {
                    //
                    log.debug("{} notifies it is up & running under version {}", server.getSrvAlias(), version);
                    server.setSrvStatus(ServersDB.SERVER_ACTIVE_STATUS);
                    server.setSrvLastUse(dataProvider.getNow());
                    server.setSrvRunningVersion(version);
                    server.store();
                    // Connecting to chat manager so we create the server channel
                    communicationsManager.createServerCommunicationsRoom(server);
                    //
                    status = "Ok";
                    serverName = server.getSrvAlias();
                    response.put("Server", server.into(Server.class));
                    // getMapCycleFromServer(response, server, dataProvider);
                    //
                    log.debug("{} added to the list of active servers", serverName);
                    Map<String, LiveServer> liveServers = hazelcast.getMap(HazelCastConfiguration.LIVE_SERVERS_MAP_NAME);
                    LiveServer liveServer = new LiveServer(server.into(Server.class), LocalDateTime.now());
                    liveServers.put(server.getSrvId(), liveServer);
                } else {
                    log.info("Server {} could not be found. ", serverId);
                }
            } catch (Exception e) {
                log.error("Error obtaining server from id", e);
            }
            response.put("Status", status);
            response.put("ServerName", serverName);
            return response;
        }
    };

    /*
     * public static List<MapCycleSpec> getCycle(Result<Record> recordSet)
     * {
     * List<MapCycleSpec> reorderedCycle = new ArrayList<>();
     * Map<String, Deque<ImmutableTriple<String, Boolean, Long>>> mapsAndTheirModes = new HashMap<>();
     * Collections.shuffle(recordSet);
     * for (Record record : recordSet)
     * {
     * MapmodeRecord mapMode = record.into(MapmodeRecord.class);
     * MapRecord map = record.into(MapRecord.class);
     * GameModeRecord mode = record.into(GameModeRecord.class);
     * if (StringUtils.isNoneBlank(map.getMapAssetName(), mode.getGamAssetName()))
     * {
     * if (!mapsAndTheirModes.containsKey(map.getMapAssetName()))
     * {
     * Deque<ImmutableTriple<String, Boolean, Long>> mapDeque = new ArrayDeque<>();
     * mapsAndTheirModes.put(map.getMapAssetName(), mapDeque);
     * }
     * mapsAndTheirModes.get(map.getMapAssetName()).push(new ImmutableTriple<>(
     * mode.getGamAssetName(),
     * mapMode.getMamAiEnabled(),
     * mapMode.getMamMatchTime()));
     * }
     * }
     * while (!mapsAndTheirModes.isEmpty())
     * {
     * for (Iterator<String> mapIt = mapsAndTheirModes.keySet().iterator(); mapIt.hasNext();)
     * {
     * String mapAsset = mapIt.next();
     * ImmutableTriple<String, Boolean, Long> mode = mapsAndTheirModes.get(mapAsset).pop();
     * reorderedCycle.add(new MapCycleSpec(mapAsset, mode.getLeft(), mode.getMiddle(), mode.getRight()));
     * if (mapsAndTheirModes.get(mapAsset).isEmpty())
     * {
     * mapIt.remove();
     * }
     * }
     * }
     * return reorderedCycle;
     * }
     */

    /*
     * public static void getMapCycleFromServer(Map<String, Object> response, ServerRecord server, DataProvider dataProvider)
     * {
     * if (server.getSrvUseCycle())
     * {
     * Object[] result = null;
     * Result<Record> recordSet;
     * if (server.getSrvUseCustomCycle())
     * {
     * recordSet = dataProvider.findServerEnabled(server.getSrvId());
     * }
     * else
     * {
     * recordSet = dataProvider.findEnabledMapModes();
     * }
     * List<Object> jsonMapModes = new ArrayList<>();
     * for (MapCycleSpec spec : getCycle(recordSet))
     * {
     * Map<String, Object> cycle = new HashMap<>();
     * cycle.put("Map", spec.getMapName());
     * cycle.put("Mode", spec.getModeName());
     * cycle.put("Bots", spec.isBotsEnabled());
     * if (spec.getMatchTime() != null)
     * {
     * cycle.put("MatchTime", spec.getMatchTime());
     * }
     * jsonMapModes.add(cycle);
     * }
     * result = new Object[jsonMapModes.size()];
     * result = jsonMapModes.toArray(result);
     * response.put("MapCycle", result);
     * }
     * }
     */

    public static void getMapCycleFromServer(Map<String, Object> response, ServerRecord server, DataProvider dataProvider) {
        //Vince: Now we handle this differently
        //First we get everything for this server in the MapCycle table
        List<Object> jsonMapModes = new ArrayList<>();


        Object[] result = null;

        Result<MapcycleRecord> cyc = dataProvider.findBySrvId(server.getSrvId());
        for (MapcycleRecord c : cyc) {
            Map<String, Object> cycle = new HashMap<>();
            cycle.put("MAL", c.getMapAlias());
            cycle.put("MMD", c.getMapMode());
            cycle.put("MAXBC", c.getMapMaxBotCount());
            cycle.put("MAXBL", c.getMapMaxBotSkillLevel());
            cycle.put("MINBL", c.getMapMinBotSkillLevel());
            cycle.put("MTL", c.getMapTimeLimit());
            cycle.put("MTD", c.getMapTimeOfDay());
            cycle.put("MWT", c.getMapWeather());
            cycle.put("MSL", c.getMapMatchScoreLimit());
            if (server.getSrvMatchTime() != null) {
                cycle.put("MT", server.getSrvMatchTime());
            } else {
                cycle.put("MT", -1);
            }

            jsonMapModes.add(cycle);
        }

        result = new Object[jsonMapModes.size()];
        result = jsonMapModes.toArray(result);
        response.put("MapCycle", result);

    }

    private final CommsAction getGearDescriptionAction = new CommsAction() {
        @Override
        public Map perform(Map request, Map<String, Object> response, DataProvider dataProvider, HazelcastInstance hazelcast) {
            String gearID = (String) request.get(TaskManager.GEAR_ID_PARAM);
            log.debug("Asking for gear instance with id {} ", gearID);
            String status = "Error";
            try {
                Triple<GearInstanceRecord, GearModelRecord, List<Triple<InventoryInstanceRecord, InventoryObjectRecord, InventoryLocationRecord>>> gear =
                    dataProvider.fullGearInstance(gearID);
                if (gear != null) {
                    response.put("GearDefinition", dataProvider.jsonModelComplete(null, gear.getLeft(), gear.getMiddle(), gear.getRight()));
                    log.trace("Gear instance with id {} found!", gearID);
                    status = "Ok";
                } else {
                    log.error("Gear with id {} not found", gearID);
                    response.put("Message", "Gear with that id not found: " + gearID);
                }
            } catch (Exception e) {
                log.error("Error obtaining gear description", e);
            }
            response.put("Status", status);
            return response;
        }
    };

    private final CommsAction signalLoginAction = new CommsAction() {
        @SuppressWarnings("unchecked")
        @Override
        public Map perform(Map request, Map<String, Object> response, DataProvider dataProvider, HazelcastInstance hazelcast) {
            // Number team = (Number) request.get("Team");
            String token = (String) request.get(USERTOKEN_PARAM);
            String serverId = (String) request.get(SERVER_ID_PARAM);
            log.debug("Login signaled for {} at server {}", token, serverId);
            String status = "Error";
            try {
                Triple<PilotRecord, ServerRecord, MatchRecord> triple = dataProvider.findPilotAndServerAndMatch(token, serverId);
                if (triple != null) {
                    PilotRecord pilot = triple.getLeft();
                    ServerRecord server = triple.getMiddle();
                    MatchRecord match = triple.getRight();
                    boolean canEnterServer = true;

                    if (StringUtils.isNotBlank(match.getMchChaId())) {
                        canEnterServer = dataProvider.isRegisteredForChampionship(pilot, match.getMchChaId());
                    }

                    if (pilot.getPilOffLimits() || canEnterServer) {

                        /*
                         * List<Triple<GearInstanceRecord, GearModelRecord, List<Triple<InventoryInstanceRecord, InventoryObjectRecord,
                         * InventoryLocationRecord>>>> pilotGears = dataProvider
                         * .fullPilotGearInstances(pilot
                         * .getPilId());
                         * if (pilotGears != null && pilotGears.size() > 0)
                         * {
                         * Object[] gearDefinitions = new Object[pilotGears.size()];
                         * int i = 0;
                         * for (Triple<GearInstanceRecord, GearModelRecord, List<Triple<InventoryInstanceRecord, InventoryObjectRecord,
                         * InventoryLocationRecord>>> gearInstance : pilotGears)
                         * {
                         * log.debug("Added gear {} for server {} ", gearInstance.getLeft().getGeiName(), server.getSrvAlias());
                         * gearDefinitions[i] = dataProvider.jsonModelComplete(pilot, gearInstance.getLeft(), gearInstance.getMiddle(),
                         * gearInstance.getRight());
                         * i++;
                         * }
                         * response.put("GearDefinitions", gearDefinitions);
                         * }
                         * 
                         * if (pilot.getPilDefaultScheme() != null)
                         * {
                         * if (pilot.getPilDefaultScheme().length() == 26)
                         * {
                         * response.put("StringColorScheme", pilot.getPilDefaultScheme());
                         * }
                         * else
                         * {
                         * response.put("ColorScheme", pilot.getPilDefaultScheme());
                         * }
                         * }
                         */
                        List<Pair<GearInstanceRecord, GearModelRecord>> gInstance = dataProvider.findByPilotId(pilot.getPilId());

                        JSONObject jGearCollection = new JSONObject();

                        if (gInstance != null) {
                            for (Pair<GearInstanceRecord, GearModelRecord> g : gInstance) {
                                JSONObject jGearModel = new JSONObject();
                                String getGemName = removeNull(g.getLeft().getGeiName());
                                String getGemId = removeNull(g.getLeft().getGeiGemId());

                                jGearModel.put("Name", getGemName.toString());
                                jGearModel.put("ICID", getGemId);
                                jGearCollection.put(getGemId, jGearModel);
                            }
                        }

                        response.put("ICIDs", jGearCollection);


                        ParticipationRecord participation = new ParticipationRecord();
                        participation.setParId(CommonAPI.newUUID());
                        participation.setParPilId(pilot.getPilId());
                        participation.setParMchId(match.getMchId());
                        participation.setParDateSummary(null);
                        participation.attach(dataProvider.configuration());
                        participation.store();

                        server.setSrvNumPlayers(server.getSrvNumPlayers() + 1);
                        server.store();

                        response.put("Participation", participation.getParId());
                        log.debug("{}'s Login accepted for server {} with running match {}", new Object[] {
                            pilot.getPilCallsign(), server.getSrvAlias(), match.getMchId()});
                        status = "Ok";
                    } else {
                        log.error("{}'s not authorised to join server {} with running match {}, access is restricted.", new Object[] {
                            pilot.getPilCallsign(), server.getSrvAlias(), match.getMchId()});
                        response.put("Message", "Access to this match is restricted. You are not authorised.");
                    }
                } else {
                    log.error("An unknown pilot with token {} tried to login ", token);
                    response.put("Message", "No pilot with that token registered");
                }
            } catch (Exception e) {
                log.error("Error login in user", e);
            }
            response.put("Status", status);
            return response;
        }
    };

    private final CommsAction signalLeaveServerAction = new CommsAction() {
        @Override
        public Map perform(Map request, Map<String, Object> response, DataProvider dataProvider, HazelcastInstance hazelcast) {
            String token = (String) request.get(USERTOKEN_PARAM);
            String serverId = (String) request.get(SERVER_ID_PARAM);
            log.debug("Logout signaled for {} at server {}", token, serverId);
            // We always want to answer Ok so the game keeps working and let's you
            // leave...
            leaveServer(dataProvider, token, serverId);
            response.put("Status", "Ok");
            return response;
        }

    };

    public static PilotRecord leaveServer(DataProvider dataProvider, String token, String serverId) {
        PilotRecord pilot = null;
        if (StringUtils.isNotBlank(token)) {
            try {
                if (StringUtils.isNotBlank(serverId)) {
                    Pair<PilotRecord, ServerRecord> pair = dataProvider.findPilotAndServer(token, serverId);
                    if (pair != null) {
                        pilot = pair.getLeft();
                        ServerRecord server = pair.getRight();
                        if (server == null) {
                            log.error("Pilot {} left a server with id {} and it is no longer there", pilot.getPilCallsign(), serverId);
                        } else if (!server.getSrvId().equals(pilot.getPilSrvId())) {
                            log.error("Pilot {} left a server {} but pilot was not there", pilot.getPilCallsign(), server.getSrvAlias());
                            server.setSrvNumPlayers(server.getSrvNumPlayers() - 1);
                            server.store();
                        } else {
                            log.info("{} left server {} ", pilot.getPilCallsign(), server.getSrvAlias());
                            server.setSrvNumPlayers(server.getSrvNumPlayers() - 1);
                            server.store();
                        }
                    }
                } else {
                    pilot = dataProvider.getPilots().findByToken(token);
                }
                if (pilot != null) {
                    // If pilot are still playing a match (exit from in-game menu used)
                    // we need to close their participation properly before cleaning the
                    // server
                    dataProvider.getParticipations().endParticipationsByPilotId(pilot.getPilId());
                    pilot.setPilSrvId(null);
                    pilot.setPilLeaveReason(null);
                    pilot.setPilLeaveDate(null);
                    pilot.store();
                } else {
                    log.error("An unknown pilot with token {} left ", token);
                }
            } catch (Exception e) {
                log.error("Error when leaving a server", e);
            }
        }
        return pilot;
    }

    private final CommsAction signalLogoutAction = new CommsAction() {
        @Override
        public Map perform(Map request, Map<String, Object> response, DataProvider dataProvider, HazelcastInstance hazelcast) {
            String token = (String) request.get(USERTOKEN_PARAM);
            String serverId = (String) request.get(SERVER_ID_PARAM);
            log.debug("Logout signaled for {} at server {}", token, serverId);
            String status = "Error";
            try {
                ServerRecord server = dataProvider.findServerById(serverId);
                PilotRecord pilot = dataProvider.findPilotByToken(token);
                if (pilot != null && server != null) {
                    List<ParticipationRecord> active = dataProvider.getActiveMatchForPilot(server, pilot);
                    if (active.size() > 0) {
                        for (ParticipationRecord participation : active) {
                            participation.setParDateEnd(dataProvider.getNow());
                            participation.store();
                            log.debug("{}'s logout from match({}) accepted for server {} ", new Object[] {
                                pilot.getPilCallsign(), participation.getParMchId(), server.getSrvAlias()});

                            server.setSrvNumPlayers(server.getSrvNumPlayers() - 1);
                            server.store();
                        }
                    }
                    status = "Ok";
                } else if (pilot == null) {
                    // Pilot already exited the game
                    response.put("Message", "Player already exited the game (in-game menu)");
                } else if (server == null) {
                    log.error("Pilot {} tried to logout from a server with non existant id {}", pilot.getPilCallsign(), serverId);
                    response.put("Message", "No server with that id");
                }
            } catch (Exception e) {
                log.error("Error login out user", e);
            }
            response.put("Status", status);
            return response;
        }
    };

    private final CommsAction signalGameCreatedAction = new CommsAction() {
        @Override
        public Map perform(Map request, Map<String, Object> response, DataProvider dataProvider, HazelcastInstance hazelcast) {
            String serverId = (String) request.get(TaskManager.SERVER_ID_PARAM);
            String mapId = (String) request.get(TaskManager.MAP_NAME_PARAM);
            String gameMode = (String) request.get(TaskManager.GAME_MODE_PARAM);
            Boolean bots = (Boolean) request.get(TaskManager.BOTS_PARAM);
            Integer matchTime = (Integer) request.get(TaskManager.MATCH_TIME_PARAM);
            if (bots == null) {
                bots = Boolean.FALSE;
            }
            ServerRecord server = dataProvider.findServerById(serverId);
            MapRecord theMap = null;
            GameModeRecord theMode = null;
            if (server != null) {
                theMap = dataProvider.findGameMap(mapId);
                if (theMap == null) {
                    theMap = new MapRecord();
                    theMap.setMapId(mapId);
                    theMap.attach(dataProvider.configuration());
                    theMap.store();
                }
                theMode = dataProvider.findGameMode(gameMode);
                if (theMode == null) {
                    theMode = new GameModeRecord();
                    theMode.setGamId(gameMode);
                    theMode.attach(dataProvider.configuration());
                    theMode.store();
                }
                dataProvider.endCurrentMatchAndPrevious(server.into(Server.class));
                MatchRecord match = new MatchRecord();
                match.setMchId(CommonAPI.newUUID());
                match.setMchSrvId(server.getSrvId());
                match.setMchMapId(mapId);
                match.setMcue4mId(gameMode);
                match.setMchSanctioned(server.getSrvSanctioned());
                match.setMchChaId(server.getSrvChaId());
                match.setMchMatchTime(matchTime.longValue());
                match.setMchBotsAmount(0l);
                match.attach(dataProvider.configuration());
                match.store();
                match = dataProvider.findMatch(match.getMchId());
                server.setSrvMchId(match.getMchId());
                server.store();
                response.put("Status", "Ok");
                response.put("Match", match.getMchId());
                log.debug("Match created at server {}, map {}, mode {}, time {}: {}", new Object[] {
                    server.getSrvAlias(), mapId, gameMode, matchTime, match.getMchId()});
                // Just in case, reconnect to the server's room &  notify we have started a match
                communicationsManager.reconnectoToServerCommunicationsRoom(server, mapId, gameMode);
            } else {
                log.error("Match creation error for server {}, map-mode {}-{}. Server not registered", new Object[] {
                    serverId, mapId, gameMode});
            }
            return response;
        }
    };

    private final CommsAction signalMatchStartedAction = new CommsAction() {
        @Override
        public Map perform(Map request, Map<String, Object> response, DataProvider dataProvider, HazelcastInstance hazelcast) {
            String matchId = (String) request.get(TaskManager.MATCH_ID_PARAM);
            String playerCount = request.get("Players").toString();

            log.debug("Match start signaled for match {}", matchId);
            try {
                Pair<MatchRecord, ServerRecord> pair = dataProvider.findMatchAndServer(matchId);
                if (pair != null) {
                    MatchRecord match = pair.getLeft();
                    ServerRecord server = pair.getRight();

                    if (playerCount.isEmpty() == false) {
                        server.setSrvNumPlayers(Long.valueOf(playerCount));
                        server.store();
                    } else {
                        server.setSrvNumPlayers(Long.valueOf(0));
                        server.store();
                    }

                    match.setMchDateStart(dataProvider.getNow());

                    match.store();
                    response.put("Status", "Ok");
                    log.debug("Match {}/{}/{} started ", new Object[] {
                        server.getSrvAlias(), matchId, match.getMchMapId()});
                    // Notifying we have started a match
                    communicationsManager.sendServerNews(server, "started the match in " + match.getMchMapId() + "(" + match.getMcue4mId() + ")");
                } else {
                    log.error("Error trying to start match with id {}. Match not found", matchId);
                    response.put("Status", "Error");
                    response.put("Message", "Match not found");
                }
            } catch (Exception e) {
                log.error("Error starting match ", e);
                response.put("Status", "Error");
                response.put("Message", e.getMessage());
            }
            return response;
        }
    };

    private final CommsAction signalGameFinished = new CommsAction() {
        @Override
        public Map perform(Map request, Map<String, Object> response, DataProvider dataProvider, HazelcastInstance hazelcast) {
            String serverId = (String) request.get(SERVER_ID_PARAM);
            log.debug("Game finish signaled for server {}", serverId);
            ServerRecord server = dataProvider.findServerById(serverId);
            if (server != null) {
                log.debug("Finishing game for server {}", server.getSrvAlias());
                dataProvider.stop(server.into(Server.class));
                // Cleaning the server chat room and parting from it
                // Vince: I don't agree with this logic the cleanup should be when the server is stopped.
                /*
                 * sendServerNews(server.getSrvAlias(), "is being shutdown!");
                 * ChatManager.Singleton.INSTANCE.get().emptyServerRoom(server.getSrvAlias(), server.getSrvId());
                 */
                response.put("Status", "Ok");
                log.debug("Game finish signaled for server {}", server.getSrvAlias());
            } else {
                log.error("Game finish signaled for unknown server {}", serverId);
                response.put("Status", "Error");
                response.put("Message", "Server not registered");
            }
            return response;
        }
    };

    private final CommsAction eventReceived = new CommsAction() {
        @Override
        public Map perform(Map request, Map<String, Object> response, DataProvider dataProvider, HazelcastInstance hazelcast) {
            String sourceParticipaction = (String) request.get("SourceParticipationId");
            String targetParticipaction = (String) request.get("TargetParticipationId");
            String eventTypeName = (String) request.get("EventType");
            Number numberValue = (Number) request.get("NumberValue");
            String textValue = (String) request.get("TextValue");
            // TODO String sourceGear = (String) request.get("SourceGearId");
            // TODO String targetGear = (String) request.get("TargetGearId");
            // Simply store the event data, we don't want to get fancy for this
            // operation, as it is fire&forget
            response.put("Status", "Error");
            try {
                if (StringUtils.isNotBlank(eventTypeName)) {
                    MatchEventRecord event = new MatchEventRecord();
                    event.setMevId(CommonAPI.newUUID());
                    event.setMevType(eventTypeName);
                    event.setMevTxtValue(textValue);
                    event.setMevValue(numberValue.longValue());
                    if (StringUtils.isNotBlank(sourceParticipaction)) {
                        event.setMevParIdSource(sourceParticipaction);
                    }
                    if (StringUtils.isNotBlank(targetParticipaction)) {
                        event.setMevParIdTarget(targetParticipaction);
                    }
                    if (StringUtils.isNotBlank(sourceParticipaction) || StringUtils.isNotBlank(targetParticipaction)) {
                        event.attach(dataProvider.configuration());
                        event.store();
                        log.trace("Event {} stored for participation {}-{}:({})", new Object[] {
                            eventTypeName, sourceParticipaction, targetParticipaction, textValue});
                        response.put("Status", "Ok");
                    } else {
                        log.error("Event {} stored for participation {}-{}:({}) could not be inserted. Participations unknown or null", new Object[] {
                            eventTypeName, sourceParticipaction, targetParticipaction, textValue});
                    }
                } else {
                    log.error("Event for participation {}-{}:({}) could not be inserted. Event type not specified", new Object[] {
                        sourceParticipaction, targetParticipaction, textValue});
                }
            } catch (Exception e) {
                log.error("Error inserting event: ", e);
                response.put("Status", "Error");
                response.put("Message", e.getMessage());
            }
            return response;
        }
    };

    private final CommsAction signalMatchFinishedAction = new CommsAction() {
        @Override
        public Map perform(Map request, Map<String, Object> response, DataProvider dataProvider, HazelcastInstance hazelcast) {
            String matchId = (String) request.get(TaskManager.MATCH_ID_PARAM);
            String status = "Error";
            try {
                Pair<MatchRecord, ServerRecord> pair = dataProvider.findMatchAndServer(matchId);
                if (pair != null) {
                    MatchRecord match = pair.getLeft();
                    ServerRecord server = pair.getRight();
                    dataProvider.endMatch(match);
                    status = "Ok";
                    log.debug("Match {}/{} finished", new Object[] {
                        server.getSrvAlias(), match.getMchId()});
                    if (match.getMchChaId() != null) {
                        dataProvider.summariseMatch(match, server);
                    }
                } else {
                    log.error("Match finish cannot be signaled for unexistent match {}", matchId);
                    response.put("Message", "No match with that id");
                }
            } catch (Exception e) {
                log.error("Error finishing match ", e);
                response.put("Message", e.getMessage());
            }
            response.put("Status", status);
            return response;
        }
    };

    private final CommsAction updateMatchBotsAction = new CommsAction() {
        @Override
        public Map perform(Map request, Map<String, Object> response, DataProvider dataProvider, HazelcastInstance hazelcast) {
            String matchId = (String) request.get(TaskManager.MATCH_ID_PARAM);
            Integer botsAmmount = (Integer) request.get(TaskManager.BOTS_AMOUNT_PARAM);
            String status = "Error";
            try {
                MatchRecord match = dataProvider.findMatch(matchId);
                if (match != null) {
                    match.setMchBotsAmount(botsAmmount.longValue());
                    match.store();
                    status = "Ok";
                    log.trace("Match {} updated the number of bots {}", matchId, botsAmmount);
                } else {
                    log.error("Match finish cannot be signaled for unexistent match {}", matchId);
                    response.put("Message", "No match with that id");
                }
            } catch (Exception e) {
                log.error("Error updating bots amount", e);
                response.put("Message", e.getMessage());
            }
            response.put("Status", status);
            return response;
        }
    };

    private final CommsAction signalTecAddAction = new CommsAction() {
        @Override
        public Map perform(Map request, Map<String, Object> response, DataProvider dataProvider, HazelcastInstance hazelcast) {
            Integer tec = (Integer) request.get("Amount");
            //String arena = (String) request.get("Arena");
            String sourceToken = (String) request.get("SourceToken");
            String targetToken = (String) request.get("TargetToken");

            //Each Arena needs its currency as well
            //Currency is awarded from the arena that the player is playing in
            //return the total tec for the player
            //potential scenario where arena is out of cash?

            log.trace("Add tec {} signaled for token {}", tec, sourceToken);
            try {
                PilotRecord sourcePilot = dataProvider.findPilotByToken(sourceToken);
                PilotRecord targetPilot = dataProvider.findPilotByToken(targetToken);
                if (sourcePilot != null) {
                    BmAccountsRecord account = dataProvider.findAccountByPilotId(sourcePilot.getPilId());
                    if (account != null) {
                        String transferId = CommonAPI.newUUID();
                        BmAccountsRecord accountTarget = null;

                        if (targetPilot != null) {
                            accountTarget = dataProvider.findAccountByPilotId(targetPilot.getPilId());
                        } else {
                            accountTarget = dataProvider.findAccountByCorpId("b34efc6021f64b91a2cbb376537f10da");
                        }

                        //Transfer to pilot
                        BmTransactionsRecord trans = new BmTransactionsRecord();
                        trans.setBmTransactionsId(CommonAPI.newUUID());
                        trans.setBmTransferId(transferId);
                        trans.setBmAccountsId(account.getBmAccountsId());
                        trans.setBmAmount(Double.valueOf(tec));
                        trans.setBmPaymentstatus("COMPLETE");
                        trans.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                        trans.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
                        trans.attach(dataProvider.configuration());
                        trans.store();

                        //Transfer from corp or team member
                        BmTransactionsRecord trans2 = new BmTransactionsRecord();
                        trans2.setBmTransactionsId(CommonAPI.newUUID());
                        trans2.setBmTransferId(transferId);
                        trans2.setBmAccountsId(accountTarget.getBmAccountsId());
                        trans2.setBmAmount(Double.valueOf(tec) * -1);
                        trans2.setBmPaymentstatus("COMPLETE");
                        trans2.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                        trans2.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
                        trans2.attach(dataProvider.configuration());
                        trans2.store();

                        //Now we need to update the balance for both parties
                        BmAccountsRecord balance = dataProvider.findAccountByPilotId(sourcePilot.getPilId());
                        balance.setBmAccountbalance(balance.getBmAccountbalance() + tec);
                        balance.attach(dataProvider.configuration());
                        balance.store();

                        BmAccountsRecord balance2 = null;

                        if (targetPilot != null) {
                            balance2 = dataProvider.findAccountByPilotId(sourcePilot.getPilId());
                        } else {
                            balance2 = dataProvider.findAccountByCorpId("b34efc6021f64b91a2cbb376537f10da");
                        }

                        balance2.setBmAccountbalance(balance2.getBmAccountbalance() + (tec * -1));
                        balance2.attach(dataProvider.configuration());
                        balance2.store();

                        log.trace("Transfer {} tec to pilot {}", new Object[] {
                            tec, sourceToken});
                    }
                }

            } catch (Exception e) {
                log.error("Error awarding tec ", e);
                response.put("Status", "Error");
                response.put("Message", e.getMessage());
            }
            return response;
        }
    };

    public static Map getErrorResponse(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("Status", "Error");
        response.put("Message", e.getMessage());
        return response;
    }

    public static String removeNull(Object nullCheck) {
        if (nullCheck == null) {
            return "";
        } else {
            return nullCheck.toString();
        }
    }

}
