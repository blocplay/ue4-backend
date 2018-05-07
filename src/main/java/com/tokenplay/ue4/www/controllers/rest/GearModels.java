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
import com.tokenplay.ue4.model.db.tables.pojos.GearModel;
import com.tokenplay.ue4.model.db.tables.records.GearModelRecord;
import com.tokenplay.ue4.model.repositories.GearModelsDB;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Controller
@Transactional
@RequestMapping(value = "/srest/gears")
public class GearModels extends AbstractRestHandler {

    @Autowired
    DSLContext jooq;

    @Autowired
    GearModelsDB gearsDB;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public List<GearModel> all() {
        log.debug("All gears requested");
        return gearsDB.findAll().into(GearModel.class);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<GearModel> view(@PathVariable String id) {
        log.debug("Viewing gear {}", id);
        GearModelRecord gear = gearsDB.findById(id);
        if (gear != null) {
            return new ResponseEntity<GearModel>(gear.into(GearModel.class), HttpStatus.OK);
        } else {
            log.error("Gear not found: {}", id);
            return new ResponseEntity<GearModel>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GearModel> delete(@PathVariable String id) {
        log.debug("Deleting gear {}", id);
        GearModelRecord gear = gearsDB.findById(id);
        if (gear != null && gear.delete() > 0) {
            return new ResponseEntity<GearModel>(HttpStatus.OK);
        } else {
            log.error("Gear not found: {}", id);
            return new ResponseEntity<GearModel>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GearModel> create(@RequestBody GearModelRecord newGear) {
        log.debug("Creating gear {}", newGear.getGemName());
        newGear.setGemId(newUUID());
        newGear.attach(jooq.configuration());
        if (newGear.store() > 0) {
            return new ResponseEntity<GearModel>(newGear.into(GearModel.class), HttpStatus.OK);
        } else {
            log.error("Gear missing mandatory data");
            return new ResponseEntity<GearModel>(newGear.into(GearModel.class), HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GearModel> modify(@PathVariable String id, @RequestBody GearModelRecord newGear) {
        log.debug("Modifying gear {}", newGear.getGemId());
        GearModelRecord gear = gearsDB.findById(id);
        if (gear != null) {
            gear.from(newGear);
            if (gear.update() > 0) {
                return new ResponseEntity<GearModel>(newGear.into(GearModel.class), HttpStatus.OK);
            } else {
                log.error("Gear missing mandatory data");
                return new ResponseEntity<GearModel>(newGear.into(GearModel.class), HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("Gear not found: {}", id);
            return new ResponseEntity<GearModel>(HttpStatus.NOT_FOUND);
        }
    }
}
