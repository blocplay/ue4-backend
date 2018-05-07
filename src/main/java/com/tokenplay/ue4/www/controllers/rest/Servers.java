package com.tokenplay.ue4.www.controllers.rest;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.model.db.tables.pojos.Server;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;
import com.tokenplay.ue4.model.repositories.ServersDB;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Controller
@Transactional
@RequestMapping(value = "/srest/servers")
public class Servers extends AbstractRestHandler {

    @Autowired
    DSLContext jooq;

    @Autowired
    ServersDB serversDB;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public List<Server> servers() {
        log.debug("All servers modes requested");
        return serversDB.findAll().into(Server.class);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<Server> viewServer(@PathVariable String id) {
        log.debug("Viewing server {}", id);
        ServerRecord server = serversDB.findById(id);
        if (server != null) {
            return new ResponseEntity<Server>(server.into(Server.class), HttpStatus.OK);
        } else {
            log.error("Server not found: {}", id);
            return new ResponseEntity<Server>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Server> deleteServer(@PathVariable String id) {
        log.debug("Deleting server {}", id);
        ServerRecord server = serversDB.findById(id);
        if (server != null && server.delete() > 0) {
            return new ResponseEntity<Server>(HttpStatus.OK);
        } else {
            log.error("Server not found: {}", id);
            return new ResponseEntity<Server>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Server> createServer(@RequestBody ServerRecord newServer) {
        log.debug("Creating server {}", newServer.getSrvAlias());
        newServer.setSrvId(newUUID());
        if (newServer.getSrvDevelopment() == null) {
            newServer.setSrvDevelopment(false);
        }
        if (newServer.getSrvSanctioned() == null) {
            newServer.setSrvSanctioned(false);
        }
        if (newServer.getSrvUseCycle() == null) {
            newServer.setSrvUseCycle(false);
        }
        newServer.setSrvVisible(false);
        newServer.attach(jooq.configuration());
        if (newServer.store() > 0) {
            return new ResponseEntity<Server>(newServer.into(Server.class), HttpStatus.OK);
        } else {
            log.error("Server missing mandatory data");
            return new ResponseEntity<Server>(newServer.into(Server.class), HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Server> modifyServer(@PathVariable String id, @RequestBody ServerRecord newServer) {
        log.debug("Modifying server {}", newServer.getSrvId());
        ServerRecord server = serversDB.findById(id);
        if (server != null) {
            boolean changeChampionshipStatus = StringUtils.isBlank(server.getSrvChaId()) && StringUtils.isNotBlank(newServer.getSrvChaId());
            server.from(newServer);
            if (server.update() > 0) {
                // If we are in a championship, kick all pilots that are not registered
                // and are in the lobby.
                if (changeChampionshipStatus) {
                    serversDB.kickIdlePilots(server, "Server now in championship mode");
                }
                return new ResponseEntity<Server>(newServer.into(Server.class), HttpStatus.OK);
            } else {
                log.error("Server missing mandatory data");
                return new ResponseEntity<Server>(newServer.into(Server.class), HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("Server not found: {}", id);
            return new ResponseEntity<Server>(HttpStatus.NOT_FOUND);
        }
    }
}
