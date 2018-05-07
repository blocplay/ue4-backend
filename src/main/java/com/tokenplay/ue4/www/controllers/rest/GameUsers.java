package com.tokenplay.ue4.www.controllers.rest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.model.db.tables.Profiles;
import com.tokenplay.ue4.model.db.tables.Tue4Pilot;
import com.tokenplay.ue4.model.db.tables.Users;
import com.tokenplay.ue4.model.db.tables.records.ProfilesRecord;
import com.tokenplay.ue4.model.db.tables.records.UsersRecord;
import com.tokenplay.ue4.model.repositories.UsersDB;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Controller
@Transactional
@RequestMapping(value = "/srest/users")
public class GameUsers extends AbstractRestHandler {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GameUser {
        private long id;
        private String email;
        private String name;
        private String steamId;
        private boolean enabled;
    }

    @Autowired
    DSLContext jooq;

    @Autowired
    UsersDB usersDB;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    @ResponseStatus(HttpStatus.OK)
    public List<GameUser> gameUsersList() {
        List<GameUser> gameUsers = null;
        log.debug("All game users requested");
        final Result<Record> records =
            jooq.selectFrom(
                Users.USERS.join(Profiles.PROFILES).on(Profiles.PROFILES.USER_ID.eq(Users.USERS.ID)).leftJoin(Tue4Pilot.PILOT)
                    .on(Tue4Pilot.PILOT.PIL_USU_ID.eq(Users.USERS.ID))).fetch();
        gameUsers = new ArrayList<>(records.size());
        for (Record record : records) {
            UsersRecord user = record.into(UsersRecord.class);
            ProfilesRecord profile = record.into(ProfilesRecord.class);
            //PilotRecord pilot = record.into(PilotRecord.class);
            gameUsers.add(buildGameUser(user, profile));
        }
        return gameUsers;
    }

    private GameUser buildGameUser(UsersRecord user, ProfilesRecord profile) {
        final GameUser gameUser = new GameUser();
        gameUser.setId(user.getId());
        gameUser.setEmail(user.getEmail());
        gameUser.setName(profile.getCallsign());
        gameUser.setSteamId(user.getSteamId());
        gameUser.setEnabled(Boolean.TRUE.equals(user.getAlpha()) && Boolean.TRUE.equals(user.getNda()));
        return gameUser;
    }

    //    @ResponseBody
    //    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    //    public ResponseEntity<GameUser> deleteUser(@PathVariable String id) {
    //        log.debug("Deleting user {}", id);
    //        UserRecord user = usersDB.findById(id);
    //        if (user != null && user.delete() > 0) {
    //            return new ResponseEntity<User>(HttpStatus.OK);
    //        } else {
    //            log.error("User not found: {}", id);
    //            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
    //        }
    //    }
    //
    //    @ResponseBody
    //    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    //    public ResponseEntity<GameUser> createUser(@RequestBody GameUser newUser) {
    //        log.debug("Creating user {}", newUser.getSrvAlias());
    //        newUser.setSrvId(newUUID());
    //        if (newUser.getSrvDevelopment() == null) {
    //            newUser.setSrvDevelopment(false);
    //        }
    //        if (newUser.getSrvSanctioned() == null) {
    //            newUser.setSrvSanctioned(false);
    //        }
    //        if (newUser.getSrvUseCycle() == null) {
    //            newUser.setSrvUseCycle(false);
    //        }
    //        newUser.setSrvVisible(false);
    //        newUser.attach(jooq.configuration());
    //        if (newUser.store() > 0) {
    //            return new ResponseEntity<User>(newUser.into(User.class), HttpStatus.OK);
    //        } else {
    //            log.error("User missing mandatory data");
    //            return new ResponseEntity<User>(newUser.into(User.class), HttpStatus.BAD_REQUEST);
    //        }
    //    }
    //

    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameUser> modifyUser(@RequestBody GameUser newUser) {
        try {
            if (newUser != null) {
                log.debug("Modifying user {}", newUser.getId());
                final Record record =
                    jooq.selectFrom(
                        Users.USERS.join(Profiles.PROFILES).on(Profiles.PROFILES.USER_ID.eq(Users.USERS.ID)).leftJoin(Tue4Pilot.PILOT)
                            .on(Tue4Pilot.PILOT.PIL_USU_ID.eq(Users.USERS.ID))).where(Users.USERS.ID.eq(newUser.getId())).fetchOne();
                if (record != null) {
                    UsersRecord user = record.into(UsersRecord.class);
                    ProfilesRecord profile = record.into(ProfilesRecord.class);
                    //PilotRecord pilot = record.into(PilotRecord.class);
                    if (StringUtils.isNotBlank(newUser.getEmail()) && !newUser.getEmail().equals(user.getEmail())) {
                        log.info("User email updated: {}, {} -> {}", profile.getCallsign(), user.getEmail(), newUser.getEmail());
                        user.setEmail(newUser.getEmail());
                        user.store();
                    }
                    if ((newUser.getSteamId() == null && user.getSteamId() != null)
                        || (newUser.getSteamId() != null && !newUser.getSteamId().equals(user.getSteamId()))) {
                        log.info("User steam_id updated: {}, {} -> {}", profile.getCallsign(), user.getSteamId(), newUser.getSteamId());
                        user.setSteamId(newUser.getSteamId());
                        user.store();
                    }
                    if (StringUtils.isNotBlank(newUser.getName()) && !newUser.getName().equals(profile.getCallsign())) {
                        log.info("User callsign updated: {} -> {}", profile.getCallsign(), newUser.getName());
                        profile.setCallsign(newUser.getName());
                        profile.store();
                    }
                    if (!(Boolean.TRUE.equals(user.getAlpha()) && Boolean.TRUE.equals(user.getNda()) && newUser.isEnabled())) {
                        log.info("User being enabled: {}", profile.getCallsign());
                        user.setAlpha(true);
                        user.setNda(true);
                        user.store();
                    }
                    return new ResponseEntity<>(buildGameUser(user, profile), HttpStatus.OK);
                } else {
                    log.info("No user found with that id: {}, no modifications will be performed", newUser.getId());
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() != null && e.getCause() instanceof SQLException) {
                throw new RuntimeException("Error updating user " + e.getMostSpecificCause().getMessage());
            } else {
                throw new RuntimeException("Error updating user " + ((SQLException) e.getCause()).getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating user " + e.getMessage());
        }
    }
}
