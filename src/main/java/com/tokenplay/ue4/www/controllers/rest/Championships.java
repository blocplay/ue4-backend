package com.tokenplay.ue4.www.controllers.rest;

import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
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
import com.tokenplay.ue4.model.db.tables.pojos.Championship;
import com.tokenplay.ue4.model.db.tables.pojos.Pilot;
import com.tokenplay.ue4.model.db.tables.records.ChampionshipRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.model.repositories.ChampionshipsDB;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Controller
@Transactional
@RequestMapping(value = "/srest/championships")
public class Championships extends AbstractRestHandler {

    @Autowired
    DSLContext jooq;

    @Autowired
    ChampionshipsDB championshipsDB;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public List<Championship> championships() {
        log.debug("All championships requested");
        return championshipsDB.findAll().into(Championship.class);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<Championship> viewChampionship(@PathVariable String id) {
        log.debug("Viewing championship {}", id);
        ChampionshipRecord championship = championshipsDB.findById(id);
        if (championship != null) {
            return new ResponseEntity<Championship>(championship.into(Championship.class), HttpStatus.OK);
        } else {
            log.error("Championship not found: {}", id);
            return new ResponseEntity<Championship>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/{id}/pilots", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<List<Pilot>> viewChampionshipPilots(@PathVariable String id) {
        log.debug("Viewing championship {}", id);
        ChampionshipRecord championship = championshipsDB.findById(id);
        if (championship != null) {
            Result<Record> records = championshipsDB.getPilots(championship);
            List<Pilot> pilots = new ArrayList<>(records.size());
            for (Record record : records) {
                PilotRecord pilot = record.into(PilotRecord.class);
                pilots.add(pilot.into(Pilot.class));
            }
            return new ResponseEntity<List<Pilot>>(pilots, HttpStatus.OK);
        } else {
            log.error("Championship not found: {}", id);
            return new ResponseEntity<List<Pilot>>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Championship> deleteChampionship(@PathVariable String id) {
        log.debug("Deleting championship {}", id);
        ChampionshipRecord championship = championshipsDB.findById(id);
        if (championship != null && championship.delete() > 0) {
            return new ResponseEntity<Championship>(HttpStatus.OK);
        } else {
            log.error("Championship not found: {}", id);
            return new ResponseEntity<Championship>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Championship> createChampionship(@RequestBody ChampionshipRecord newChampionship) {
        log.debug("Creating championship {}", newChampionship.getChaName());
        newChampionship.setChaId(newUUID());
        newChampionship.attach(jooq.configuration());
        if (newChampionship.store() > 0) {
            return new ResponseEntity<Championship>(newChampionship.into(Championship.class), HttpStatus.OK);
        } else {
            log.error("Championship missing mandatory data");
            return new ResponseEntity<Championship>(newChampionship.into(Championship.class), HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Championship> modifyChampionship(@PathVariable String id, @RequestBody ChampionshipRecord newChampionship) {
        log.debug("Modifying championship {}", newChampionship.getChaId());
        ChampionshipRecord championship = championshipsDB.findById(id);
        if (championship != null) {
            championship.from(newChampionship);
            if (championship.store() > 0) {
                return new ResponseEntity<Championship>(newChampionship.into(Championship.class), HttpStatus.OK);
            } else {
                log.error("Championship missing mandatory data");
                return new ResponseEntity<Championship>(newChampionship.into(Championship.class), HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("Championship not found: {}", id);
            return new ResponseEntity<Championship>(HttpStatus.NOT_FOUND);
        }
    }
}
