package com.tokenplay.ue4.www.controllers.ui;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hazelcast.core.IMap;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.configuration.HazelCastConfiguration;
import com.tokenplay.ue4.logic.DataProvider;
import com.tokenplay.ue4.model.db.tables.records.ChampionshipRecord;
import com.tokenplay.ue4.model.db.tables.records.MatchRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;
import com.tokenplay.ue4.model.db.tables.records.UiThemeAttributeRecord;
import com.tokenplay.ue4.model.db.tables.records.UiThemeRecord;
import com.tokenplay.ue4.tasks.LeaderboardUpdater;
import com.tokenplay.ue4.tasks.LeaderboardUpdater.PilotStats;
import com.tokenplay.ue4.www.api.JSONResponse;
import com.tokenplay.ue4.www.api.LeaderBoardResponse;
import com.tokenplay.ue4.www.api.MatchData;
import com.tokenplay.ue4.www.api.PilotMatchesResponse;
import com.tokenplay.ue4.www.api.PilotStatsResponse;
import com.tokenplay.ue4.www.api.ServerStatusData;
import com.tokenplay.ue4.www.api.ServerStatusDataList;
import com.tokenplay.ue4.www.api.ServerStatusResponse;
import com.tokenplay.ue4.www.controllers.CommonAPI;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Controller
@Transactional
@RequestMapping(value = "/gi/{token}")
public class ClientInterfaceAPI extends CommonAPI {
    @Autowired
    DataProvider dataProvider;

    @Resource(name = HazelCastConfiguration.LEADERBOARD_MAP_NAME)
    IMap<String, PilotStats> cacheMap;

    public static SimpleDateFormat df = new SimpleDateFormat("dd MMM YYYY - HH:mm", Locale.ENGLISH);

    @ResponseBody
    @RequestMapping(value = "/pilot", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public JSONResponse pilotStats(@PathVariable(value = "token") String token) {
        log.debug("Stats requested for token {}", token);
        JSONResponse response = new JSONResponse();
        Object[] stats = getPilots().pilotStatsByToken(token);
        if (stats != null) {
            response = new PilotStatsResponse((Number) stats[0], (Number) stats[1], (Number) stats[2]);
            response.setSuccess(true);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/update_leaderboard", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public JSONResponse updateLeaderboard(@PathVariable(value = "token") String token) {
        JSONResponse response = new JSONResponse();
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            if (cacheMap != null) {
                LeaderboardUpdater.updateLeaderboard(dataProvider, cacheMap);
                response.setSuccess(true);
            }
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/pilot_leaderboard", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public JSONResponse pilotLeaderboard(@PathVariable(value = "token") String token) {
        log.debug("Leaderboard requested for token {}", token);
        JSONResponse response = new JSONResponse();
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            if (cacheMap != null) {
                PilotStats pilotStats;
                Set<PilotStats> surroundingPilots = null;
                if (cacheMap.containsKey(pilot.getPilId())) {
                    pilotStats = cacheMap.get(pilot.getPilId());
                    EntryObject e = new PredicateBuilder().getEntryObject();
                    int max = pilotStats.getPosition() + 2;
                    int min = pilotStats.getPosition() - 2;
                    Predicate predicate = e.get("position").lessEqual(max).and(e.get("position").greaterEqual(min));
                    Collection<PilotStats> surroundingP = cacheMap.values(predicate);
                    surroundingPilots = new TreeSet<>(LeaderboardUpdater.STATS_COMPARATOR);
                    surroundingPilots.addAll(surroundingP);
                } else {
                    pilotStats = new PilotStats(pilot.getPilId(), pilot.getPilCallsign());
                }
                EntryObject e = new PredicateBuilder().getEntryObject();
                Predicate predicate = e.get("position").lessEqual(5);
                Collection<PilotStats> topP = cacheMap.values(predicate);
                Set<PilotStats> topPilots = new TreeSet<>(LeaderboardUpdater.STATS_COMPARATOR);
                topPilots.addAll(topP);
                //
                response = new LeaderBoardResponse(pilotStats, topPilots, surroundingPilots);
                response.setSuccess(true);
            } else {
                log.error("Leaderboard cache is null");
            }
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/server_status", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public JSONResponse serverStats(@PathVariable(value = "token") String token) {
        log.debug("Server stats requested token {}", token);
        JSONResponse response = new JSONResponse();
        Record record = getPilots().findMatchStatusByToken(token);
        if (record != null) {
            ServerRecord server = record.into(ServerRecord.class);
            MatchRecord match = record.into(MatchRecord.class);
            Integer pilots = (Integer) record.getValue("PILOTS");
            response =
                new ServerStatusResponse(server.getSrvName(), match.getMchMapId(), match.getMcue4mId(), server.getSrvMaxPlayers(), pilots,
                    getMatches().getDateInitString(match));
            response.setSuccess(true);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/private_server_status", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public JSONResponse privateServerStatus(@PathVariable(value = "token") String token, @RequestParam(value = "id") String id) {
        log.debug("Private server stats requested for {}", token);
        JSONResponse response = new JSONResponse();
        Record record = getPilots().findPrivateServerStatus(token, id);
        if (record != null) {
            ServerRecord server = record.into(ServerRecord.class);
            MatchRecord match = record.into(MatchRecord.class);
            Integer pilots = (Integer) record.getValue("PILOTS");
            String map = "";
            String mode = "";
            String dateInit = "";
            if (match.getMchId() != null) {
                map = match.getMchMapId();
                mode = match.getMcue4mId();
                dateInit = getMatches().getDateInitString(match);
            }
            response = new ServerStatusResponse(server.getSrvName(), map, mode, server.getSrvMaxPlayers(), pilots, dateInit);
            response.setSuccess(true);
        } else {
            log.error("Private server status requested from an invalid pilot/server combo {}:{}", new Object[] {
                token, id});
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/pilot_matches", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public JSONResponse pilotMatches(@PathVariable(value = "token") String token) {
        log.debug("Matches requested with token {}", token);
        JSONResponse response;
        Result<Record> matchesToShow = getPilots().findMatchesStatByToken(token);
        List<MatchData> orderedData = new ArrayList<>(matchesToShow.size());
        for (Record record : matchesToShow) {
            ServerRecord server = record.into(ServerRecord.class);
            MatchRecord match = record.into(MatchRecord.class);
            orderedData.add(new MatchData(match.getMchId(), match.getMchDateInit(), match.getMchDateEnd(), server.getSrvName(), match.getMchMapId()));
        }
        response = new PilotMatchesResponse(orderedData);
        response.setSuccess(true);
        return response;
    }

    /*
     * @ResponseBody
     * 
     * @RequestMapping(value =
     * { "/servers_status", "/servers_status.hbe" }, produces = MediaType.APPLICATION_JSON_VALUE)
     * 
     * @Transactional(readOnly = true)
     * public JSONResponse serversStatus(@PathVariable(value = "token") String token)
     * {
     * log.debug("Status of the servers requested token {}", token);
     * JSONResponse response = new JSONResponse();
     * PilotRecord pilot = getPilots().findByToken(token);
     * if (pilot != null)
     * {
     * Result<Record> visibleMatches = getMatches().listActiveServers(pilot.getPilLastIp());
     * List<ServerData> orderedData = new ArrayList<>(visibleMatches.size());
     * for (Record record : visibleMatches)
     * {
     * //
     * ServerRecord server = record.into(ServerRecord.class);
     * MatchRecord match = record.into(MatchRecord.class);
     * ChampionshipRecord championship = record.into(ChampionshipRecord.class);
     * //Integer pilotCount = (Integer) record.getValue("PILOTS");
     * //Integer activePilots = (Integer) record.getValue("ACTIVE_PILOTS");
     * String map = "";
     * String mode = "";
     * Timestamp dateInit = null;
     * Long botsAmount = 0l;
     * String championshipName = null;
     * boolean registeredForChampionship = false;
     * if (match.getMchId() != null)
     * {
     * map = match.getMchMapId();
     * mode = match.getMcue4mId();
     * dateInit = match.getMchDateInit();
     * botsAmount = match.getMchBotsAmount();
     * }
     * if (getServers().isInChampionship(server, match))
     * {
     * championshipName = championship.getChaName();
     * String chaId = match.getMchChaId() != null ? match.getMchChaId() : server.getSrvChaId();
     * registeredForChampionship = getPilots().isRegisteredForChampionship(pilot, chaId);
     * }
     * orderedData.add(new ServerData(
     * server.getSrvAlias(),
     * server.getSrvName(),
     * server.getSrvId(),
     * server.getSrvDescription(),
     * server.getSrvRunningVersion(),
     * map,
     * mode,
     * server.getSrvStatus(),
     * dateInit,
     * server.getSrvMaxPlayers(),
     * server.getSrvNumPlayers(),
     * server.getSrvNumPlayers(),
     * botsAmount,
     * server.getSrvDevelopment(),
     * championshipName,
     * registeredForChampionship));
     * }
     * response = new ServerListResponse(orderedData);
     * response.setSuccess(true);
     * }
     * else
     * {
     * log.info("Listing servers: no pilot found with token {}", token);
     * }
     * return response;
     * }
     */

    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = {
        "/servers_status_withkeys", "/servers_withkeys.hbe"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public ServerStatusDataList serversRaw(@PathVariable(value = "token") String token) {
        log.debug("Status of the servers requested token {}", token);
        ServerStatusDataList serverStatusDataList = new ServerStatusDataList();
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            for (Record record : getMatches().listActiveServers(pilot.getPilLastIp())) {
                ServerRecord server = record.into(ServerRecord.class);
                MatchRecord match = record.into(MatchRecord.class);
                ChampionshipRecord championship = record.into(ChampionshipRecord.class);
                //Integer pilotCount = (Integer) record.getValue("PILOTS");
                //Integer activePilots = removeNullNum(server.getSrvNumPlayers().toString());
                String map = "";
                String mode = "";
                OffsetDateTime dateInit = null;
                Long botsAmount = 0L;
                String championshipName = null;
                boolean registeredForChampionship = false;
                //
                if (match.getMchId() != null) {
                    map = match.getMchMapId();
                    mode = match.getMcue4mId();
                    dateInit = match.getMchDateInit();
                    botsAmount = match.getMchBotsAmount();
                }
                if (getServers().isInChampionship(server, match)) {
                    championshipName = championship.getChaName();
                    String chaId = match.getMchChaId() != null ? match.getMchChaId() : server.getSrvChaId();
                    registeredForChampionship = getPilots().isRegisteredForChampionship(pilot, chaId);
                }
                final String srvId = server.getSrvId();
                List<String> authorisedPilots = getPilots().getAuthorisedPilotsForServer(srvId);
                //
                ServerStatusData serverStatusData = server.into(ServerStatusData.class);
                serverStatusData.setMap(map);
                serverStatusData.setMode(mode);
                serverStatusData.setSecured(StringUtils.isNotBlank(server.getSrvPassword()) || authorisedPilots.size() > 0);
                if (CollectionUtils.isNotEmpty(authorisedPilots) && authorisedPilots.contains(pilot.getPilId())) {
                    serverStatusData.setPermitted(true);
                }
                serverStatusData.setDateInit(dateInit != null ? dateInit.toString() : "");
                serverStatusData.setActivePilots(Math.max(0, server.getSrvNumPlayers()));
                serverStatusData.setBotsAmount(botsAmount);
                serverStatusData.setChampionshipName(championshipName);
                serverStatusData.setRegisteredForChampionship(registeredForChampionship);
                serverStatusDataList.getServers().put(serverStatusData.getSrvId(), serverStatusData);

                log.debug("serverStatusDataList: {}", serverStatusDataList);
            }
        } else {
            log.info("Listing servers: no pilot found with token {}", token);
        }
        return serverStatusDataList;
    }

    @ResponseBody
    @RequestMapping(value = "/{style}/set_style", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public JSONResponse setPilotStyle(@PathVariable(value = "token") String token, @PathVariable(value = "style") String style) {
        JSONResponse response = new JSONResponse();
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            if (!"none".equals(style)) {
                UiThemeRecord theme = getThemes().findById(style);
                if (theme != null) {
                    pilot.setPilUthId(style);
                    pilot.store();
                    log.info("Pilot {} set theme to {}", pilot.getPilCallsign(), theme.getUthName());
                    response.setSuccess(true);
                } else {
                    log.info("Pilot {} tried to set theme to an invalid code: {}", pilot.getPilCallsign(), style);
                }
            } else {
                log.info("Pilot {} set theme to default", pilot.getPilCallsign());
                pilot.setPilUthId(null);
                pilot.store();
                response.setSuccess(true);
            }
        } else {
            log.info("Changing style: no pilot found with token {}", token);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/set_ignore_chat", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public JSONResponse setPilotIgnoreChat(@PathVariable(value = "token") String token, @RequestParam(value = "ignore_chat") boolean ignore_chat) {
        JSONResponse response = new JSONResponse();
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            pilot.setPilDisableChat(ignore_chat);
            pilot.store();
            log.info("Pilot {} set ignore chat to {}", pilot.getPilCallsign(), ignore_chat);
            response.setSuccess(true);
        } else {
            log.info("Setting chat ignore: no pilot found with token {}", token);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/set_ignore_requests", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public JSONResponse setPilotIgnoreRequests(@PathVariable(value = "token") String token,
        @RequestParam(value = "ignore_requests") boolean ignore_requests) {
        JSONResponse response = new JSONResponse();
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            pilot.setPilDisableRequests(ignore_requests);
            pilot.store();
            log.info("Pilot {} set ignore requests to {}", pilot.getPilCallsign(), ignore_requests);
            response.setSuccess(true);
        } else {
            log.info("Setting requests ignore: no pilot found with token {}", token);
        }
        return response;
    }

    @RequestMapping(value = "/theme.css")
    @Transactional(readOnly = true)
    public ModelAndView pilotStyle(@PathVariable(value = "token") String token) {
        ModelAndView model = new ModelAndView("PilotStyles");
        PilotRecord pilot = null;
        UiThemeRecord theme;
        UiThemeAttributeRecord attribute;
        Result<Record> results = getPilots().findByTokenWithFullTheme(token);
        if (results != null && results.size() > 0) {
            Map<String, String> attributes = new HashMap<>();
            for (Record record : results) {
                if (pilot == null) {
                    pilot = record.into(PilotRecord.class);
                    theme = record.into(UiThemeRecord.class);
                    log.debug("Displaying theme for pilot {}: {}", pilot.getPilCallsign(), pilot.getPilUthId());
                    model.addObject("theme-name", theme.getUthName());
                }
                attribute = record.into(UiThemeAttributeRecord.class);
                if (attribute != null) {
                    attributes.put(attribute.getUtaUatId(), attribute.getUtaValue());
                }
            }
            if (attributes.size() > 0) {
                model.addObject("theme", attributes);
            }
        } else {
            log.info("Requesting style: no pilot found with token {}", token);
        }
        return model;
    }

    public static Integer removeNullNum(Object nullCheck) {
        if (nullCheck == null) {
            return 0;
        } else if (nullCheck.toString().isEmpty()) {
            return 0;
        } else {
            return Integer.valueOf(nullCheck.toString());
        }
    }
}
