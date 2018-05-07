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
import com.tokenplay.ue4.model.db.tables.pojos.GearSection;
import com.tokenplay.ue4.model.db.tables.records.GearSectionRecord;
import com.tokenplay.ue4.model.repositories.GearSectionsDB;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Controller
@Transactional
@RequestMapping(value = "/srest/gearsections")
public class GearSections extends AbstractRestHandler {

    @Autowired
    DSLContext jooq;

    @Autowired
    GearSectionsDB gearSectionsDB;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public List<GearSection> all() {
        log.debug("All gear sections requested");
        return gearSectionsDB.findAll().into(GearSection.class);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<GearSection> view(@PathVariable String id) {
        log.debug("Viewing gear section {}", id);
        GearSectionRecord gear = gearSectionsDB.findById(id);
        if (gear != null) {
            return new ResponseEntity<GearSection>(gear.into(GearSection.class), HttpStatus.OK);
        } else {
            log.error("Gear section not found: {}", id);
            return new ResponseEntity<GearSection>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GearSection> delete(@PathVariable String id) {
        log.debug("Deleting gear section {}", id);
        GearSectionRecord gear = gearSectionsDB.findById(id);
        if (gear != null && gear.delete() > 0) {
            return new ResponseEntity<GearSection>(HttpStatus.OK);
        } else {
            log.error("Gear section not found: {}", id);
            return new ResponseEntity<GearSection>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GearSection> create(@RequestBody GearSectionRecord newGear) {
        log.debug("Creating gear section {}", newGear.getGesName());
        newGear.setGesId(newUUID());
        newGear.attach(jooq.configuration());
        if (newGear.store() > 0) {
            return new ResponseEntity<GearSection>(newGear.into(GearSection.class), HttpStatus.OK);
        } else {
            log.error("Gear section missing mandatory data");
            return new ResponseEntity<GearSection>(newGear.into(GearSection.class), HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GearSection> modify(@PathVariable String id, @RequestBody GearSectionRecord newGear) {
        log.debug("Modifying gear section {}", newGear.getGesId());
        GearSectionRecord gear = gearSectionsDB.findById(id);
        if (gear != null) {
            gear.from(newGear);
            if (gear.update() > 0) {
                return new ResponseEntity<GearSection>(newGear.into(GearSection.class), HttpStatus.OK);
            } else {
                log.error("Gear section missing mandatory data");
                return new ResponseEntity<GearSection>(newGear.into(GearSection.class), HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("Gear section not found: {}", id);
            return new ResponseEntity<GearSection>(HttpStatus.NOT_FOUND);
        }
    }
}
