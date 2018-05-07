package com.tokenplay.ue4.www.controllers.rest;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.model.db.tables.pojos.EventScore;
import com.tokenplay.ue4.model.db.tables.records.EventScoreRecord;
import com.tokenplay.ue4.model.db.tables.records.ScoreConfigRecord;
import com.tokenplay.ue4.model.repositories.EventScoresDB;
import com.tokenplay.ue4.model.repositories.ScoreConfigsDB;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Controller
@Transactional
@RequestMapping(value = "/srest/event_scores/{scoId}")
public class EventScores extends AbstractRestHandler {

    @Autowired
    DSLContext jooq;

    @Autowired
    ScoreConfigsDB scoreConfigsDB;

    @Autowired
    EventScoresDB eventScoresDB;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public Object viewEventScore(@PathVariable String scoId) {
        log.debug("Vieing all event scores from {}", scoId);
        return eventScoresDB.findAll(scoId);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventScore> deleteEventScore(@PathVariable String scoId, @RequestParam String escId) {
        log.debug("Deleting event score {}-{}", new Object[] {
            scoId, escId});
        EventScoreRecord eventScore = eventScoresDB.findById(escId);
        if (eventScore != null && eventScore.delete() > 0) {
            return new ResponseEntity<EventScore>(HttpStatus.OK);
        } else {
            log.error("Event score not found: {}-{}", new Object[] {
                scoId, escId});
            return new ResponseEntity<EventScore>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventScore> createEventScore(@PathVariable String scoId, @RequestBody EventScoreRecord eventScore) {
        log.debug("Creating event score {}-{}", scoId, eventScore);
        ScoreConfigRecord scoreConfig = scoreConfigsDB.findById(scoId);
        if (scoreConfig != null) {
            eventScore.setEscScoId(scoId);
            eventScore.setEscId(newUUID());
            eventScore.attach(jooq.configuration());
            if (eventScore.store() > 0) {
                return new ResponseEntity<EventScore>(eventScore.into(EventScore.class), HttpStatus.OK);
            } else {
                log.error("Server missing mandatory data");
                return new ResponseEntity<EventScore>(eventScore.into(EventScore.class), HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("Game mode created in a score configuration that does not exist");
            return new ResponseEntity<EventScore>(eventScore.into(EventScore.class), HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventScore> modifyEventScore(@PathVariable String scoId, @RequestParam String escId,
        @RequestBody EventScoreRecord newEventScore) {
        log.debug("Modifying event score {}-{}", new Object[] {
            scoId, escId});
        EventScoreRecord eventScore = eventScoresDB.findById(escId);
        if (eventScore != null) {
            eventScore.from(newEventScore);
            eventScore.store();
            return new ResponseEntity<EventScore>(eventScore.into(EventScore.class), HttpStatus.OK);
        } else {
            log.error("Event score not found: {}-{}", new Object[] {
                scoId, escId});
            return new ResponseEntity<EventScore>(HttpStatus.NOT_FOUND);
        }
    }
}
