package com.tokenplay.ue4.www.controllers.rest;

import java.util.List;

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
import com.tokenplay.ue4.model.db.tables.pojos.Map;
import com.tokenplay.ue4.model.db.tables.records.MapRecord;
import com.tokenplay.ue4.model.repositories.GameMapsDB;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Controller
@Transactional
@RequestMapping(value = "/srest/gamemaps")
public class Gamemaps extends AbstractRestHandler {

    @Autowired
    DSLContext jooq;

    @Autowired
    GameMapsDB gamemapsDB;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public List<Map> gamemaps() {
        log.debug("All game maps requested");
        return gamemapsDB.findAll().into(Map.class);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<Map> viewGameMap(@PathVariable String id) {
        log.debug("Viewing game map {}", id);
        MapRecord gameMap = gamemapsDB.findById(id);
        if (gameMap != null) {
            return new ResponseEntity<Map>(gameMap.into(Map.class), HttpStatus.OK);
        } else {
            log.error("Game mode not found: {}", id);
            return new ResponseEntity<Map>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> deleteGameMap(@PathVariable String id) {
        log.debug("Deleting game map {}", id);
        MapRecord map = gamemapsDB.findById(id);
        if (map != null && map.delete() > 0) {
            return new ResponseEntity<Map>(HttpStatus.OK);
        } else {
            log.error("Game map not found: {}", id);
            return new ResponseEntity<Map>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> createGameMap(@RequestBody MapRecord gameMap) {
        log.debug("Creating game map {}", gameMap.getMapId());
        gameMap.attach(jooq.configuration());
        if (gameMap.store() > 0) {
            return new ResponseEntity<Map>(gameMap.into(Map.class), HttpStatus.OK);
        } else {
            log.error("Server missing mandatory data");
            return new ResponseEntity<Map>(gameMap.into(Map.class), HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> modifyGameMap(@PathVariable String id, @RequestBody MapRecord newGameMap) {
        log.debug("Modifying game mode {}", newGameMap.getMapId());
        MapRecord gamemode = gamemapsDB.findById(id);
        if (gamemode != null) {
            gamemode.from(newGameMap);
            if (gamemode.store() > 0) {
                return new ResponseEntity<Map>(newGameMap.into(Map.class), HttpStatus.OK);
            } else {
                log.error("Game map missing mandatory data");
                return new ResponseEntity<Map>(newGameMap.into(Map.class), HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("Game map not found: {}", id);
            return new ResponseEntity<Map>(HttpStatus.NOT_FOUND);
        }
    }
}
