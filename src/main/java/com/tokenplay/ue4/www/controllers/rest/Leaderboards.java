package com.tokenplay.ue4.www.controllers.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.logic.DataProvider;
import com.tokenplay.ue4.model.db.tables.records.ScoreConfigRecord;
import com.tokenplay.ue4.model.repositories.ScoreConfigsDB;
import com.tokenplay.ue4.tasks.LeaderboardUpdater;
import com.tokenplay.ue4.tasks.LeaderboardUpdater.PilotStats;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Controller
@Transactional
@RequestMapping(value = "/srest/leaderboards/{scoId}")
public class Leaderboards extends AbstractRestHandler {

    @Autowired
    DataProvider dataProvider;

    @Autowired
    ScoreConfigsDB scoreConfigsDB;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<PilotStats>> leaderboard(@PathVariable String scoId) {
        log.debug("Calculating leaderboard using score configuration {}", scoId);
        ScoreConfigRecord scoreConfig = scoreConfigsDB.findById(scoId);
        if (scoreConfig != null) {
            Set<PilotStats> sortedPilots = LeaderboardUpdater.updateLeaderboard(dataProvider, scoreConfig.getScoName());
            List<PilotStats> pilotStats = new ArrayList<>();
            int counter = 1;
            for (PilotStats pilotStat : sortedPilots) {
                pilotStat.setPosition(counter++);
                pilotStats.add(pilotStat);
            }
            return new ResponseEntity<List<PilotStats>>(pilotStats, HttpStatus.OK);
        } else {
            return new ResponseEntity<List<PilotStats>>(HttpStatus.BAD_REQUEST);
        }
    }

}
