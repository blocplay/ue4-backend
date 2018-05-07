package com.tokenplay.ue4.www.controllers;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Record;
import org.jooq.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.be.zeromq.ServerTasks.ServerWork;
import com.tokenplay.ue4.model.db.tables.pojos.Server;
import com.tokenplay.ue4.model.db.tables.records.ChampionshipRecord;
import com.tokenplay.ue4.model.db.tables.records.MatchRecord;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;
import com.tokenplay.ue4.www.api.BatchServersResponse;
import com.tokenplay.ue4.www.api.JSONResponse;
import com.tokenplay.ue4.www.api.ServerData;


@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Controller
@Transactional
public class WebAPI extends CommonAPI {
    @ResponseBody
    @RequestMapping(value = "/srest/servers_status", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public List<ServerData> serversStatus() {
        log.debug("Status of the servers requested");
        Result<Record> visibleMatches = getMatches().listActiveServers(null);
        List<ServerData> orderedData = new ArrayList<>(visibleMatches.size());
        for (Record record : visibleMatches) {
            //
            ServerRecord server = record.into(ServerRecord.class);
            MatchRecord match = record.into(MatchRecord.class);
            ChampionshipRecord championship = record.into(ChampionshipRecord.class);
            Long pilots = server.getSrvNumPlayers();
            Long activePilots = server.getSrvNumPlayers();
            String map = "";
            String mode = "";
            OffsetDateTime dateInit = null;
            Long botsAmount = 0L;
            String championshipName = null;
            if (match.getMchId() != null) {
                map = match.getMchMapId();
                mode = match.getMcue4mId();
                dateInit = match.getMchDateInit();
                botsAmount = match.getMchBotsAmount();
            }
            if (getServers().isInChampionship(server, match)) {
                championshipName = championship.getChaName();
            }
            orderedData.add(new ServerData(server.getSrvAlias(), server.getSrvName(), server.getSrvId(), server.getSrvDescription(), server
                .getSrvRunningVersion(), map, mode, server.getSrvStatus(), dateInit, server.getSrvMaxPlayers(), server.getSrvNumPlayers(), server
                .getSrvNumPlayers(), botsAmount, server.getSrvDevelopment(), championshipName, false));
        }
        return orderedData;
    }

    @ResponseBody
    @RequestMapping(value = "/srest/servers_status_withkeys", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Server>> listrawservers() {
        return new ResponseEntity<>(getServers().findActiveServersRaw(), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/srest/reload_cycle", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public JSONResponse reloadAllServerCycles() {
        log.debug("Map cycle reload forall servers requested");
        JSONResponse response;
        Result<ServerRecord> serversList = getServers().findActiveServers();
        List<JSONResponse> responses = new ArrayList<>(serversList.size());
        Map params = new HashMap();
        for (ServerRecord server : serversList) {
            if (server.getSrvVisible() && !server.getSrvUseCustomCycle()) {
                log.info("Sending a reload message to server {}", server.getSrvAlias());
                responses.add(performServerWork(server, params, ServerWork.SetMapCycle));
            } else {
                log.debug("Server {} is not visible or is not using global map cycle, map cycle won't be reloaded", server.getSrvAlias());
            }
        }
        response = new BatchServersResponse("ReloadMapCycle", responses);
        response.setSuccess(true);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/srest/{id}/reload_cycle", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public JSONResponse reloadServerCycle(@PathVariable String id) {
        JSONResponse response = new JSONResponse();
        ServerRecord server = getServers().findById(id);
        if (server != null) {
            log.info("Reloading map cycle for server {}", server.getSrvAlias());
            if (server.getSrvVisible()) {
                Map params = new HashMap();
                response = performServerWork(server, params, ServerWork.SetMapCycle);
                response.setSuccess(true);
            } else {
                log.error("Server {} is not visible, map cycle can't be reloaded", server.getSrvAlias());
            }
        } else {
            log.error("Reloading map cycle for server {} failed, no such server", id);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/srest/{id}/stop_map", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public JSONResponse stopMap(@PathVariable String id) {
        JSONResponse response = new JSONResponse();
        ServerRecord server = getServers().findById(id);
        if (server != null) {
            log.info("Stopping map for server {}", server.getSrvAlias());
            if (server.getSrvVisible()) {
                Map params = new HashMap();
                response = performServerWork(server, params, ServerWork.StopMap);
                response.setSuccess(true);
            } else {
                log.error("Server {} is not visible, map can't be stopped", server.getSrvAlias());
            }
        } else {
            log.error("Stopping map for server {} failed, no such server", id);
        }
        return response;
    }


}
