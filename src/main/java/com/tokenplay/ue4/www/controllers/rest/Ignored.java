package com.tokenplay.ue4.www.controllers.rest;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.Pair;
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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.logic.DataProvider;
import com.tokenplay.ue4.model.RelationshipKey;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.model.db.tables.records.RelationshipRecord;
import com.tokenplay.ue4.model.repositories.PilotsDB;
import com.tokenplay.ue4.model.repositories.RelationshipsDB;
import com.tokenplay.ue4.model.repositories.RelationshipsDB.IgnoredRecord;
import com.tokenplay.ue4.www.api.Friend;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Controller
@Transactional
@RequestMapping(value = "/gi/{token}/rest/ignored")
public class Ignored extends AbstractRestHandler {

    @Autowired
    DSLContext jooq;

    @Autowired
    RelationshipsDB relationshipsDB;

    @Autowired
    PilotsDB pilotsDB;

    @Autowired
    DataProvider dataProvider;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public Object view(@PathVariable("token") String token) {
        log.debug("Viewing ignored for token {}", token);
        Pair<PilotRecord, List<IgnoredRecord>> allRelationships = relationshipsDB.findIgnoredByPilotToken(token);
        Set<Friend> allFriends = new TreeSet<>(Friend.BY_NAME);
        if (allRelationships != null) {
            for (IgnoredRecord ignoredRecord : allRelationships.getValue()) {
                if (Friend.STATUS_IGNORED.equals(ignoredRecord.getRelationship().getRelStatus())) {
                    allFriends.add(new Friend(ignoredRecord, token));
                }
            }
        }
        return allFriends;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{callsign}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Friend> delete(@PathVariable("token") String token, @PathVariable String callsign) {
        PilotRecord pilot = pilotsDB.findByToken(token);
        PilotRecord otherPilot = pilotsDB.findByCallsign(callsign);
        if (pilot != null && otherPilot != null) {
            RelationshipKey key = new RelationshipKey(pilot.getPilId(), otherPilot.getPilId());
            RelationshipRecord relationship = relationshipsDB.findByAnyId(key);
            if (relationship != null && Friend.STATUS_IGNORED.equals(relationship.getRelStatus())) {
                relationship.delete();
                log.debug("Ignored relationship deleted  {}-{}", pilot.getPilCallsign(), otherPilot.getPilCallsign());
                return new ResponseEntity<Friend>(HttpStatus.OK);
            } else {
                log.error("Ignored relationship could not be deleted : not found {}-{}", token, callsign);
                return new ResponseEntity<Friend>(HttpStatus.NOT_FOUND);
            }
        } else {
            log.error("Ignored could not be deleted, pilot does not exist: {}", callsign);
            return new ResponseEntity<Friend>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{callsign}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Friend> create(@PathVariable("token") String token, @RequestBody Friend friend, @PathVariable String callsign) {
        PilotRecord pilot = pilotsDB.findByToken(token);
        PilotRecord otherPilot = pilotsDB.findByCallsign(callsign);
        if (pilot != null && otherPilot != null) {
            if (!pilot.equals(otherPilot)) {
                RelationshipKey key = new RelationshipKey(pilot.getPilId(), otherPilot.getPilId());
                RelationshipRecord relationship = relationshipsDB.findById(key);
                if (relationship != null) {
                    if (Friend.STATUS_IGNORED.equals(relationship.getRelStatus())) {
                        log.info("Ignored relationship is already existing {}->{}", pilot.getPilCallsign(), otherPilot.getPilCallsign());
                        return new ResponseEntity<Friend>(HttpStatus.OK);
                    } else {
                        log.info("Friendship changed from {} to ignored {}->{}", new Object[] {
                            relationship.getRelStatus(), pilot.getPilCallsign(), otherPilot.getPilCallsign()});
                        relationship.setRelLastUpdate(dataProvider.getNow());
                        relationship.setRelStatus(Friend.STATUS_IGNORED);
                        relationship.store();
                        return new ResponseEntity<Friend>(HttpStatus.OK);
                    }
                }
                key = new RelationshipKey(otherPilot.getPilId(), pilot.getPilId());
                relationship = relationshipsDB.findById(key);
                // Check if there is a pending relationship the other way, if it is,
                // accept it
                if (relationship != null && Friend.STATUS_IGNORED.equals(relationship.getRelStatus())) {
                    log.info("There was a previous friendship the other way, removing it {}->{}", pilot.getPilCallsign(), otherPilot.getPilCallsign());
                    relationship.delete();
                }
                // Create the real ignore one
                RelationshipRecord newRelationship = new RelationshipRecord();
                newRelationship.setRelPilIdSource(pilot.getPilId());
                newRelationship.setRelPilIdTarget(otherPilot.getPilId());
                newRelationship.setRelLastUpdate(dataProvider.getNow());
                newRelationship.setRelStatus(Friend.STATUS_IGNORED);
                newRelationship.attach(jooq.configuration());
                newRelationship.store();
                return new ResponseEntity<Friend>(HttpStatus.OK);
            } else {
                log.error("You are quite a fool if you need to ignore yourself!");
                return new ResponseEntity<Friend>(HttpStatus.NOT_FOUND);
            }
        } else {
            log.error("Ignored relationship could not be created, pilot does not exist: {} or {}", token, callsign);
            return new ResponseEntity<Friend>(HttpStatus.NOT_FOUND);
        }
    }
}
