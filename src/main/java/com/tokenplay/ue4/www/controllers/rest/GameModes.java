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
import com.tokenplay.ue4.model.db.tables.pojos.GameMode;
import com.tokenplay.ue4.model.db.tables.records.GameModeRecord;
import com.tokenplay.ue4.model.repositories.GameModesDB;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Controller
@Transactional
@RequestMapping(value = "/srest/gamemodes")
public class GameModes extends AbstractRestHandler {

    @Autowired
    DSLContext jooq;

    @Autowired
    GameModesDB gameModesDB;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public List<GameMode> gamemodes() {
        log.debug("All game modes requested");
        return gameModesDB.findAll().into(GameMode.class);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<GameMode> viewGameMode(@PathVariable String id) {
        log.debug("Viewing game mode {}", id);
        GameModeRecord gameMode = gameModesDB.findById(id);
        if (gameMode != null) {
            return new ResponseEntity<GameMode>(gameMode.into(GameMode.class), HttpStatus.OK);
        } else {
            log.error("Game mode not found: {}", id);
            return new ResponseEntity<GameMode>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameMode> deleteGameMode(@PathVariable String id) {
        log.debug("Deleting game mode {}", id);
        GameModeRecord gameMode = gameModesDB.findById(id);
        if (gameMode != null && gameMode.delete() > 0) {
            return new ResponseEntity<GameMode>(HttpStatus.OK);
        } else {
            log.error("Game mode not found: {}", id);
            return new ResponseEntity<GameMode>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameMode> createGameMode(@RequestBody GameModeRecord gameMode) {
        log.debug("Creating game mode {}", gameMode.getGamId());
        gameMode.attach(jooq.configuration());
        if (gameMode.store() > 0) {
            return new ResponseEntity<GameMode>(gameMode.into(GameMode.class), HttpStatus.OK);
        } else {
            log.error("Server missing mandatory data");
            return new ResponseEntity<GameMode>(gameMode.into(GameMode.class), HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameMode> modifyGameMode(@PathVariable String id, @RequestBody GameModeRecord newGameMode) {
        log.debug("Modifying game mode {}", newGameMode.getGamId());
        GameModeRecord gamemode = gameModesDB.findById(id);
        if (gamemode != null) {
            gamemode.from(newGameMode);
            if (gamemode.store() > 0) {
                return new ResponseEntity<GameMode>(newGameMode.into(GameMode.class), HttpStatus.OK);
            } else {
                log.error("Game mode missing mandatory data");
                return new ResponseEntity<GameMode>(newGameMode.into(GameMode.class), HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("Game mode not found: {}", id);
            return new ResponseEntity<GameMode>(HttpStatus.NOT_FOUND);
        }
    }
}
