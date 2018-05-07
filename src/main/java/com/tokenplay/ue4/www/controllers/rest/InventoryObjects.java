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
import com.tokenplay.ue4.model.db.tables.pojos.InventoryObject;
import com.tokenplay.ue4.model.db.tables.records.InventoryObjectRecord;
import com.tokenplay.ue4.model.repositories.InventoryObjectsDB;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Controller
@Transactional
@RequestMapping(value = "/srest/inventory_objects")
public class InventoryObjects extends AbstractRestHandler {

    @Autowired
    DSLContext jooq;

    @Autowired
    InventoryObjectsDB inventoryObjectsDB;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryObject> inventoryObjects() {
        log.debug("All inventory objects requested");
        return inventoryObjectsDB.findAll().into(InventoryObject.class);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<InventoryObject> viewInventoryObject(@PathVariable String id) {
        log.debug("Viewing inventory object {}", id);
        InventoryObjectRecord inventoryObject = inventoryObjectsDB.findById(id);
        if (inventoryObject != null) {
            return new ResponseEntity<InventoryObject>(inventoryObject.into(InventoryObject.class), HttpStatus.OK);
        } else {
            log.error("InventoryObject not found: {}", id);
            return new ResponseEntity<InventoryObject>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InventoryObject> deleteInventoryObject(@PathVariable String id) {
        log.debug("Deleting inventory object {}", id);
        InventoryObjectRecord inventoryObject = inventoryObjectsDB.findById(id);
        if (inventoryObject != null && inventoryObject.delete() > 0) {
            return new ResponseEntity<InventoryObject>(HttpStatus.OK);
        } else {
            log.error("InventoryObject not found: {}", id);
            return new ResponseEntity<InventoryObject>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InventoryObject> createInventoryObject(@RequestBody InventoryObjectRecord newInventoryObject) {
        log.debug("Creating inventory object {}", newInventoryObject.getInoName());
        newInventoryObject.setInoId(newUUID());
        newInventoryObject.attach(jooq.configuration());
        if (newInventoryObject.store() > 0) {
            return new ResponseEntity<InventoryObject>(newInventoryObject.into(InventoryObject.class), HttpStatus.OK);
        } else {
            log.error("InventoryObject missing mandatory data");
            return new ResponseEntity<InventoryObject>(newInventoryObject.into(InventoryObject.class), HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InventoryObject> modifyInventoryObject(@PathVariable String id, @RequestBody InventoryObjectRecord newInventoryObject) {
        log.debug("Modifying inventory object {}", newInventoryObject.getInoId());
        InventoryObjectRecord inventoryObject = inventoryObjectsDB.findById(id);
        if (inventoryObject != null) {
            inventoryObject.from(newInventoryObject);
            if (inventoryObject.store() > 0) {
                return new ResponseEntity<InventoryObject>(newInventoryObject.into(InventoryObject.class), HttpStatus.OK);
            } else {
                log.error("InventoryObject missing mandatory data");
                return new ResponseEntity<InventoryObject>(newInventoryObject.into(InventoryObject.class), HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("InventoryObject not found: {}", id);
            return new ResponseEntity<InventoryObject>(HttpStatus.NOT_FOUND);
        }
    }
}
