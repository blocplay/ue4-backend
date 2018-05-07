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
import com.tokenplay.ue4.model.db.tables.pojos.InventoryLocation;
import com.tokenplay.ue4.model.db.tables.records.InventoryLocationRecord;
import com.tokenplay.ue4.model.repositories.InventoryLocationsDB;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Controller
@Transactional
@RequestMapping(value = "/srest/inventory_locations")
public class InventoryLocations extends AbstractRestHandler {

    @Autowired
    DSLContext jooq;

    @Autowired
    InventoryLocationsDB inventoryLocationsDB;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryLocation> inventoryLocations() {
        log.debug("All inventory locations requested");
        return inventoryLocationsDB.findAll().into(InventoryLocation.class);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<InventoryLocation> viewInventoryLocation(@PathVariable String id) {
        log.debug("Viewing inventory location {}", id);
        InventoryLocationRecord inventoryLocation = inventoryLocationsDB.findById(id);
        if (inventoryLocation != null) {
            return new ResponseEntity<InventoryLocation>(inventoryLocation.into(InventoryLocation.class), HttpStatus.OK);
        } else {
            log.error("InventoryLocation not found: {}", id);
            return new ResponseEntity<InventoryLocation>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InventoryLocation> deleteInventoryLocation(@PathVariable String id) {
        log.debug("Deleting inventory location {}", id);
        InventoryLocationRecord inventoryLocation = inventoryLocationsDB.findById(id);
        if (inventoryLocation != null && inventoryLocation.delete() > 0) {
            return new ResponseEntity<InventoryLocation>(HttpStatus.OK);
        } else {
            log.error("InventoryLocation not found: {}", id);
            return new ResponseEntity<InventoryLocation>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InventoryLocation> createInventoryLocation(@RequestBody InventoryLocationRecord newInventoryLocation) {
        log.debug("Creating inventory location {}", newInventoryLocation.getInlName());
        newInventoryLocation.setInlId(newUUID());
        newInventoryLocation.attach(jooq.configuration());
        if (newInventoryLocation.store() > 0) {
            return new ResponseEntity<InventoryLocation>(newInventoryLocation.into(InventoryLocation.class), HttpStatus.OK);
        } else {
            log.error("InventoryLocation missing mandatory data");
            return new ResponseEntity<InventoryLocation>(newInventoryLocation.into(InventoryLocation.class), HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InventoryLocation> modifyInventoryLocation(@PathVariable String id,
        @RequestBody InventoryLocationRecord newInventoryLocation) {
        log.debug("Modifying inventory location {}", newInventoryLocation.getInlId());
        InventoryLocationRecord inventoryLocation = inventoryLocationsDB.findById(id);
        if (inventoryLocation != null) {
            inventoryLocation.from(newInventoryLocation);
            if (inventoryLocation.store() > 0) {
                return new ResponseEntity<InventoryLocation>(newInventoryLocation.into(InventoryLocation.class), HttpStatus.OK);
            } else {
                log.error("InventoryLocation missing mandatory data");
                return new ResponseEntity<InventoryLocation>(newInventoryLocation.into(InventoryLocation.class), HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("InventoryLocation not found: {}", id);
            return new ResponseEntity<InventoryLocation>(HttpStatus.NOT_FOUND);
        }
    }
}
