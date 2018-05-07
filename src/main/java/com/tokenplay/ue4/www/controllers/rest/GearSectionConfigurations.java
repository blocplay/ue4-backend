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
import com.tokenplay.ue4.model.db.tables.pojos.GearSectionConfiguration;
import com.tokenplay.ue4.model.db.tables.records.GearSectionConfigurationRecord;
import com.tokenplay.ue4.model.repositories.GearSectionConfigurationsDB;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Controller
@Transactional
@RequestMapping(value = "/srest/gearsectionconfigurations/{section}")
public class GearSectionConfigurations extends AbstractRestHandler {

    @Autowired
    DSLContext jooq;

    @Autowired
    GearSectionConfigurationsDB gearSectionConfigurationsDB;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public List<GearSectionConfiguration> all(@PathVariable String section) {
        log.debug("All gear section configurations requested from section {}");
        return gearSectionConfigurationsDB.findAll(section).into(GearSectionConfiguration.class);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<GearSectionConfiguration> view(@PathVariable String section, @PathVariable String id) {
        log.debug("Viewing gear section configuration {}:{}", id);
        GearSectionConfigurationRecord gear = gearSectionConfigurationsDB.findById(id);
        if (gear != null) {
            return new ResponseEntity<GearSectionConfiguration>(gear.into(GearSectionConfiguration.class), HttpStatus.OK);
        } else {
            log.error("Gear section configuration not found: {}", id);
            return new ResponseEntity<GearSectionConfiguration>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GearSectionConfiguration> delete(@PathVariable String section, @PathVariable String id) {
        log.debug("Deleting gear section configuration {}", id);
        GearSectionConfigurationRecord gear = gearSectionConfigurationsDB.findById(id);
        if (gear != null && gear.delete() > 0) {
            return new ResponseEntity<GearSectionConfiguration>(HttpStatus.OK);
        } else {
            log.error("Gear section configuration not found: {}", id);
            return new ResponseEntity<GearSectionConfiguration>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GearSectionConfiguration> create(@PathVariable String section, @RequestBody GearSectionConfigurationRecord newGear) {
        log.debug("Creating gear section configuration {}", newGear.getGscPropertyName());
        newGear.setGscId(newUUID());
        newGear.attach(jooq.configuration());
        if (newGear.store() > 0) {
            return new ResponseEntity<GearSectionConfiguration>(newGear.into(GearSectionConfiguration.class), HttpStatus.OK);
        } else {
            log.error("Gear section configuration missing mandatory data");
            return new ResponseEntity<GearSectionConfiguration>(newGear.into(GearSectionConfiguration.class), HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GearSectionConfiguration> modify(@PathVariable String section, @PathVariable String id,
        @RequestBody GearSectionConfigurationRecord newGear) {
        log.debug("Modifying gear section configuration {}", newGear.getGscId());
        GearSectionConfigurationRecord gear = gearSectionConfigurationsDB.findById(id);
        if (gear != null) {
            gear.from(newGear);
            if (gear.update() > 0) {
                return new ResponseEntity<GearSectionConfiguration>(newGear.into(GearSectionConfiguration.class), HttpStatus.OK);
            } else {
                log.error("Gear section configuration missing mandatory data");
                return new ResponseEntity<GearSectionConfiguration>(newGear.into(GearSectionConfiguration.class), HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("Gear section configuration not found: {}", id);
            return new ResponseEntity<GearSectionConfiguration>(HttpStatus.NOT_FOUND);
        }
    }
}
