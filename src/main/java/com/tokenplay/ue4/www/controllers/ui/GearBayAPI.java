package com.tokenplay.ue4.www.controllers.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.tools.json.JSONObject;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.model.db.tables.pojos.GearSection;
import com.tokenplay.ue4.model.db.tables.pojos.InventoryLocation;
import com.tokenplay.ue4.model.db.tables.pojos.InventoryObject;
import com.tokenplay.ue4.model.db.tables.pojos.PaintScheme;
import com.tokenplay.ue4.model.db.tables.records.GearInstanceRecord;
import com.tokenplay.ue4.model.db.tables.records.GearModelRecord;
import com.tokenplay.ue4.model.db.tables.records.PaintSchemeRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.steam.client.types.api.GearInstanceResponse;
import com.tokenplay.ue4.steam.client.types.api.GearInstancesResponse;
import com.tokenplay.ue4.www.api.JSONResponse;
import com.tokenplay.ue4.www.controllers.CommonAPI;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Controller
@Transactional
@RequestMapping(value = "/gi/{token}")
public class GearBayAPI extends CommonAPI {
    @Autowired
    public GearBayAPI(DSLContext jooq) {
        setJooq(jooq);
    }

    @ResponseBody
    @RequestMapping(value = "/{gear}/set_scheme", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public JSONResponse setPilotGearScheme(@PathVariable(value = "token") String token, @PathVariable(value = "gear") String gear, @RequestParam(
        value = "usecustomscheme") boolean useCustomScheme, @RequestParam(value = "default_scheme") String defaultScheme) {
        JSONResponse response = new JSONResponse();
        GearInstanceRecord gearInstance = getGearInstancesDB().findByIdAndPilotToken(gear, token);
        if (gearInstance != null) {
            gearInstance.setGeiDefaultScheme(defaultScheme);
            gearInstance.setGeiUseCustomScheme(useCustomScheme);
            gearInstance.store();
            log.debug("Gear {} set scheme to {}/{}", new Object[] {
                gearInstance.getGeiName(), defaultScheme, useCustomScheme});
            response.setSuccess(true);
        } else {
            log.info("Setting scheme: no paint scheme found with id {} for pilot with token {}", gear, token);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/set_default_scheme", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public JSONResponse setPilotDefaultScheme(@PathVariable(value = "token") String token,
        @RequestParam(value = "usecustomscheme") boolean useCustomScheme, @RequestParam(value = "default_scheme") String defaultScheme) {
        JSONResponse response = new JSONResponse();
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            pilot.setPilDefaultScheme(defaultScheme);
            pilot.setPilUseCustomScheme(useCustomScheme);
            pilot.store();
            log.debug("Pilot {} set default scheme to {}/{}", new Object[] {
                pilot.getPilCallsign(), defaultScheme, useCustomScheme});
            response.setSuccess(true);
        } else {
            log.info("Setting default scheme: no pilot found with token {}", token);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/export_scheme", produces = MediaType.TEXT_PLAIN_VALUE)
    @Transactional
    public ResponseEntity<String> exportScheme(@PathVariable(value = "token") String token, @RequestParam(value = "scheme") String scheme) {
        try {
            return new ResponseEntity<String>(toShortened(scheme), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/import_scheme", produces = MediaType.TEXT_PLAIN_VALUE)
    @Transactional
    public ResponseEntity<String> importScheme(@PathVariable(value = "token") String token, @RequestParam(value = "encoded") String encoded) {
        try {
            return new ResponseEntity<String>(fromShortened(encoded), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/rest/gearmodels", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject gearmodels(@PathVariable(value = "token") String token) {
        Result<GearModelRecord> gModel = null;

        JSONObject response = new JSONObject();
        JSONObject jGearCollection = new JSONObject();

        PilotRecord pilot = getPilots().findByToken(token);
        log.debug("All gear models requested for token: {}, pilot: {}", token, pilot);
        if (pilot != null) {
            //System.out.println(pilot.toString());
            gModel = getGearModelsDB().findAll();

            for (GearModelRecord g : gModel) {
                JSONObject jGearModel = new JSONObject();
                String getGemName = removeNull(g.getGemName());
                String getGemId = removeNull(g.getGemId());
                String getGemGesEngine = removeNull(g.getGemGesEngine());
                String getGemGesFootLeft = removeNull(g.getGemGesFootLeft());
                String getGemGesFootRight = removeNull(g.getGemGesFootRight());
                String getGemGesFuelTank = removeNull(g.getGemGesFuelTank());
                String getGemGesHandLeft = removeNull(g.getGemGesHandLeft());
                String getGemGesHandRight = removeNull(g.getGemGesHandRight());
                String getGemGesHead = removeNull(g.getGemGesHead());
                String getGemGesHip = removeNull(g.getGemGesHip());
                String getGemGesLowerArmLeft = removeNull(g.getGemGesLowerArmLeft());
                String getGemGesLowerArmRight = removeNull(g.getGemGesLowerArmRight());
                String getGemGesLowerLegLeft = removeNull(g.getGemGesLowerLegLeft());
                String getGemGesLowerLegRight = removeNull(g.getGemGesLowerLegRight());
                String getGemGesPylonLeft = removeNull(g.getGemGesPylonLeft());
                String getGemGesPylonRight = removeNull(g.getGemGesPylonRight());
                String getGemGesShoulderLeft = removeNull(g.getGemGesShoulderLeft());
                String getGemGesShoulderRight = removeNull(g.getGemGesShoulderRight());
                String getGemGesTorso = removeNull(g.getGemGesTorso());
                String getGemGesUpperArmLeft = removeNull(g.getGemGesUpperArmLeft());
                String getGemGesUpperArmRight = removeNull(g.getGemGesUpperArmRight());
                String getGemGesUpperLegLeft = removeNull(g.getGemGesUpperLegLeft());
                String getGemGesUpperLegRight = removeNull(g.getGemGesUpperLegRight());
                String getGemGesEngineHeavyMountLeft = removeNull(g.getGemGesEngineHeavyMountLeft());
                String getGemGesEngineHeavyMountRight = removeNull(g.getGemGesEngineHeavyMountRight());
                String getGemGesFuelTankStorage = removeNull(g.getGemGesFuelTankStorage());
                String getGemGesLeftCollarMount = removeNull(g.getGemGesLeftCollarMount());
                String getGemGesRightCollarMount = removeNull(g.getGemGesRightCollarMount());
                String getGemGesUpperLegOutLeft = removeNull(g.getGemGesUpperLegOutLeft());
                String getGemGesUpperLegOutRight = removeNull(g.getGemGesUpperLegOutRight());

                jGearModel.put("Name", getGemName.toString());
                jGearModel.put("ICID", getGemId);
                jGearModel.put("SENG", getGemGesEngine);
                jGearModel.put("SFL", getGemGesFootLeft);
                jGearModel.put("SFR", getGemGesFootRight);
                jGearModel.put("SEFT", getGemGesFuelTank);
                jGearModel.put("SHL", getGemGesHandLeft);
                jGearModel.put("SHR", getGemGesHandRight);
                jGearModel.put("SHED", getGemGesHead);
                jGearModel.put("SHIP", getGemGesHip);
                jGearModel.put("SADL", getGemGesLowerArmLeft);
                jGearModel.put("SADR", getGemGesLowerArmRight);
                jGearModel.put("SLDL", getGemGesLowerLegLeft);
                jGearModel.put("SLDR", getGemGesLowerLegRight);
                jGearModel.put("SEPL", getGemGesPylonLeft);
                jGearModel.put("SEPR", getGemGesPylonRight);
                jGearModel.put("SSHL", getGemGesShoulderLeft);
                jGearModel.put("SSHR", getGemGesShoulderRight);
                jGearModel.put("STOR", getGemGesTorso);
                jGearModel.put("SAUL", getGemGesUpperArmLeft);
                jGearModel.put("SAUR", getGemGesUpperArmRight);
                jGearModel.put("SLUL", getGemGesUpperLegLeft);
                jGearModel.put("SLUR", getGemGesUpperLegRight);
                jGearModel.put("MEHL", getGemGesEngineHeavyMountLeft);
                jGearModel.put("MEHR", getGemGesEngineHeavyMountRight);
                jGearModel.put("MFT", getGemGesFuelTankStorage);
                jGearModel.put("MLC", getGemGesLeftCollarMount);
                jGearModel.put("MRC", getGemGesRightCollarMount);
                jGearModel.put("MULOL", getGemGesUpperLegOutLeft);
                jGearModel.put("MULOR", getGemGesUpperLegOutRight);


                jGearCollection.put(getGemId, jGearModel);
            }
        }

        response.put("Models", jGearCollection);

        return response;
    }


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/rest/gearmodelids", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject gearmodelsicids(@PathVariable(value = "token") String token) {
        Result<GearModelRecord> gModel = null;

        JSONObject response = new JSONObject();
        JSONObject jGearCollection = new JSONObject();

        log.debug("All gear instances requested");
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            //System.out.println(pilot.toString());
            gModel = getGearModelsDB().findAll();

            for (GearModelRecord g : gModel) {
                JSONObject jGearModel = new JSONObject();
                String getGemName = removeNull(g.getGemName());
                String getGemId = removeNull(g.getGemId());

                jGearModel.put("Name", getGemName.toString());
                jGearModel.put("ICID", getGemId);
                jGearCollection.put(getGemId, jGearModel);
            }
        }

        response.put("ICIDs", jGearCollection);

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/rest/gearsections", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<GearSection>> gearsections(@PathVariable(value = "token") String token) {
        return new ResponseEntity<List<GearSection>>(getGearSectionsDB().findAll().into(GearSection.class), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/rest/inventory_locations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<InventoryLocation>> inventoryLocations(@PathVariable(value = "token") String token) {
        return new ResponseEntity<List<InventoryLocation>>(getInventoryLocationsDB().findAll().into(InventoryLocation.class), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/rest/inventory_objects", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<InventoryObject>> inventoryObjects(@PathVariable(value = "token") String token) {
        return new ResponseEntity<List<InventoryObject>>(getInventoryObjectsDB().findAll().into(InventoryObject.class), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/rest/gearinstances", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public GearInstancesResponse gearinstances(@PathVariable(value = "token") String token) {
        log.debug("All gear instances requested for token {}", token);
        GearInstancesResponse gearInstancesResponse = new GearInstancesResponse();
        List<Pair<GearInstanceRecord, GearModelRecord>> gInstance = getGearInstancesDB().findByPilotToken(token);
        if (gInstance != null) {
            if (gInstance.size() < 1) {
                PilotRecord pilot = getPilots().findByToken(token);
                log.error("Pilot has no gears: {}/{}/{}", pilot.getPilUsuId(), pilot.getPilId(), pilot.getPilCallsign());
            }
            gearInstancesResponse.setInstancesList(gInstance.stream().filter(pair -> Boolean.TRUE.equals(pair.getRight().getGemSelectable()))
                .map(GearInstanceResponse::of).collect(Collectors.toMap(GearInstanceResponse::getGeiId, Function.identity())));
        }
        //log.info("All gear instances: {}", gearInstancesResponse);
        return gearInstancesResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/rest/gearinstances/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, GearInstanceResponse> viewGearInstance(@PathVariable String id, @PathVariable(value = "token") String token) {
        Map<String, GearInstanceResponse> gearInstancesResponse = new HashMap<>();
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {

            //System.out.println(pilot.toString());
            Pair<GearInstanceRecord, GearModelRecord> g = getGearInstancesDB().findById(id);

            if (g != null && Boolean.TRUE.equals(g.getRight().getGemSelectable())) {
                GearInstanceResponse gearInstanceResponse = GearInstanceResponse.of(g);
                gearInstancesResponse.put(gearInstanceResponse.getGeiId(), gearInstanceResponse);
            }
        }
        //log.info("Gear instance: {}", gearInstancesResponse);
        return gearInstancesResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/rest/gearinstances/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteGearInstance(@PathVariable String id) {
        Pair<GearInstanceRecord, GearModelRecord> g = getGearInstancesDB().findById(id);
        if (g != null) {
            g.getLeft().delete();
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<Boolean>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/rest/gearinstances/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> modifyGearInstance(@PathVariable String id, @RequestBody String gearspec) {
        Pair<GearInstanceRecord, GearModelRecord> g = getGearInstancesDB().findById(id);
        if (g != null) {
            GearInstanceRecord record = g.getLeft();
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> map = new HashMap<String, Object>();
                map = mapper.readValue(gearspec, new TypeReference<Map<String, String>>() {});

                //System.out.println(map);

                record.setGeiGesEngine(removeNull(map.get("SENG")));
                record.setGeiGesFootLeft(removeNull(map.get("SFL")));
                record.setGeiGesFootRight(removeNull(map.get("SFR")));
                record.setGeiGesFuelTank(removeNull(map.get("SEFT")));
                record.setGeiGesHandLeft(removeNull(map.get("SHL")));
                record.setGeiGesHandRight(removeNull(map.get("SHR")));
                record.setGeiGesHead(removeNull(map.get("SHED")));
                record.setGeiGesHip(removeNull(map.get("SHIP")));
                record.setGeiGesLowerArmLeft(removeNull(map.get("SADL")));
                record.setGeiGesLowerArmRight(removeNull(map.get("SADR")));
                record.setGeiGesLowerLegLeft(removeNull(map.get("SLDL")));
                record.setGeiGesLowerLegRight(removeNull(map.get("SLDR")));
                record.setGeiGesPylonLeft(removeNull(map.get("SEPL")));
                record.setGeiGesPylonRight(removeNull(map.get("SEPR")));
                record.setGeiGesShoulderLeft(removeNull(map.get("SSHL")));
                record.setGeiGesShoulderRight(removeNull(map.get("SSHR")));
                record.setGeiGesTorso(removeNull(map.get("STOR")));
                record.setGeiGesUpperArmLeft(removeNull(map.get("SAUL")));
                record.setGeiGesUpperArmRight(removeNull(map.get("SAUR")));
                record.setGeiGesUpperLegLeft(removeNull(map.get("SLUL")));
                record.setGeiGesUpperLegRight(removeNull(map.get("SLUR")));
                record.setIiGesEngineHeavyMountLeft(removeNull(map.get("MEHL")));
                record.setIiGesEngineHeavyMountRight(removeNull(map.get("MEHR")));
                record.setIiGesFuelTankStorage(removeNull(map.get("MFT")));
                record.setIiGesRightCollarMount(removeNull(map.get("MRC")));
                record.setIiGesLeftCollarMount(removeNull(map.get("MLC")));
                record.setIiGesUpperLegOutLeft(removeNull(map.get("MULOL")));
                record.setIiGesUpperLegOutRight(removeNull(map.get("MULOR")));
                record.setGeiDefaultScheme(removeNull(map.get("PSD")));
                record.update();

                return new ResponseEntity<Boolean>(true, HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace(System.out);
                return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<Boolean>(false, HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/rest/gearinstances", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createGearInstance(@PathVariable(value = "token") String token, @RequestBody String gearspec) {
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            if (gearspec != null) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> map = new HashMap<String, Object>();
                    map = mapper.readValue(gearspec, new TypeReference<Map<String, String>>() {});

                    //System.out.println(map);

                    GearInstanceRecord record = new GearInstanceRecord();
                    //record.from(map);

                    String geiUUID = newUUID();

                    record.setGeiId(geiUUID);
                    record.setGeiPilId(pilot.getPilId());
                    record.setGeiGemId(removeNull(map.get("DCID")));
                    record.setGeiName(removeNull(map.get("Name")));
                    record.setGeiDefaultScheme(removeNull(map.get("PSD")));
                    record.setGeiGesEngine(removeNull(map.get("SENG")));
                    record.setGeiGesFootLeft(removeNull(map.get("SFR")));
                    record.setGeiGesFootRight(removeNull(map.get("SFR")));
                    record.setGeiGesFuelTank(removeNull(map.get("SEFT")));
                    record.setGeiGesHandLeft(removeNull(map.get("SHR")));
                    record.setGeiGesHandRight(removeNull(map.get("SHR")));
                    record.setGeiGesHead(removeNull(map.get("SHED")));
                    record.setGeiGesHip(removeNull(map.get("SHIP")));
                    record.setGeiGesLowerArmLeft(removeNull(map.get("SADL")));
                    record.setGeiGesLowerArmRight(removeNull(map.get("SADR")));
                    record.setGeiGesLowerLegLeft(removeNull(map.get("SLDL")));
                    record.setGeiGesLowerLegRight(removeNull(map.get("SLDR")));
                    record.setGeiGesPylonLeft(removeNull(map.get("SEPL")));
                    record.setGeiGesPylonRight(removeNull(map.get("SEPR")));
                    record.setGeiGesShoulderLeft(removeNull(map.get("SSHL")));
                    record.setGeiGesShoulderRight(removeNull(map.get("SSHR")));
                    record.setGeiGesTorso(removeNull(map.get("STOR")));
                    record.setGeiGesUpperArmLeft(removeNull(map.get("SAUL")));
                    record.setGeiGesUpperArmRight(removeNull(map.get("SAUR")));
                    record.setGeiGesUpperLegLeft(removeNull(map.get("SLUL")));
                    record.setGeiGesUpperLegRight(removeNull(map.get("SLUR")));
                    record.setIiGesEngineHeavyMountLeft(removeNull(map.get("MEHL")));
                    record.setIiGesEngineHeavyMountRight(removeNull(map.get("MEHR")));
                    record.setIiGesFuelTankStorage(removeNull(map.get("MFT")));
                    record.setIiGesRightCollarMount(removeNull(map.get("MRC")));
                    record.setIiGesLeftCollarMount(removeNull(map.get("MLC")));
                    record.setIiGesUpperLegOutLeft(removeNull(map.get("MULOL")));
                    record.setIiGesUpperLegOutRight(removeNull(map.get("MULOR")));
                    record.setGeiDefaultScheme(removeNull(map.get("PSD")));
                    record.attach(getJooq().configuration());
                    record.store();

                    //System.out.println(record);
                    return new ResponseEntity<String>(geiUUID, HttpStatus.OK);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                    return new ResponseEntity<String>(e.toString(), HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<String>("Gear Instance not provided!", HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("Pilot not found with that token: {}", token);
            return new ResponseEntity<String>("Pilot not found with that token", HttpStatus.NOT_FOUND);
        }
    }

    /************************************************************************************************************************/

    /************************************************************************************************************************/
    /************************************************************************************************************************/

    @ResponseBody
    @RequestMapping(value = "/rest/paintschemes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<PaintScheme>> paintschemes(@PathVariable(value = "token") String token) {
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            return new ResponseEntity<List<PaintScheme>>(getPaintSchemesDB().findAll(pilot), HttpStatus.OK);
        } else {
            log.error("Pilot not found with that token: {}", token);
            return new ResponseEntity<List<PaintScheme>>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/rest/paintschemes/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaintScheme> viewPaintScheme(@PathVariable String id) {
        PaintSchemeRecord instance = getPaintSchemesDB().findById(id);
        if (instance != null) {
            return new ResponseEntity<PaintScheme>(instance.into(PaintScheme.class), HttpStatus.OK);
        } else {
            return new ResponseEntity<PaintScheme>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/rest/paintschemes/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deletePaintScheme(@PathVariable String id) {
        PaintSchemeRecord instance = getPaintSchemesDB().findById(id);
        if (instance != null) {
            instance.delete();
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<Boolean>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/rest/paintschemes/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> modifyPaintScheme(@PathVariable String id, @RequestBody PaintSchemeRecord paintScheme) {
        log.debug("Updating paint scheme {}", paintScheme.getPisName());
        PaintSchemeRecord instance = getPaintSchemesDB().findById(id);
        if (instance != null) {
            instance.from(paintScheme);
            instance.update();
            return new ResponseEntity<Boolean>(HttpStatus.OK);
        } else {
            return new ResponseEntity<Boolean>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/rest/paintschemes", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaintScheme> createPaintScheme(@PathVariable(value = "token") String token, @RequestBody PaintScheme paintScheme) {
        log.debug("Creating paint scheme {}", paintScheme.getPisName());
        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            PaintSchemeRecord record = new PaintSchemeRecord();
            record.from(paintScheme);
            record.setPisId(newUUID());
            record.setPisPilId(pilot.getPilId());
            record.attach(getJooq().configuration());
            record.store();
            return new ResponseEntity<PaintScheme>(record.into(PaintScheme.class), HttpStatus.OK);
        } else {
            log.error("Pilot not found with that token: {}", token);
            return new ResponseEntity<PaintScheme>(HttpStatus.NOT_FOUND);
        }
    }

    public static String fromShortened(String in) throws Exception {
        ByteArrayOutputStream theBOS = new ByteArrayOutputStream();
        try (ByteArrayInputStream theBIS = new ByteArrayInputStream(in.getBytes("UTF-8"));
            Base64InputStream b64is = new Base64InputStream(theBIS);
            InflaterInputStream inflater = new InflaterInputStream(b64is)) {
            byte[] buf = new byte[4096];
            int count = -1;
            while ((count = inflater.read(buf)) > 0) {
                theBOS.write(buf, 0, count);
            }
        }
        theBOS.flush();
        String longIn =
            theBOS.toString("UTF-8").replaceAll("-1-", "layerRepeatRate").replaceAll("-2-", "bUseLayer").replaceAll("-3-", "layerBase")
                .replaceAll("-4-", "layer1").replaceAll("-5-", "layer2").replaceAll("-6-", "materialName").replaceAll("-7-", "layerPrimaryColor")
                .replaceAll("-8-", "layerSecondaryColor");
        return longIn;
    }

    public static String toShortened(String in) throws Exception {
        String shortIn =
            in.replaceAll("layerRepeatRate", "-1-").replaceAll("bUseLayer", "-2-").replaceAll("layerBase", "-3-").replaceAll("layer1", "-4-")
                .replaceAll("layer2", "-5-").replaceAll("materialName", "-6-").replaceAll("layerPrimaryColor", "-7-")
                .replaceAll("layerSecondaryColor", "-8-");
        ByteArrayOutputStream theBOS = new ByteArrayOutputStream();
        try (Base64OutputStream b64os = new Base64OutputStream(theBOS); DeflaterOutputStream deflater = new DeflaterOutputStream(b64os)) {
            deflater.write(shortIn.getBytes("UTF-8"));
        }
        theBOS.flush();
        return theBOS.toString("UTF-8");
    }

    public static String removeNull(Object nullCheck) {
        if (nullCheck == null) {
            return "";
        } else {
            return nullCheck.toString();
        }
    }
}
