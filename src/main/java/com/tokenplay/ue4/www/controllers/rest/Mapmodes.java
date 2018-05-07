package com.tokenplay.ue4.www.controllers.rest;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.model.db.tables.pojos.Mapmode;
import com.tokenplay.ue4.model.db.tables.records.GameModeRecord;
import com.tokenplay.ue4.model.db.tables.records.MapRecord;
import com.tokenplay.ue4.model.db.tables.records.MapmodeRecord;
import com.tokenplay.ue4.model.repositories.GameMapsDB;
import com.tokenplay.ue4.model.repositories.GameModesDB;
import com.tokenplay.ue4.model.repositories.MapmodesDB;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Controller
@Transactional
@RequestMapping(value = "/srest/mapmodes")
public class Mapmodes extends AbstractRestHandler {

    @Autowired
    DSLContext jooq;

    @Autowired
    MapmodesDB mapmodesDB;

    @Autowired
    GameMapsDB gamemapsDB;

    @Autowired
    GameModesDB gamemodesDB;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public Object viewMapMode() {
        log.debug("Vieing all map modes");
        return mapmodesDB.findAll().into(Mapmode.class);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mapmode> deleteMapMode(@RequestParam String mamId) {
        log.debug("Deleting map mode {}", new Object[] {mamId});
        MapmodeRecord mapmode = mapmodesDB.findById(null, mamId);
        if (mapmode != null && mapmode.delete() > 0) {
            return new ResponseEntity<Mapmode>(HttpStatus.OK);
        } else {
            log.error("Game mode not found: {}", new Object[] {mamId});
            return new ResponseEntity<Mapmode>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mapmode> createMapMode(@RequestBody MapmodeRecord mapMode) {
        log.debug("Creating map mode {}", mapMode);
        MapRecord theMap = gamemapsDB.findById(mapMode.getMamMapId());
        GameModeRecord theMode = gamemodesDB.findById(mapMode.getMamGamId());
        if (theMap != null && theMode != null) {
            if (StringUtils.isAnyBlank(theMap.getMapAssetName(), theMode.getGamAssetName()) || mapMode.getMamEnabled() == null) {
                mapMode.setMamEnabled(false);
            }
            if (mapMode.getMamAiEnabled() == null) {
                mapMode.setMamAiEnabled(false);
            }
            mapMode.setMamId(newUUID());
            mapMode.attach(jooq.configuration());
            if (mapMode.store() > 0) {
                return new ResponseEntity<Mapmode>(mapMode.into(Mapmode.class), HttpStatus.OK);
            } else {
                log.error("Server missing mandatory data");
                return new ResponseEntity<Mapmode>(mapMode.into(Mapmode.class), HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("Game mode created from a map or mode that does not exist");
            return new ResponseEntity<Mapmode>(mapMode.into(Mapmode.class), HttpStatus.BAD_REQUEST);
        }

    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mapmode> modifyMapMode(@RequestParam String mamId, @RequestBody MapmodeRecord newMapMode) {
        log.debug("Modifying map mode {}", new Object[] {mamId});
        Record record = mapmodesDB.withMapAndMode(null, mamId);
        if (record != null) {
            MapmodeRecord mapMode = record.into(MapmodeRecord.class);
            MapRecord map = record.into(MapRecord.class);
            GameModeRecord mode = record.into(GameModeRecord.class);
            if (newMapMode != null) {
                if (StringUtils.isAnyBlank(map.getMapAssetName(), mode.getGamAssetName())) {
                    mapMode.setMamEnabled(false);
                } else {
                    mapMode.setMamEnabled(newMapMode.getMamEnabled());
                }
                mapMode.setMamMatchTime(newMapMode.getMamMatchTime());
                mapMode.store();
                return new ResponseEntity<Mapmode>(mapMode.into(Mapmode.class), HttpStatus.OK);
            } else {
                log.error("Game mode missing mandatory data");
                return new ResponseEntity<Mapmode>(mapMode.into(Mapmode.class), HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("Game mode not found: {}", new Object[] {mamId});
            return new ResponseEntity<Mapmode>(HttpStatus.NOT_FOUND);
        }
    }
}
