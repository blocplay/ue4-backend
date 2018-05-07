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
import com.tokenplay.ue4.model.db.tables.pojos.ScoreConfig;
import com.tokenplay.ue4.model.db.tables.records.ScoreConfigRecord;
import com.tokenplay.ue4.model.repositories.ScoreConfigsDB;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Controller
@Transactional
@RequestMapping(value = "/srest/score_configs")
public class ScoreConfigs extends AbstractRestHandler {

    @Autowired
    DSLContext jooq;

    @Autowired
    ScoreConfigsDB scoreConfigsDB;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public List<ScoreConfig> inventoryLocations() {
        log.debug("All score configs requested");
        return scoreConfigsDB.findAll().into(ScoreConfig.class);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<ScoreConfig> viewScoreConfig(@PathVariable String id) {
        log.debug("Viewing score config {}", id);
        ScoreConfigRecord inventoryLocation = scoreConfigsDB.findById(id);
        if (inventoryLocation != null) {
            return new ResponseEntity<ScoreConfig>(inventoryLocation.into(ScoreConfig.class), HttpStatus.OK);
        } else {
            log.error("ScoreConfig not found: {}", id);
            return new ResponseEntity<ScoreConfig>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ScoreConfig> deleteScoreConfig(@PathVariable String id) {
        log.debug("Deleting score config {}", id);
        ScoreConfigRecord inventoryLocation = scoreConfigsDB.findById(id);
        if (inventoryLocation != null && inventoryLocation.delete() > 0) {
            return new ResponseEntity<ScoreConfig>(HttpStatus.OK);
        } else {
            log.error("ScoreConfig not found: {}", id);
            return new ResponseEntity<ScoreConfig>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ScoreConfig> createScoreConfig(@RequestBody ScoreConfigRecord newScoreConfig) {
        log.debug("Creating score config {}", newScoreConfig.getScoName());
        newScoreConfig.setScoId(newUUID());
        newScoreConfig.attach(jooq.configuration());
        if (newScoreConfig.store() > 0) {
            return new ResponseEntity<ScoreConfig>(newScoreConfig.into(ScoreConfig.class), HttpStatus.OK);
        } else {
            log.error("ScoreConfig missing mandatory data");
            return new ResponseEntity<ScoreConfig>(newScoreConfig.into(ScoreConfig.class), HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ScoreConfig> modifyScoreConfig(@PathVariable String id, @RequestBody ScoreConfigRecord newScoreConfig) {
        log.debug("Modifying score config {}", newScoreConfig.getScoId());
        ScoreConfigRecord inventoryLocation = scoreConfigsDB.findById(id);
        if (inventoryLocation != null) {
            inventoryLocation.from(newScoreConfig);
            if (inventoryLocation.store() > 0) {
                return new ResponseEntity<ScoreConfig>(newScoreConfig.into(ScoreConfig.class), HttpStatus.OK);
            } else {
                log.error("ScoreConfig missing mandatory data");
                return new ResponseEntity<ScoreConfig>(newScoreConfig.into(ScoreConfig.class), HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("ScoreConfig not found: {}", id);
            return new ResponseEntity<ScoreConfig>(HttpStatus.NOT_FOUND);
        }
    }
}
