package com.tokenplay.ue4.www.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Either;
import com.tokenplay.ue4.model.db.tables.records.*;
import com.tokenplay.ue4.www.api.*;
import com.tokenplay.ue4.www.exception.UserNotFoundException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.util.Strings;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.tools.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.hazelcast.core.HazelcastInstance;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.configuration.HazelCastConfiguration;
import com.tokenplay.ue4.logic.TaskManager;
import com.tokenplay.ue4.model.db.tables.Tue4ServerAuthorised;
import com.tokenplay.ue4.model.db.tables.pojos.PaintScheme;
import com.tokenplay.ue4.model.db.tables.pojos.Server;
import com.tokenplay.ue4.model.repositories.ServersDB;
import com.tokenplay.ue4.steam.client.SteamApiClient;
import com.tokenplay.ue4.steam.client.types.ApiException;
import com.tokenplay.ue4.steam.client.types.api.AuthenticateUserTicketRS;
import com.tokenplay.ue4.steam.client.types.api.CheckAppOwnershipRS;
import com.tokenplay.ue4.steam.client.types.api.SteamApiSessionResponse;
import com.tokenplay.ue4.utils.BCrypt;
import com.tokenplay.ue4.www.caching.LiveServer;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static org.springframework.util.StringUtils.isEmpty;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Controller
@Transactional
public class GameAPI extends CommonAPI {

    private static final String DEVELOPMENT_KEYWORD = "serv6271@coolxeons";

    public static final int NUM_RETRIES = 3;

    @Autowired
    DSLContext jooq;

    @Autowired
    HazelcastInstance hazelcast;

    @Autowired(required = false)
    SteamApiClient steamApiClient;

    @ResponseBody
    @RequestMapping(value = "/leave_server", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @Retryable(backoff = @Backoff(delay = 500))
    public JSONResponse leaveServer(@RequestParam(value = "pil_token") String token, @RequestParam(value = "srv_id") String id, @RequestParam(
        value = "quit", defaultValue = "false") boolean quitting) {
        log.debug("Leave requested for token {} at server {}, quitting: {}", token, id, quitting);
        JSONResponse response = new JSONResponse();
        try {
            PilotRecord pilot = TaskManager.leaveServer(getDataProvider(), token, id);
            // If we are quitting, clean the pilot token as well
            if (pilot == null) {
                log.error("An unknown pilot with token {} tried to leave server {} or quit {}", token, id, quitting);
                response.setError("Pilot session invalid");

            } else if (quitting) {
                getPilots().clean(pilot);
                log.info("Pilot {} quit game", pilot.getPilCallsign());
            }
        } catch (Exception e) {
            log.info("Error while {} was leaving server {}:{}", new Object[] {
                token, id}, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response.setError("Error leaving server");
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/gi/{token}/rest/isdeveloper", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @Retryable(backoff = @Backoff(delay = 500))
    public JSONResponse isDeveloper(@PathVariable(value = "token") String token) {
        JSONResponse response = new JSONResponse();
        try {
            Pair<PilotRecord, UsersRecord> pair = getPilots().findPilotAndUser(token);
            UsersRecord user = null;
            if (pair != null) {
                user = pair.getRight();
            }

            if (getUsers().isDeveloper(user)) {
                response.setSuccess(true);
            }

        } catch (Exception e) {
            response.setError("Error finding developer.");
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/gi/rest/users/ext/{ext_service_id}/{ext_service_user_id}", produces = MediaType.APPLICATION_JSON_VALUE,
        method = RequestMethod.GET)
    @Retryable(backoff = @Backoff(delay = 500))
    public JSONResponse getUserByExternalServiceId(@PathVariable(value = "ext_service_user_id") String extServiceUserId, @PathVariable(
        value = "ext_service_id") String extServiceId, @RequestParam(value = "token") String token) {
        JSONResponse response = new JSONResponse();
        try {
            if (isAuthorized(token)) {
                ExternalService service = ExternalService.getEnum(extServiceId);
                switch (service) {
                    case STEAM:
                        Either<UserNotFoundException, JSONResponse> stuff = toUserResponse(getPilots().findBySteamId(extServiceUserId));
                        if (stuff.isRight()) {
                            response = stuff.get();
                            response.setSuccess(true);
                        } else {
                            response.setSuccess(true);
                            log.info("Failed to find user for external service ID " + extServiceUserId);
                        }
                        break;
                    default:
                        String msg = "Unhandled external service " + extServiceId;
                        log.warn(msg);
                        response.setError(msg);
                }
            } else {
                String msg = "Not authenticated";
                log.warn(msg);
                response.setError(msg);
            }
        } catch (IllegalArgumentException iae) {
            String message = "Unknown external service ID: " + extServiceId;
            log.error(message);
            response.setError(message);

        } catch (Exception e) {
            String message = "Exception when finding user by external service ID " + extServiceUserId + " for service " + extServiceId;
            log.error(message, e);
            response.setError(message);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/gi/rest/users/{user_id}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @Retryable(backoff = @Backoff(delay = 500))
    public JSONResponse getUserByUserId(@PathVariable(value = "user_id") String userId, @RequestParam(value = "token") String token) {
        JSONResponse response = new JSONResponse();
        try {
            if (isAuthorized(token)) {
                Either<UserNotFoundException, JSONResponse> stuff = toUserResponse(getPilots().findByIdWithUser(userId));
                if (stuff.isRight()) {
                    response = stuff.get();
                    response.setSuccess(true);
                } else {
                    response.setSuccess(true);
                    log.info("Failed to find user for user ID " + userId);
                }
            } else {
                String msg = "Not authenticated";
                log.warn(msg);
                response.setError(msg);
            }
        } catch (Exception e) {
            String msg = "Exception when finding user by user ID " + userId;
            log.error(msg, e);
            response.setError(msg);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/gi/rest/matchsession/{match_session_id}/join/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PUT)
    @Retryable(backoff = @Backoff(delay = 500))
    public JSONResponse joinMatchSession(@PathVariable(value = "match_session_id") String matchSessionId,
        @RequestParam(value = "joining") boolean joining, @RequestParam(value = "token") String token) {
        JSONResponse response = new JSONResponse();
        String authPilotId = getAuthPilotId(token);
        if (!authPilotId.isEmpty()) {
            UUID uuid = UUID.fromString(matchSessionId);

            Result<MatchSessionParticipantRecord> existing = getMatchSessionParticipantsDB().findForMatchSessionAndPilot(uuid, authPilotId);

            if (existing.isNotEmpty()) {
                // There can be only one, because of PK on the table
                MatchSessionParticipantRecord record = existing.get(0);
                boolean active = record.getMspActive();
                if (active) {
                    response.setSuccess(false);
                    response.setError("Pilot already joined to Match Session");
                    return response;
                } else {
                    if (joining) {
                        // Update the user to active and send positive response
                        int rowsUpdated = getMatchSessionParticipantsDB().updateToActive(uuid, authPilotId);
                        if (rowsUpdated != 1) {
                            // TODO: what to do if 0, return negative response?
                        }
                    } else {
                        // Delete the record and send positive response
                        getMatchSessionParticipantsDB().deleteParticipant(uuid, authPilotId);
                    }
                }
            } else {
                response.setSuccess(false);
                response.setError("Pilot not invited to Match Session");
                return response;
            }
            response.setSuccess(true);
        } else {
            String msg = "Not authenticated";
            log.warn(msg);
            response.setError(msg);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/gi/rest/matchsession/{match_session_id}/markready/", produces = MediaType.APPLICATION_JSON_VALUE,
        method = RequestMethod.PUT)
    @Retryable(backoff = @Backoff(delay = 500))
    public JSONResponse markMatchSessionReady(@PathVariable(value = "match_session_id") String matchSessionId,
        @RequestParam(value = "token") String token) {
        JSONResponse response = new JSONResponse();
        String authPilotId = getAuthPilotId(token);
        if (!authPilotId.isEmpty()) {
            UUID uuid = UUID.fromString(matchSessionId);

            Result<MatchSessionParticipantRecord> existing = getMatchSessionParticipantsDB().findForMatchSessionAndPilot(uuid, authPilotId);

            if (existing.isNotEmpty()) {
                MatchSessionRecord matchSessionRecord = getMatchSessionsDB().findById(uuid);

                // TODO: we need to do the match with a server
                String serverId = "000020cf2a2d4c4883034450c2af1e40";

                matchSessionRecord.setMsSrvId(serverId);
                matchSessionRecord.setMsActive(true);
                matchSessionRecord.store();

                ServerRecord serverRecord = getDataProvider().findServerById(serverId);

                // Create the message payload, containing the launch message
                LaunchMessagePayload launchMessage = new LaunchMessagePayload();
                launchMessage.setMatchSessionId(matchSessionId);
                launchMessage.setPort(serverRecord.getSrvUe4Port());
                launchMessage.setServerId(serverId);
                launchMessage.setUrlOrIp(serverRecord.getSrvIp());
                launchMessage.setZmqPort(serverRecord.getSrvZmqPort());

                List<String> pilotIds =
                    getMatchSessionParticipantsDB()
                            .findAllForMatchSession(uuid, true)
                            .map(a -> a.getMspPilotId())
                            .stream()
                            .collect(Collectors.toList());

                for (String pilotId : pilotIds) {
                    MessageRecord messageRecord = new MessageRecord();
                    messageRecord.setMsgDelivered(false);
                    messageRecord.setMsgExpiration(0.0f);

                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        String messageJsonString = mapper.writeValueAsString(launchMessage);
                        messageRecord.setMsgPayload(messageJsonString);
                    } catch (JsonProcessingException e) {
                        // TODO
                        e.printStackTrace();
                        messageRecord.setMsgPayload("");
                    }

                    messageRecord.setMsgRecipient(pilotId);
                    messageRecord.setMsgSender(authPilotId);
                    messageRecord.setMsgTime(OffsetDateTime.now());
                    messageRecord.setMsgType(MessageType.LAUNCH.code);
                    messageRecord.attach(jooq.configuration());
                    messageRecord.store();
                }

            } else {
                response.setSuccess(false);
                response.setError("Pilot not invited to Match Session");
                return response;
            }
            response.setSuccess(true);
        } else {
            String msg = "Not authenticated";
            log.warn(msg);
            response.setError(msg);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/gi/rest/matchsession/{match_session_id}/user/{user_id}/", produces = MediaType.APPLICATION_JSON_VALUE,
        method = RequestMethod.PUT)
    @Retryable(backoff = @Backoff(delay = 500))
    public JSONResponse invitePlayer(@PathVariable(value = "match_session_id") String matchSessionId,
        @PathVariable(value = "user_id") String pilotId, @RequestParam(value = "message") String message, @RequestParam(value = "token") String token) {
        JSONResponse response = new JSONResponse();
        String authPilotId = getAuthPilotId(token);
        if (!authPilotId.isEmpty()) {
            UUID uuid = UUID.fromString(matchSessionId);

            // TODO: check that the pilot ID is valid

            Result<MatchSessionParticipantRecord> existing = getMatchSessionParticipantsDB().findForMatchSessionAndPilot(uuid, pilotId);

            if (existing.isNotEmpty()) {
                response.setSuccess(false);
                response.setError("User already invited");
                return response;
            }

            // Store the user into the list of players in the MatchSession
            MatchSessionParticipantRecord matchSessionParticipant = new MatchSessionParticipantRecord();
            matchSessionParticipant.setMspMsId(uuid);
            matchSessionParticipant.setMspPilotId(pilotId);
            matchSessionParticipant.setMspUserIndex(-1);
            matchSessionParticipant.setMspActive(false);
            matchSessionParticipant.attach(jooq.configuration());
            matchSessionParticipant.store();

            // Queue the message to send to the user
            MessageRecord messageRecord = new MessageRecord();
            messageRecord.setMsgDelivered(false);
            messageRecord.setMsgExpiration(0.0f);

            // Create the message payload, containing the kick message
            AbstractMatchSessionMessagePayload inviteMessage = new MatchSessionInviteMessagePayload();
            inviteMessage.setMatchSessionId(matchSessionId);
            inviteMessage.setMessage(message);
            ObjectMapper mapper = new ObjectMapper();
            try {
                String messageJsonString = mapper.writeValueAsString(inviteMessage);
                messageRecord.setMsgPayload(messageJsonString);
            } catch (JsonProcessingException e) {
                // TODO
                e.printStackTrace();
                messageRecord.setMsgPayload("");
            }

            messageRecord.setMsgRecipient(pilotId);
            messageRecord.setMsgSender(authPilotId);
            messageRecord.setMsgTime(OffsetDateTime.now());
            messageRecord.setMsgType(MessageType.INVITE.code);
            messageRecord.attach(jooq.configuration());
            messageRecord.store();

            response.setSuccess(true);

        } else {
            String msg = "Not authenticated";
            log.warn(msg);
            response.setError(msg);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/gi/rest/message/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @Retryable(backoff = @Backoff(delay = 500))
    public MessageListContainer getUndeliveredMessages(@RequestParam(value = "token") String token) {
        MessageListContainer response = new MessageListContainer();
        String authPilotId = getAuthPilotId(token);
        if (!authPilotId.isEmpty()) {

            Result<MessageRecord> messageRecords = getMessagesDB().findAllNotDeliveredForRecipient(authPilotId);
            List<Message> messages = new ArrayList<>();

            for (MessageRecord messageRecord : messageRecords) {

                Message message = new Message();
                MessageType messageType = MessageType.getEnum(messageRecord.getMsgType());
                String payloadJsonString = messageRecord.getMsgPayload();
                ObjectMapper om = new ObjectMapper();

                AbstractMatchSessionMessagePayload payload = null;
                try {
                    JsonNode node = om.readTree(payloadJsonString);

                    String matchSessionId = node.path("MatchSessionId").asText();
                    String payloadMessage = node.path("Message").asText();

                    switch (messageType) {
                        case INVITE:
                            payload = new MatchSessionInviteMessagePayload();
                            payload.setMessage(payloadMessage);
                            payload.setMatchSessionId(matchSessionId);
                            break;
                        case KICK:
                            payload = new MatchSessionKickMessagePayload();
                            payload.setMessage(payloadMessage);
                            payload.setMatchSessionId(matchSessionId);
                            break;
                        default:
                            // TODO
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                message.setPayload(payload); // get this from the database

                message.setExpiration(messageRecord.getMsgExpiration());
                message.setType(MessageType.getEnum(messageRecord.getMsgType()).getCode());
                message.setSender(messageRecord.getMsgSender());
                message.setTime(messageRecord.getMsgTime());

                messages.add(message);

                // Once the message is sent, mark it as delivered in the database
                messageRecord.setMsgDelivered(true);
                messageRecord.update();
            }

            response.setMessages(messages);
            response.setTimestamp(OffsetDateTime.now());
            response.setType(ContainerType.MESSAGES.getCode());
        } else {
            String msg = "Not authenticated";
            log.warn(msg);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/gi/rest/matchsession/{match_session_id}/user/{user_id}/", produces = MediaType.APPLICATION_JSON_VALUE,
        method = RequestMethod.DELETE)
    @Retryable(backoff = @Backoff(delay = 500))
    public JSONResponse kickPlayer(@PathVariable(value = "match_session_id") String matchSessionId, @PathVariable(value = "user_id") String pilotId,
        @RequestParam(value = "message") String message, @RequestParam(value = "token") String token) {
        JSONResponse response = new JSONResponse();
        String authPilotId = getAuthPilotId(token);
        if (!authPilotId.isEmpty()) {
            UUID uuid = UUID.fromString(matchSessionId);

            PilotRecord pilotRecord = getPilots().findById(pilotId);

            if (pilotRecord != null) {
                MatchSessionRecord matchSessionRecord = getMatchSessionsDB().findById(uuid);
                if (matchSessionRecord != null) {
                    if (matchSessionRecord.getMsHostId().equals(pilotId)) {
                        // host can't be kicked
                        String msg = "Pilot cannot be kicked as they are the session host";
                        response.setError(msg);
                        log.warn(msg);
                        return response;
                    } else {
                        getMatchSessionParticipantsDB().deleteParticipant(uuid, pilotId);

                        // Queue the message to send to the recipient
                        MessageRecord messageRecord = new MessageRecord();
                        messageRecord.setMsgDelivered(false);
                        // TODO: where should this value come from?
                        messageRecord.setMsgExpiration(0.0f);

                        AbstractMatchSessionMessagePayload kickMessage = new MatchSessionKickMessagePayload();
                        kickMessage.setMatchSessionId(matchSessionId);
                        kickMessage.setMessage(message);

                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            String messageJsonString = mapper.writeValueAsString(kickMessage);
                            messageRecord.setMsgPayload(messageJsonString);
                        } catch (JsonProcessingException e) {
                            // TODO
                            e.printStackTrace();
                            messageRecord.setMsgPayload("");
                        }

                        messageRecord.setMsgRecipient(pilotId);
                        messageRecord.setMsgSender(authPilotId);
                        messageRecord.setMsgTime(OffsetDateTime.now());
                        messageRecord.setMsgType(MessageType.KICK.code);
                        messageRecord.attach(jooq.configuration());
                        messageRecord.store();

                        response.setSuccess(true);
                    }
                } else {
                    // No match session exists for the ID
                    String msg = "Cannot find Match Session with ID " + matchSessionId;
                    response.setError(msg);
                    log.warn(msg);
                    return response;
                }
            } else {
                // Pilot does not exist
                String msg = "Cannot find Pilot with ID " + pilotId;
                response.setError(msg);
                log.warn(msg);
                return response;
            }
        } else {
            String msg = "Not authenticated";
            log.warn(msg);
            response.setError(msg);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/gi/rest/matchsession/{match_session_id}/user/", produces = MediaType.APPLICATION_JSON_VALUE,
        method = RequestMethod.DELETE)
    @Retryable(backoff = @Backoff(delay = 500))
    public JSONResponse leaveMatchSession(@PathVariable(value = "match_session_id") String matchSessionId, @RequestParam(value = "token") String token) {
        JSONResponse response = new JSONResponse();
        String authPilotId = getAuthPilotId(token);
        if (!authPilotId.isEmpty()) {
            UUID uuid = UUID.fromString(matchSessionId);
            MatchSessionRecord matchSessionRecord = getMatchSessionsDB().findById(uuid);
            if (matchSessionRecord == null) {
                String msg = "No MatchSession exists for " + matchSessionId;
                response.setError(msg);
                log.warn(msg);
                return response;
            } else {
                if (matchSessionRecord.getMsHostId().equals(authPilotId)) {
                    // The user cannot leave the match session because they are the host
                    String msg = "You cannot leave the Match Session as you are the host";
                    response.setError(msg);
                    log.warn(msg);
                    return response;
                } else {
                    getMatchSessionParticipantsDB().deleteParticipant(uuid, authPilotId);
                    response.setSuccess(true);
                }
            }
        } else {
            String msg = "Not authenticated";
            log.warn(msg);
            response.setError(msg);
        }
        return response;
    }

    @RequestMapping(value = "/gi/rest/matchsession/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @Retryable(backoff = @Backoff(delay = 500))
    public @ResponseBody JSONResponse createMatchSession(@RequestBody MatchSession matchSession, @RequestParam(value = "token") String token) {
        JSONResponse response = new JSONResponse();
        String authPilotId = getAuthPilotId(token);
        if (!authPilotId.isEmpty()) {
            log.info("MatchSession with name " + matchSession.getServer() + " being created by host " + matchSession.getHost());

            MatchSessionRecord matchSessionRecord = new MatchSessionRecord();
            if (matchSession.isCanHaveBots()) {
                matchSessionRecord.setMsBotCountHigh(matchSession.getMaxBotCount());
                matchSessionRecord.setMsBotCountLow(matchSession.getMinBotCount());
                matchSessionRecord.setMsTargetBotCount(matchSession.getTargetBotCount());
                matchSessionRecord.setMsBotEnabled(true);
            }
            if (matchSession.isCompetitive()) {
                matchSessionRecord.setMsCompetitionId(Long.toString(matchSession.getCompetitionId()));
                matchSessionRecord.setMsCompetitive(true);
            }
            matchSessionRecord.setMsCreateDate(OffsetDateTime.now());
            matchSessionRecord.setMsHostId(authPilotId);
            matchSessionRecord.setMsMapId(matchSession.getMapAlias());
            matchSessionRecord.setMsMapMode(matchSession.getMapMode());
            matchSessionRecord.setMsMotd(matchSession.getMotd());
            matchSessionRecord.setMsName(matchSession.getServer());
            matchSessionRecord.setMsPlayersLimit(matchSession.getPlayerCount());
            matchSessionRecord.setMsRegionCode(matchSession.getRegionCode());
            matchSessionRecord.setMsScoreLimit(matchSession.getMatchScoreLimit());
            matchSessionRecord.setMsTimeLimit(matchSession.getMatchTimeLimit());
            matchSessionRecord.setMsState(MatchSessionState.LOBBY.getCode());
            matchSessionRecord.setMsTimeOfDay(matchSession.getTimeOfDayIndex());
            matchSessionRecord.setMsVersion(matchSession.getVersion());
            matchSessionRecord.setMsWeatherMode(matchSession.getWeatherModeIndex());
            matchSessionRecord.setMsActive(false);
            matchSessionRecord.attach(jooq.configuration());
            matchSessionRecord.store();

            // Store the host into the list of players in the MatchSession
            MatchSessionParticipantRecord matchSessionParticipant = new MatchSessionParticipantRecord();
            matchSessionParticipant.setMspMsId(matchSessionRecord.getMsId());
            matchSessionParticipant.setMspPilotId(matchSessionRecord.getMsHostId());
            matchSessionParticipant.setMspUserIndex(-1);
            matchSessionParticipant.setMspActive(true);
            matchSessionParticipant.attach(jooq.configuration());
            matchSessionParticipant.store();

            UUID createdId = matchSessionRecord.getMsId();
            response = new MatchSessionCreationResponse(createdId.toString());
            response.setSuccess(true);
        } else {
            String msg = "Not authenticated";
            log.warn(msg);
            response.setError(msg);
        }
        return response;
    }

    @RequestMapping(value = "/gi/rest/matchsession/{id}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @Retryable(backoff = @Backoff(delay = 500))
    public @ResponseBody JSONResponse getMatchSession(@PathVariable(value = "id") String id, @RequestParam(value = "token") String token) {
        JSONResponse response = new JSONResponse();
        if (isAuthorized(token)) {
            log.info("Retrieve MatchSession with ID " + id);
            UUID uuid = UUID.fromString(id);
            MatchSessionRecord matchSessionRecord = getMatchSessionsDB().findById(uuid);

            if (matchSessionRecord != null) {
                MatchSession matchSession = new MatchSession();
                matchSession.setId(matchSessionRecord.getMsId().toString());
                if (matchSessionRecord.getMsBotEnabled()) {
                    matchSession.setCanHaveBots(true);
                    matchSession.setMaxBotCount(matchSessionRecord.getMsBotCountHigh());
                    matchSession.setMinBotCount(matchSessionRecord.getMsBotCountLow());
                    matchSession.setTargetBotCount(matchSessionRecord.getMsTargetBotCount());
                } else {
                    matchSession.setCanHaveBots(false);
                }

                if (matchSessionRecord.getMsCompetitive()) {
                    matchSession.setCompetitive(true);
                    matchSession.setCompetitionId(Long.valueOf(matchSessionRecord.getMsCompetitionId()));
                } else {
                    matchSession.setCompetitive(false);
                }
                matchSession.setHost(matchSessionRecord.getMsHostId());
                matchSession.setMapAlias(matchSessionRecord.getMsMapId());
                matchSession.setMapMode(matchSessionRecord.getMsMapMode());
                matchSession.setMotd(matchSessionRecord.getMsMotd());
                matchSession.setServer(matchSessionRecord.getMsName());
                matchSession.setPlayerCount(matchSessionRecord.getMsPlayersLimit());
                matchSession.setRegionCode(matchSessionRecord.getMsRegionCode());
                matchSession.setMatchScoreLimit(matchSessionRecord.getMsScoreLimit());
                matchSession.setMatchTimeLimit(matchSessionRecord.getMsTimeLimit());
                matchSession.setSessionStateIndex(matchSessionRecord.getMsState());
                matchSession.setTimeOfDayIndex(matchSessionRecord.getMsTimeOfDay());
                matchSession.setVersion(matchSessionRecord.getMsVersion());
                matchSession.setWeatherModeIndex(matchSessionRecord.getMsWeatherMode());

                // Add the session participants into the response
                List<MatchSessionParticipantRecord> participants = getMatchSessionsDB().findParticipantsById(uuid);
                matchSession.setPlayers(participants.stream().map(a -> createPlayerFromParticipant(a)).collect(Collectors.toList()));

                response = new FindMatchSessionResponse(matchSession);
            }
            response.setSuccess(true);
        } else {
            String msg = "Not authenticated";
            log.warn(msg);
            response.setError(msg);
        }
        return response;
    }

    private Player createPlayerFromParticipant(MatchSessionParticipantRecord participant) {
        Player player = new Player();
        player.setId(participant.getMspPilotId());
        player.setTeamIndex(participant.getMspUserIndex());
        return player;
    }

    private boolean isAuthorized(String token) {
        return !getAuthPilotId(token).isEmpty();
    }

    private String getAuthPilotId(String token) {
        if (StringUtils.isNotBlank(token)) {
            Pair<PilotRecord, UsersRecord> userAndPilotForAuth = getPilots().findPilotAndUser(token);
            if (userAndPilotForAuth != null) {
                PilotRecord authPilot = userAndPilotForAuth.getLeft();
                UsersRecord authUser = userAndPilotForAuth.getRight();
                if (authPilot != null && authUser != null) {
                    return authPilot.getPilId();
                } else {
                    log.warn("Failed to authenticate for token " + token);
                }
            } else {
                log.warn("Failed to authenticate for token " + token);
            }
        } else {
            log.warn("Invalid token supplied: " + token);
        }
        return Strings.EMPTY;
    }

    private Either<UserNotFoundException, JSONResponse> toUserResponse(Pair<PilotRecord, UsersRecord> recordPair) {

        if (recordPair != null) {
            PilotRecord pilot = recordPair.getLeft();
            UsersRecord user = recordPair.getRight();

            String steamId = null;
            String name = null;
            String pilotId = null;
            String callsign = null;

            if (user != null) {
                if (!isEmpty(user.getSteamId())) {
                    steamId = user.getSteamId();
                }
                if (!isEmpty(user.getFirstname()) && !isEmpty(user.getLastname())) {
                    name = user.getFirstname() + " " + user.getLastname();
                }
                pilotId = pilot.getPilId();
            }
            if (pilot != null) {
                if (!isEmpty(pilot.getPilCallsign())) {
                    callsign = pilot.getPilCallsign();
                }
            }

            List<ExternalServiceID> externalServiceIDs = new ArrayList<>();
            if (steamId != null) {
                externalServiceIDs.add(new ExternalServiceID(ExternalService.STEAM.toString(), steamId));
            }

            return right(new FindUserResponse(externalServiceIDs, name, pilotId, callsign));

        } else {
            return left(new UserNotFoundException("User not found"));
        }
    }

    @ResponseBody
    @RequestMapping(value = "/join_server", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @Retryable(backoff = @Backoff(delay = 500))
    public JSONResponse joinServer(@RequestParam(value = "pil_token") String token, @RequestParam(value = "srv_id") String id, @RequestParam(
        value = "srv_password", required = false) String password) {
        log.info("Join requested for token {} at server {}", token, id);
        JSONResponse response = new JSONResponse();
        try {
            Pair<PilotRecord, UsersRecord> pair = getPilots().findPilotAndUser(token);
            PilotRecord pilot = null;
            UsersRecord user = null;
            List<String> authorisedPilots = null;
            if (pair != null) {
                pilot = pair.getLeft();
                user = pair.getRight();
            }
            Record record = getServers().findByIdWithPilotCount(id);
            ServerRecord server = null;
            MatchRecord match = null;
            ChampionshipRecord championship = null;
            Integer numPilots = null;
            if (record != null && record.size() > 0) {
                server = record.into(ServerRecord.class);
                match = record.into(MatchRecord.class);
                championship = record.into(ChampionshipRecord.class);
                numPilots = (Integer) record.getValue("PILOTS");
                authorisedPilots = getPilots().getAuthorisedPilotsForServer(id);
            }
            if (pilot != null && server != null && ServersDB.SERVER_ACTIVE_STATUS.equals(server.getSrvStatus())) {
                if ((CollectionUtils.isNotEmpty(authorisedPilots) && authorisedPilots.contains(pilot.getPilId()))
                    || (StringUtils.isBlank(server.getSrvPassword()) || (password != null && BCrypt.checkpw(password, server.getSrvPassword())))) {
                    String chaId = match.getMchChaId() != null ? match.getMchChaId() : server.getSrvChaId();
                    if (!pilot.getPilOffLimits() && getServers().isInChampionship(server, match)
                        && !getPilots().isRegisteredForChampionship(pilot, chaId)) {
                        log.error("Unregistered user is trying to join a championship server! {}-{}", pilot.getPilCallsign(),
                            championship.getChaName());
                        response.setError("Championship registration only server");
                    } else if (server.getSrvDevelopment() && !getUsers().isDeveloper(user)) {
                        log.info("Regular user trying to join a development server! {}", pilot.getPilCallsign());
                        response.setError("Development only server");
                    } else if (pilot.getPilOffLimits() || numPilots < server.getSrvMaxPlayers()) {
                        pilot.setPilSrvId(server.getSrvId());
                        pilot.store();
                        log.info("Pilot {} joined server {} ", pilot.getPilCallsign(), server.getSrvAlias());


                        String serverIP;
                        if (server.getSrvVisible()) {
                            serverIP = getServers().getSrvUE4Address(server);
                        } else {
                            log.info("Joining invisible server -> localhost");
                            serverIP = getServers().getInvisibleUE4Address(server);
                        }


                        response = new JoinServerResponse(serverIP, server.getSrvId(), server.getSrvAlias(), server.getSrvName());
                        response.setSuccess(true);
                    } else {
                        log.error("Server limit reached!");
                        response.setError("Server limit reached!");
                    }
                } else {
                    log.error("Pilot {}/{} tried to join server {}/{} with the wrong password", pilot.getPilId(), pilot.getPilCallsign(),
                        server.getSrvId(), server.getSrvAlias());
                    response.setError("Wrong password!");
                }
            } else if (pilot == null) {
                log.error("An unknown pilot with token {} tried to join ", token);
                response.setError("Pilot session invalid");
            } else if (server == null) {
                log.error("A pilot with token {} tried to join a non existant server with id {}", token, id);
                response.setError("Server not found");
            } else {
                log.error("Server {} is not active", server.getSrvAlias());
                response.setError("Server is down");
            }
        } catch (Exception e) {
            log.info("Error while {} was joining server {}:{}", new Object[] {
                token, id}, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response.setError("Error joining server");
        }
        return response;
    }


    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/gi/{token}/rest/get_allservers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject getallservers(@PathVariable(value = "token") String token) {
        log.info("Getting all servers");
        JSONObject response = new JSONObject();
        JSONObject jServerCollection = new JSONObject();

        Pair<PilotRecord, UsersRecord> pair = getPilots().findPilotAndUser(token);
        UsersRecord user = null;
        if (pair != null) {
            user = pair.getRight();
        }

        if (getUsers().isDeveloper(user)) {
            Result<ServerRecord> gServer;

            gServer = getServers().findAll();

            for (ServerRecord s : gServer) {
                JSONObject jServerRecord = new JSONObject();
                String getSrvAlias = removeNull(s.getSrvAlias());
                String getSrvChaId = removeNull(s.getSrvChaId());
                String getSrvCyclelist = removeNull(s.getSrvCyclelist());
                String getSrvDefaultMapTimeLimit = removeNull(s.getSrvDefaultMapTimeLimit());
                String getSrvDescription = removeNull(s.getSrvDescription());
                String getSrvDevelopment = removeNull(s.getSrvDevelopment());
                String getSrvId = removeNull(s.getSrvId());
                String getSrvIp = removeNull(s.getSrvIp());
                String getSrvLastCheck = removeNull(s.getSrvLastCheck());
                String getSrvLastUse = removeNull(s.getSrvLastUse());
                String getSrvMapCycleLastIndex = removeNull(s.getSrvMapCycleLastIndex());
                String getSrvMatchTime = removeNull(s.getSrvMatchTime());
                String getSrvMaxBots = removeNull(s.getSrvMaxBots());
                String getSrvMaxPlayers = removeNull(s.getSrvMaxPlayers());
                String getSrvMchId = removeNull(s.getSrvMchId());
                String getSrvName = removeNull(s.getSrvName());
                String getSrvOverrideMapPlayerCount = removeNull(s.getSrvOverrideMapPlayerCount());
                String getSrvPilId = removeNull(s.getSrvPilId());
                String getSrvRunningVersion = removeNull(s.getSrvRunningVersion());
                String getSrvSanctioned = removeNull(s.getSrvSanctioned());
                String getSrvShuffle = removeNull(s.getSrvShuffle());
                String getSrvStatus = removeNull(s.getSrvStatus());
                String getSrvUe4Port = removeNull(s.getSrvUe4Port());
                String getSrvUseCustomCycle = removeNull(s.getSrvUseCustomCycle());
                String getSrvUseCycle = removeNull(s.getSrvUseCycle());
                String getSrvVisible = removeNull(s.getSrvVisible());
                String getSrvZmqPort = removeNull(s.getSrvZmqPort());


                //                Result<MapcycleRecord> gMapCycle = null;
                //
                //                gMapCycle = getMapCyclesDB().findBySrvId(getSrvId);
                //
                //                for (MapcycleRecord m : gMapCycle) {
                //                    JSONObject jMapCycleRecord = new JSONObject();
                //                    String getMapAlias = removeNull(m.getMapAlias());
                //                    String getMapId = removeNull(m.getMapId());
                //                    String getMapMaxBotCount = removeNull(m.getMapMaxBotCount());
                //                    String getMapMinBotSkillLevel = removeNull(m.getMapMinBotSkillLevel());
                //                    String getMapMode = removeNull(m.getMapMode());
                //                    String getMapSrvId = removeNull(m.getMapSrvId());
                //                    String getMapTimeLimit = removeNull(m.getMapTimeLimit());
                //                    String getMapWeather = removeNull(m.getMapWeather());
                //                    String getMapTimeOfDay = removeNull(m.getMapTimeOfDay());
                //                    String getMatchScoreLimit = removeNull(m.getMapMatchScoreLimit());
                //
                //                    jMapCycleRecord.put("MAL", getMapAlias.toString());
                //                    jMapCycleRecord.put("MID", getMapId.toString());
                //                    jMapCycleRecord.put("MAXBC", getMapMaxBotCount.toString());
                //                    jMapCycleRecord.put("MAXBL", getMapMinBotSkillLevel.toString());
                //                    jMapCycleRecord.put("MMD", getMapMode.toString());
                //                    jMapCycleRecord.put("SID", getMapSrvId.toString());
                //                    jMapCycleRecord.put("MTL", getMapTimeLimit.toString());
                //                    jMapCycleRecord.put("MWT", getMapWeather.toString());
                //                    jMapCycleRecord.put("MTD", getMapTimeOfDay.toString());
                //                    jMapCycleRecord.put("MSL", getMatchScoreLimit.toString());
                //
                //                    jServerRecord.put(getMapId, jMapCycleRecord);
                //                }


                jServerRecord.put("AL", getSrvAlias);
                jServerRecord.put("CHID", getSrvChaId);
                jServerRecord.put("CYCL", getSrvCyclelist);
                jServerRecord.put("DMTL", getSrvDefaultMapTimeLimit);
                jServerRecord.put("DE", getSrvDescription);
                jServerRecord.put("DEV", getSrvDevelopment);
                jServerRecord.put("ID", getSrvId);
                jServerRecord.put("IP", getSrvIp);
                jServerRecord.put("LC", getSrvLastCheck);
                jServerRecord.put("LU", getSrvLastUse);
                jServerRecord.put("MCLI", getSrvMapCycleLastIndex);
                jServerRecord.put("MT", getSrvMatchTime);
                jServerRecord.put("MAXB", getSrvMaxBots);
                jServerRecord.put("MAXP", getSrvMaxPlayers);
                jServerRecord.put("MID", getSrvMchId);
                jServerRecord.put("Name", getSrvName);
                jServerRecord.put("SMPO", getSrvOverrideMapPlayerCount);
                jServerRecord.put("PILID", getSrvPilId);
                jServerRecord.put("VER", getSrvRunningVersion);
                jServerRecord.put("SAN", getSrvSanctioned);
                jServerRecord.put("SH", getSrvShuffle);
                jServerRecord.put("STAT", getSrvStatus);
                jServerRecord.put("HP", getSrvUe4Port);
                jServerRecord.put("UCYL", getSrvUseCustomCycle);
                jServerRecord.put("CYL", getSrvUseCycle);
                jServerRecord.put("VIS", getSrvVisible);
                jServerRecord.put("ZP", getSrvZmqPort);

                jServerCollection.put(getSrvAlias, jServerRecord);
            }

            response.put("Servers", jServerCollection);

        }

        return response;
    }

    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/gi/{token}/rest/myservers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject getmyservers(@PathVariable(value = "token") String token) {
        log.debug("Getting my servers");
        JSONObject response = new JSONObject();
        JSONObject jServerCollection = new JSONObject();

        PilotRecord pilot = getPilots().findByToken(token);
        if (pilot != null) {
            Result<ServerRecord> gServer;

            gServer = getServers().findByPilId(pilot.getPilId());

            for (ServerRecord s : gServer) {
                JSONObject jServerRecord = new JSONObject();
                String getSrvAlias = removeNull(s.getSrvAlias());
                String getSrvChaId = removeNull(s.getSrvChaId());
                String getSrvCyclelist = removeNull(s.getSrvCyclelist());
                String getSrvDefaultMapTimeLimit = removeNull(s.getSrvDefaultMapTimeLimit());
                String getSrvDescription = removeNull(s.getSrvDescription());
                String getSrvDevelopment = removeNull(s.getSrvDevelopment());
                String getSrvId = removeNull(s.getSrvId());
                String getSrvIp = removeNull(s.getSrvIp());
                String getSrvLastCheck = removeNull(s.getSrvLastCheck());
                String getSrvLastUse = removeNull(s.getSrvLastUse());
                String getSrvMapCycleLastIndex = removeNull(s.getSrvMapCycleLastIndex());
                String getSrvMatchTime = removeNull(s.getSrvMatchTime());
                String getSrvMaxBots = removeNull(s.getSrvMaxBots());
                String getSrvMaxPlayers = removeNull(s.getSrvMaxPlayers());
                String getSrvMchId = removeNull(s.getSrvMchId());
                String getSrvName = removeNull(s.getSrvName());
                String getSrvOverrideMapPlayerCount = removeNull(s.getSrvOverrideMapPlayerCount());
                String getSrvPilId = removeNull(s.getSrvPilId());
                String getSrvRunningVersion = removeNull(s.getSrvRunningVersion());
                String getSrvSanctioned = removeNull(s.getSrvSanctioned());
                String getSrvShuffle = removeNull(s.getSrvShuffle());
                String getSrvStatus = removeNull(s.getSrvStatus());
                String getSrvUe4Port = removeNull(s.getSrvUe4Port());
                String getSrvUseCustomCycle = removeNull(s.getSrvUseCustomCycle());
                String getSrvUseCycle = removeNull(s.getSrvUseCycle());
                String getSrvVisible = removeNull(s.getSrvVisible());
                String getSrvZmqPort = removeNull(s.getSrvZmqPort());

                Result<MapcycleRecord> gMapCycle;

                gMapCycle = getMapCyclesDB().findBySrvId(getSrvId);

                for (MapcycleRecord m : gMapCycle) {
                    JSONObject jMapCycleRecord = new JSONObject();
                    String getMapAlias = removeNull(m.getMapAlias());
                    String getMapId = removeNull(m.getMapId());
                    String getMapMaxBotCount = removeNull(m.getMapMaxBotCount());
                    String getMapMinBotSkillLevel = removeNull(m.getMapMinBotSkillLevel());
                    String getMapMode = removeNull(m.getMapMode());
                    String getMapSrvId = removeNull(m.getMapSrvId());
                    String getMapTimeLimit = removeNull(m.getMapTimeLimit());
                    String getMapWeather = removeNull(m.getMapWeather());
                    String getMapTimeOfDay = removeNull(m.getMapTimeOfDay());
                    String getMatchScoreLimit = removeNull(m.getMapMatchScoreLimit());

                    jMapCycleRecord.put("MAL", getMapAlias);
                    jMapCycleRecord.put("MID", getMapId);
                    jMapCycleRecord.put("MAXBC", getMapMaxBotCount);
                    jMapCycleRecord.put("MAXBL", getMapMinBotSkillLevel);
                    jMapCycleRecord.put("MMD", getMapMode);
                    jMapCycleRecord.put("SID", getMapSrvId);
                    jMapCycleRecord.put("MTL", getMapTimeLimit);
                    jMapCycleRecord.put("MWT", getMapWeather);
                    jMapCycleRecord.put("MTD", getMapTimeOfDay);
                    jMapCycleRecord.put("MSL", getMatchScoreLimit);

                    jServerRecord.put(getMapId, jMapCycleRecord);
                }


                jServerRecord.put("AL", getSrvAlias);
                jServerRecord.put("CHID", getSrvChaId);
                jServerRecord.put("CYCL", getSrvCyclelist);
                jServerRecord.put("DMTL", getSrvDefaultMapTimeLimit);
                jServerRecord.put("DE", getSrvDescription);
                jServerRecord.put("DEV", getSrvDevelopment);
                jServerRecord.put("ID", getSrvId);
                jServerRecord.put("IP", getSrvIp);
                jServerRecord.put("LC", getSrvLastCheck);
                jServerRecord.put("LU", getSrvLastUse);
                jServerRecord.put("MCLI", getSrvMapCycleLastIndex);
                jServerRecord.put("MT", getSrvMatchTime);
                jServerRecord.put("MAXB", getSrvMaxBots);
                jServerRecord.put("MAXP", getSrvMaxPlayers);
                jServerRecord.put("MID", getSrvMchId);
                jServerRecord.put("Name", getSrvName);
                jServerRecord.put("SMPO", getSrvOverrideMapPlayerCount);
                jServerRecord.put("PILID", getSrvPilId);
                jServerRecord.put("VER", getSrvRunningVersion);
                jServerRecord.put("SAN", getSrvSanctioned);
                jServerRecord.put("SH", getSrvShuffle);
                jServerRecord.put("STAT", getSrvStatus);
                jServerRecord.put("HP", getSrvUe4Port);
                jServerRecord.put("UCYL", getSrvUseCustomCycle);
                jServerRecord.put("CYL", getSrvUseCycle);
                jServerRecord.put("VIS", getSrvVisible);
                jServerRecord.put("ZP", getSrvZmqPort);

                jServerCollection.put(getSrvAlias, jServerRecord);
            }

            response.put("Servers", jServerCollection);

        }

        return response;
    }


    /*
     * @ResponseBody
     * 
     * @RequestMapping(value = "/gi/{token}/rest/myservers/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
     * public ResponseEntity<Boolean> modifymyservers(HttpServletRequest requestContext, @PathVariable(value = "token") String token, @PathVariable
     * String id, @RequestBody String serverspec)
     * {
     * PilotRecord pilot = getPilots().findByToken(token);
     * if (pilot != null)
     * {
     * ServerRecord record = getServers().findById(id);
     * if (record != null)
     * {
     * try
     * {
     * ObjectMapper mapper = new ObjectMapper();
     * Map<String, Object> map = new HashMap<String, Object>();
     * map = mapper.readValue(serverspec, new TypeReference<Map<String, String>>() {
     * });
     * 
     * System.out.println(map);
     * 
     * //(removeNull(map.get("SENG")));
     * 
     * record.setSrvAlias(removeNull(map.get("AL")));
     * record.setSrvChaId(removeNull(map.get("CHID")));
     * record.setSrvCyclelist(removeNull(map.get("CYCL")));
     * record.setSrvDefaultMapTimeLimit(Long.valueOf(removeNull(map.get("DMTL"))));
     * record.setSrvDescription(removeNull(map.get("DE")));
     * record.setSrvDevelopment(Boolean.valueOf(removeNull(map.get("DEV"))));
     * record.setSrvId(removeNull(map.get("ID")));
     * record.setSrvMapCycleLastIndex(Long.valueOf(removeNull(map.get("MCLI"))));
     * record.setSrvMatchTime(Long.valueOf(removeNull(map.get("MT"))));
     * record.setSrvMaxBots(Long.valueOf(removeNull(map.get("MAXB"))));
     * record.setSrvMaxPlayers(Long.valueOf(removeNull(map.get("MAXP"))));
     * record.setSrvMchId(removeNull(map.get("MID")));
     * record.setSrvName(removeNull(map.get("AL")));
     * record.setSrvOverrideMapPlayerCount(Long.valueOf(removeNull(map.get("SMPO"))));
     * record.setSrvPilId(removeNull(map.get("PILID")));
     * record.setSrvRunningVersion(removeNull(map.get("VER")));
     * record.setSrvSanctioned(Boolean.valueOf(removeNull(map.get("SAN"))));
     * record.setSrvShuffle(Boolean.valueOf(removeNull(map.get("SH"))));
     * record.setSrvStatus(removeNull(map.get("STAT")));
     * record.setSrvUe4Port(removeNull(map.get("HP")));
     * record.setSrvUseCustomCycle(Boolean.valueOf(removeNull(map.get("UCYL"))));
     * record.setSrvUseCycle(Boolean.valueOf(removeNull(map.get("CYL"))));
     * record.setSrvVisible(Boolean.valueOf(removeNull(map.get("VIS"))));
     * record.setSrvZmqPort(removeNull(map.get("ZP")));
     * 
     * String ipAddress = requestContext.getHeader("X-FORWARDED-FOR");
     * if(ipAddress==null)
     * {
     * record.setSrvIp(requestContext.getRemoteAddr());
     * }
     * else
     * {
     * record.setSrvIp(ipAddress);
     * }
     * 
     * //get the map cycle modes
     * Result<MapcycleRecord> gMapCycle= null;
     * 
     * gMapCycle = mapCyclesDB.findBySrvId(removeNull(map.get("ID")));
     * 
     * Integer max = Integer.valueOf(removeNull(map.get("MCLI")));
     * 
     * int counter = 0;
     * 
     * for(MapcycleRecord m : gMapCycle)
     * { m.attach(jooq.configuration());
     * //update cycle
     * ObjectMapper mapper2 = new ObjectMapper();
     * Map<String, Object> map2 = new HashMap<String, Object>();
     * map2 = mapper2.readValue(map.get(counter).toString(), new TypeReference<Map<String, String>>() {
     * });
     * 
     * System.out.println(map);
     * 
     * m.setMapAlias(removeNull(map2.get("MAL")));
     * m.setMapMaxBotCount(Integer.valueOf(removeNullNum(map2.get("MAXBC"))));
     * m.setMapMaxBotSkillLevel(Integer.valueOf(removeNullNum(map2.get("MAXBL"))));
     * m.setMapMinBotSkillLevel(Integer.valueOf(removeNullNum(map2.get("MINBL"))));
     * m.setMapMode(removeNull(map2.get("MMD")));
     * m.setMapTimeLimit(Integer.valueOf(removeNullNum(map2.get("MTL"))));
     * m.setMapTimeOfDay(Integer.valueOf(removeNullNum(map2.get("MTD"))));
     * m.setMapWeather(Integer.valueOf(removeNullNum(map2.get("MWT"))));
     * 
     * m.update();
     * }
     * 
     * record.update();
     * 
     * return new ResponseEntity<Boolean>(true, HttpStatus.OK);
     * }
     * 
     * catch (exception e)
     * {
     * e.printStackTrace(System.out);
     * return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
     * }
     * }
     * else
     * {
     * return new ResponseEntity<Boolean>(false, HttpStatus.NOT_FOUND);
     * }
     * }
     * return new ResponseEntity<Boolean>(false, HttpStatus.NOT_FOUND);
     * }
     */


    @ResponseBody
    @RequestMapping(value = "/gi/rest/myservers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONResponse registermyservers(@RequestBody ServerDataSpec serverDataSpec, HttpServletRequest requestContext) {
        JSONResponse response = new JSONResponse();
        log.debug("Registering my servers");
        if (serverDataSpec != null) {
            try {
                ServerRecord registeredServer = null;
                final String serverId = serverDataSpec.getId();
                if (StringUtils.isNotEmpty(serverId)) {
                    registeredServer = getServers().findById(serverId);
                    // If the server already exists, clean up previous security settings and we'll update it later
                    if (registeredServer != null) {
                        jooq.delete(Tue4ServerAuthorised.SERVER_AUTHORISED).where(Tue4ServerAuthorised.SERVER_AUTHORISED.SAU_SRV_ID.eq(serverId))
                            .execute();
                    }
                }
                if (registeredServer == null) {
                    registeredServer = new ServerRecord();
                    registeredServer.attach(jooq.configuration());
                    registeredServer.setSrvId(newUUID());
                }

                ChampionshipRecord championShip = null;

                final String championShipID = serverDataSpec.getChampionship();
                if (StringUtils.isNotEmpty(championShipID)) {
                    championShip = getChampionshipsDB().findById(championShipID);
                }
                //associate server with pilot email
                UsersRecord usr;
                PilotRecord pil;
                usr = getUsers().findByEmail(removeNull(serverDataSpec.getEmail()));
                if (usr != null) {
                    pil = getPilots().findByUserId(usr.getId());

                    if (pil != null) {
                        registeredServer.setSrvPilId(pil.getPilId());
                    }
                    registeredServer.setSrvPassword(null);
                    registeredServer.setSrvAlias(removeNull(serverDataSpec.getAlias()));
                    registeredServer.setSrvDefaultMapTimeLimit(serverDataSpec.getDefaultMapTimeLimit());
                    registeredServer.setSrvDescription(removeNull(serverDataSpec.getDescription()));
                    registeredServer.setSrvDevelopment(serverDataSpec.getDevelopment());
                    registeredServer.setSrvMapCycleLastIndex(serverDataSpec.getMapCycleLastIndex());
                    registeredServer.setSrvMatchTime(serverDataSpec.getMatchTime());
                    registeredServer.setSrvMaxBots(serverDataSpec.getMaxBots());
                    registeredServer.setSrvMaxPlayers(serverDataSpec.getMaxPlayers());
                    registeredServer.setSrvName(serverDataSpec.getAlias());
                    registeredServer.setSrvOverrideMapPlayerCount(serverDataSpec.getOverrideMapPlayerCount());
                    registeredServer.setSrvRunningVersion(removeNull(serverDataSpec.getVersion()));
                    registeredServer.setSrvVisible(serverDataSpec.getVisible());
                    registeredServer.setSrvShuffle(serverDataSpec.getShuffle());
                    registeredServer.setSrvUe4Port(removeNull(serverDataSpec.getUe4Port()));
                    registeredServer.setSrvUseCustomCycle(serverDataSpec.getUseCustomCycle());
                    registeredServer.setSrvZmqPort(removeNull(serverDataSpec.getZmqPort()));
                    if (championShip != null) {
                        registeredServer.setSrvChaId(championShip.getChaId());
                    } else {
                        registeredServer.setSrvChaId(null);
                    }
                    //this password should eventually be in the db
                    registeredServer.setSrvDevelopment(DEVELOPMENT_KEYWORD.equals(serverDataSpec.getDevelopmentPassword()));
                    //
                    if (StringUtils.isEmpty(serverDataSpec.getIp())) {
                        String ipAddress = requestContext.getHeader("X-FORWARDED-FOR");
                        if (ipAddress == null) {
                            registeredServer.setSrvIp(requestContext.getRemoteAddr());
                        } else {
                            registeredServer.setSrvIp(ipAddress);
                        }
                    } else {
                        registeredServer.setSrvIp(serverDataSpec.getIp());
                    }

                    //                    //index
                    //                    int indexLast = Integer.valueOf(removeNullNum(map.get("MCLI")));
                    //                    for (int i = 0; i <= indexLast; i++) {
                    //                        org.json.JSONObject map2 = map.getJSONObject(String.valueOf(i));
                    //
                    //                        MapcycleRecord m = new MapcycleRecord();
                    //                        m.attach(jooq.configuration());
                    //
                    //                        String mapCycleUUID = newUUID();
                    //
                    //                        m.setMapId(mapCycleUUID);
                    //                        m.setMapAlias(removeNull(map2.get("MAL")));
                    //                        m.setMapMaxBotCount(Integer.valueOf(removeNullNum(map2.get("MAXBC"))));
                    //                        m.setMapMaxBotSkillLevel(Integer.valueOf(removeNullNum(map2.get("MAXBL"))));
                    //                        m.setMapMinBotSkillLevel(Integer.valueOf(removeNullNum(map2.get("MINBL"))));
                    //                        m.setMapMode(removeNull(map2.get("MMD")));
                    //                        m.setMapTimeLimit(Integer.valueOf(removeNullNum(map2.get("MTL"))));
                    //                        m.setMapTimeOfDay(Integer.valueOf(removeNullNum(map2.get("MTD"))));
                    //                        m.setMapWeather(Integer.valueOf(removeNullNum(map2.get("MWT"))));
                    //                        m.setMapMatchScoreLimit(Integer.valueOf(removeNullNum(map2.get("MSL"))));
                    //                        m.setMapSrvId(registeredServer.getSrvId());
                    //
                    //                        m.store();
                    //                    }

                    registeredServer.store();

                    if (serverDataSpec.isSecurityEnabled()) {
                        if (StringUtils.isNotBlank(serverDataSpec.getPassword())) {
                            registeredServer.setSrvPassword(BCrypt.hashpw(serverDataSpec.getPassword(), BCrypt.gensalt()));
                            registeredServer.store();
                        }
                        if (StringUtils.isNotBlank(serverDataSpec.getAllowedUsers())) {
                            String[] allowedUsers = serverDataSpec.getAllowedUsers().split(",");
                            for (String allowedUser : allowedUsers) {
                                if (StringUtils.isNotBlank(allowedUser)) {
                                    allowedUser = allowedUser.trim();
                                    String[] parts = allowedUser.split("\\_");
                                    PilotRecord authorisedPilot = null;
                                    if (allowedUser.length() == 17 && allowedUser.indexOf('_') == -1) {
                                        Pair<PilotRecord, UsersRecord> record = getPilots().findBySteamId(allowedUser);
                                        if (record != null) {
                                            authorisedPilot = record.getLeft();
                                        }
                                    } else {
                                        try {
                                            Long id = null;
                                            if (parts.length == 2) {
                                                id = new Long(parts[1]);
                                            } else if (parts.length == 1) {
                                                id = new Long(parts[0]);
                                            }
                                            if (id != null) {
                                                authorisedPilot = getPilots().findByUserId(id);
                                            }
                                        } catch (Exception e) {
                                            log.error("Error finding user");
                                        }
                                    }
                                    if (authorisedPilot != null) {
                                        ServerAuthorisedRecord serverAuthorisedRecord =
                                            new ServerAuthorisedRecord(registeredServer.getSrvId(), authorisedPilot.getPilId());
                                        serverAuthorisedRecord.attach(jooq.configuration());
                                        serverAuthorisedRecord.store();
                                    } else {
                                        log.warn("Server {} tried to authorise user {} but no such user exist.", registeredServer.getSrvAlias(),
                                            allowedUser);
                                    }
                                }
                            }
                        }
                    }

                    response =
                        new RegisterServerResponse(registeredServer.getSrvId(), registeredServer.getSrvAlias(), registeredServer.getSrvDescription(),
                            registeredServer.getSrvMatchTime(), registeredServer.getSrvDevelopment(), registeredServer.getSrvMaxBots(),
                            registeredServer.getSrvMaxPlayers(), registeredServer.getSrvOverrideMapPlayerCount());
                    response.setSuccess(true);
                } else {
                    log.error("Unknown user {} tried to register a server.", serverDataSpec.getEmail());
                }
            } catch (Exception e) {
                log.error("Error trying to register a server.", e);
                response.setError("Server error can not read data");
            }
        } else {
            response.setError("Server spec was empty.");
        }

        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/stop_server", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @Retryable(backoff = @Backoff(delay = 500))
    public JSONResponse stopServer(@RequestParam(value = "pil_token") String token, @RequestParam(value = "srv_id") String id,
        @ModelAttribute("ip") String ip) {
        JSONResponse response = new JSONResponse();
        try {
            PilotRecord pilot = getPilots().findByToken(token);
            if (pilot != null) {
                ServerRecord server = getServers().findById(id);
                if (server != null) {
                    if (pilot.getPilId().equals(server.getSrvPilId())) {
                        getDataProvider().finishServer(server.into(Server.class));
                        log.info("Server stopped: {}->{}:{}", pilot.getPilCallsign(), server.getSrvAlias(), ip);
                        response.setSuccess(true);
                    } else {
                        log.error("Pilot {}({}) tried to stop a server from a different pilot {}", pilot.getPilCallsign(), pilot.getPilId(), ip,
                            server.getSrvPilId());
                        response.setError("Server cannot be stopped with that data");
                    }
                } else {
                    log.error("Pilot {} tried to stop a server with id {} and there is no server with that id", new Object[] {
                        pilot.getPilCallsign(), id});
                    response.setError("No server running with that id");
                }
            } else {
                log.error("An unknown pilot with token {} tried to stop a server with id {}", token, id);
                response.setError("Pilot session invalid");
            }
        } catch (Exception e) {
            log.info("Error stopping server", ip, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response.setError("Error stopping server");
        }
        return response;
    }

    /*
     * @ResponseBody
     * 
     * @RequestMapping(value = "/register_server_ip", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
     * 
     * @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 500))
     * public JSONResponse registerServerByIP(@RequestParam(value = "srv_id", required = false) String id,
     * 
     * @RequestParam(value = "srv_port", required = false) String port, @RequestParam(value = "srv_zmq_port", required = false) String zmqPort,
     * 
     * @ModelAttribute("ip") String ip)
     * {
     * JSONResponse response = new JSONResponse();
     * try
     * {
     * if (StringUtils.isBlank(port) || ServersDB.DEFAULT_UE4_PORT.equals(port))
     * {
     * port = null;
     * }
     * if (StringUtils.isBlank(zmqPort) || ServersDB.DEFAULT_ZMQ_PORT.equals(zmqPort))
     * {
     * zmqPort = null;
     * }
     * ServerRecord server = null;
     * if (StringUtils.isNotBlank(id))
     * {
     * server = getServers().findById(id);
     * if (server != null)
     * {
     * log.debug("Server registered by ID: {}->{}", ip, server.getSrvAlias());
     * response = new RegisterServerResponse(
     * server.getSrvId(),
     * server.getSrvAlias(),
     * server.getSrvName(),
     * server.getSrvDescription(),
     * server.getSrvMatchTime(),
     * server.getSrvMaxBots());
     * server.setSrvIp(ip);
     * server.setSrvUe4Port(port);
     * server.setSrvZmqPort(zmqPort);
     * server.store();
     * response.setSuccess(true);
     * }
     * else
     * {
     * log.info("Server could not be registered by ID: No server with that id {}", id);
     * response.setError("Server not found.");
     * }
     * }
     * else
     * {
     * Result<ServerRecord> ipServers = getServers().findByIpAndUe4Port(ip, port);
     * if (ipServers.size() == 1)
     * {
     * // There is just one, converting it to an array would be a pain so...
     * for (ServerRecord ipServer : ipServers)
     * {
     * server = ipServer;
     * }
     * log.debug("Server registered by IP: {}:{}->{}", new Object[]
     * { ip, port, server.getSrvAlias() });
     * response = new RegisterServerResponse(
     * server.getSrvId(),
     * server.getSrvAlias(),
     * server.getSrvName(),
     * server.getSrvDescription(),
     * server.getSrvMatchTime(),
     * server.getSrvMaxBots());
     * server.setSrvUe4Port(port);
     * server.setSrvUe4Port(zmqPort);
     * server.store();
     * response.setSuccess(true);
     * }
     * else if (ipServers.size() > 1)
     * {
     * log.info("Server could not be registered by IP: More than one server with that ip & port {}:{}", ip, port);
     * response.setError("Too many servers with that IP found.");
     * }
     * else
     * {
     * log.info("Server could not be registered by IP: No server with that ip & port {}:{}", ip, port);
     * response.setError("Server not found.");
     * }
     * }
     * }
     * catch (exception e)
     * {
     * log.info("Error registering by ip {}->{}:{}", new Object[]
     * { id, ip, port }, e);
     * response.setError("Error registering server");
     * }
     * return response;
     * }
     */

    @ResponseBody
    @RequestMapping(value = {
        "/refresh", "/refresh_pilot"}, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @Retryable(backoff = @Backoff(delay = 500))
    public JSONResponse refreshSession(@RequestParam(value = "pil_token", required = false) String token, @RequestParam(value = "srv_id",
        required = false) String id, @RequestParam(value = "mch_id", required = false) String mchId, @ModelAttribute("ip") String ip) {

        log.trace("Received request to refresh session {}:{}:{}", token, id, ip);
        JSONResponse response = new JSONResponse();
        try {
            // Refresh pilot session
            if (StringUtils.isNotBlank(token)) {
                PilotRecord pilot = getPilots().findByToken(token);
                if (pilot != null) {
                    if (!ip.equals(pilot.getPilLastIp())) {
                        log.warn("Pilot session changed IP {}/{}: {}->{}...", token, pilot.getPilCallsign(), pilot.getPilLastIp(), ip);
                        pilot.setPilLastIp(ip);
                    }
                    pilot.setPilLastPing(nowTS());
                    pilot.store();
                    response = new RefreshResponse(token, pilot.getPilLeaveReason());
                    response.setSuccess(true);
                    log.trace("Pilot successfully refreshed session {}...", pilot.getPilCallsign());
                } else {
                    log.info("No pilot currently using this token: {} from ip {}", new Object[] {
                        token, ip});
                    response.setError("Session not found.");
                }
            }
            // Refresh server session
            if (StringUtils.isNotBlank(id)) {
                Map<String, LiveServer> liveServers = hazelcast.getMap(HazelCastConfiguration.LIVE_SERVERS_MAP_NAME);
                LiveServer liveServer = liveServers.get(id);
                if (liveServer != null) {
                    if (ip.equals(liveServer.getServer().getSrvIp())) {
                        liveServer.setLastUpdate(LocalDateTime.now());
                        liveServers.put(id, liveServer);
                        log.trace("Server {} successfully refreshed session", liveServer.getServer().getSrvAlias());
                        response = new RefreshResponse(liveServer.getServer().getSrvAlias(), null);
                        response.setSuccess(true);
                    } else {
                        log.error("Wrong ip for server session refreshing {}: {}!={}...", id, ip, liveServer.getServer().getSrvIp());
                        response.setError("Server session could not be refreshed, IP session might be lost");
                    }
                }
                // If it is a pilot refreshing a launched dedicated, it might be gone
                // already
                else if (StringUtils.isBlank(token)) {
                    ServerRecord server = getServers().findById(id);
                    if (server != null) {
                        server.setSrvStatus(ServersDB.SERVER_ACTIVE_STATUS);
                        server.setSrvLastUse(getDataProvider().getNow());
                        server.store();
                        if (StringUtils.isNotBlank(mchId)) {
                            MatchRecord match = getMatches().findById(mchId);
                            if (match != null) {
                                log.info("Server match reactivated: {}/{}-{}", server.getSrvAlias(), match.getMchMapId(), match.getMcue4mId());
                                getMatches().reactivateMatch(match);
                            }
                        }
                        liveServer = new LiveServer(server.into(Server.class), LocalDateTime.now());
                        liveServers.put(server.getSrvId(), liveServer);
                        //
                        log.info("{} added to the list of active servers after losing the session", server.getSrvAlias());
                    } else {
                        log.debug("No server with that id {} found, from ip {}", new Object[] {
                            id, ip});
                        response.setError("Server not found.");
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error refreshing session", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response.setError("Error refreshing session");
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/check_steam_id", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @Retryable(backoff = @Backoff(delay = 500))
    public JSONResponse checkSteamAccount(@RequestParam(value = "steam_id") String ticket, @ModelAttribute("ip") String ip) {
        JSONResponse response = new JSONResponse();
        if (steamApiClient != null) {
            try {
                // Obtain the steam id from the session ticket
                AuthenticateUserTicketRS authenticateUserTicketRS = steamApiClient.checkSteamSession(ticket);
                final SteamApiSessionResponse reponseParams = authenticateUserTicketRS.getResponse().getParams();
                if (reponseParams != null) {
                    //
                    if ("OK".equals(reponseParams.getResult())) {
                        if (reponseParams.getOwnerSteamId().equals(reponseParams.getSteamId())) {

                            CheckAppOwnershipRS checkAppOwnershipRS = steamApiClient.checkBaseGameOwnership(reponseParams.getOwnerSteamId());
                            if (checkAppOwnershipRS.ownsGame()) {
                                UsersRecord user = getPilots().findUserFromSteamId(checkAppOwnershipRS.getAppownership().getOwnerSteamId());
                                if (user != null) {
                                    response = new SteamCheckResponse(user.getEmail());
                                    response.setSuccess(true);
                                }
                            }
                        } else {
                            log.error("Steam user not valid:  he is not owner={}/player={}", reponseParams.getOwnerSteamId(),
                                reponseParams.getSteamId());
                        }
                    } else {
                        log.error("Steam user not valid: Result not OK/{}", authenticateUserTicketRS.getError().getCode(), authenticateUserTicketRS
                            .getError().getDescription());
                    }
                } else if (authenticateUserTicketRS.getResponse().getError() != null) {
                    log.error("Steam user not valid: {}/{}", authenticateUserTicketRS.getResponse().getError().getCode(), authenticateUserTicketRS
                        .getResponse().getError().getDescription());
                }
            } catch (Exception e) {
                response.setError("Error checking steam id.");
            }
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/register_steam_player", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @Retryable(backoff = @Backoff(delay = 500))
    public JSONResponse registerSteamPlayer(@RequestParam(value = "usu_email") String usuEmail,
        @RequestParam(value = "usu_password") String usuPassword, @RequestParam(value = "pil_callsign") String pilCallsign, @RequestParam(
            value = "steam_id") String ticket, @RequestParam(value = "version") String version,
        @RequestParam(value = "encrypt", required = false) String rememberMe, @ModelAttribute("ip") String ip) {
        JSONResponse response = new JSONResponse();
        if (steamApiClient != null) {
            try {
                // Obtain the steam id from the session ticket
                AuthenticateUserTicketRS authenticateUserTicketRS = steamApiClient.checkSteamSession(ticket);
                final SteamApiSessionResponse reponseParams = authenticateUserTicketRS.getResponse().getParams();
                if (reponseParams != null) {
                    //
                    if ("OK".equals(reponseParams.getResult())) {
                        if (reponseParams.getOwnerSteamId().equals(reponseParams.getSteamId())) {
                            String steamId = reponseParams.getOwnerSteamId();
                            UsersRecord user = getPilots().findUserFromSteamId(steamId);
                            if (user != null) {
                                response.setError("Steam user already registered");
                            } else {
                                UsersRecord emailUser = getUsers().findByEmail(usuEmail);
                                if (emailUser != null) {
                                    response.setError("Email already registered");
                                } else {
                                    Triple<UsersRecord, ProfilesRecord, PilotRecord> newData =
                                        getUsers().insertUserAndProfile(usuEmail, usuPassword, pilCallsign, steamId);
                                    if (newData != null) {
                                        UsersRecord newUser = newData.getLeft();
                                        PilotRecord pilot = newData.getRight();
                                        updatePilotVisit(version, rememberMe, ip, pilot);
                                        response =
                                            new LoginResponse(pilot.getPilToken(), pilot.getPilCallsign(), newUser.getEmail(),
                                                pilot.getPilRmbrToken(), pilot.getPilDisableChat(), pilot.getPilDisableRequests(), "default",
                                                pilot.getPilDefaultScheme(), pilot.getPilUseCustomScheme() != null ? pilot.getPilUseCustomScheme()
                                                    : false);
                                        response.setSuccess(true);
                                    } else {
                                        response.setError("Error registering user. No data created");
                                    }
                                }
                                //response.setSuccess(false);
                            }
                        } else {
                            log.error("Steam user cannot register the game:  he is not owner={}/player={}", reponseParams.getOwnerSteamId(),
                                reponseParams.getSteamId());
                            response.setError("Steam user cannot register the game: 003");
                        }
                    } else {
                        log.error("Steam user cannot register the game: Result not OK/{}", authenticateUserTicketRS.getError().getCode(),
                            authenticateUserTicketRS.getError().getDescription());
                        response.setError("Steam user cannot register the game: 001");
                    }
                } else if (authenticateUserTicketRS.getResponse().getError() != null) {
                    log.error("Steam user cannot register the game: {}/{}", authenticateUserTicketRS.getResponse().getError().getCode(),
                        authenticateUserTicketRS.getResponse().getError().getDescription());
                    response.setError("Steam user cannot register the game: 001");
                }

            } catch (ApiException e) {
                log.error("Steam user could not be registered. Error communicating with steam", e);
                response.setError("Steam user could not be registered");
            }
        }

        //Ugh - Vince		
        /*
         * catch (exception e) {
         * log.error("Error registering new user", e);
         * response.setError("Error registering user.");
         * }
         */
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/check_pilot", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @Retryable(backoff = @Backoff(delay = 500))
    public JSONResponse checkPilot(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password,
        @RequestParam(value = "version") String version, @RequestParam(value = "encrypt", required = false) String rememberMe, @RequestParam(
            value = "steam_id", required = false) String ticket, @ModelAttribute("ip") String ip) {
        log.debug("Checking {}:{}:{}:{}", username, version, rememberMe, ip);
        JSONResponse response = new JSONResponse();
        try {
            Triple<UsersRecord, ProfilesRecord, PilotRecord> pair = getUsers().findUserAndPilot(username);
            if (pair != null) {
                UsersRecord user = pair.getLeft();
                ProfilesRecord profile = pair.getMiddle();
                PilotRecord pilot = pair.getRight();
                // TODO Verify that the user is not logged in more than once
                String steamId = null;
                if (getUsers().validates(user, password)
                    || (pilot != null && StringUtils.isNotBlank(rememberMe) && rememberMe.equals(pilot.getPilRmbrToken()))) {
                    if (StringUtils.isBlank(user.getSteamId()) && StringUtils.isNotBlank(ticket) && steamApiClient != null) {
                        try {
                            // Obtain the steam id from the session ticket
                            AuthenticateUserTicketRS authenticateUserTicketRS = steamApiClient.checkSteamSession(ticket);
                            final SteamApiSessionResponse reponseParams = authenticateUserTicketRS.getResponse().getParams();
                            if (reponseParams != null) {
                                //
                                if ("OK".equals(reponseParams.getResult())) {
                                    steamId = reponseParams.getOwnerSteamId();
                                    if (steamId.equals(reponseParams.getSteamId())) {
                                        CheckAppOwnershipRS checkAppOwnershipRS = steamApiClient.checkBaseGameOwnership(steamId);
                                        if (checkAppOwnershipRS.ownsGame()) {
                                            UsersRecord registeredUser = getPilots().findUserFromSteamId(steamId);
                                            if (registeredUser == null) {
                                                log.info("Associating steam account {} with user {}", steamId, username);
                                                try {
                                                    user.setSteamId(steamId);
                                                    user.store();
                                                } catch (Exception e) {
                                                    log.error("Error registering steam id {} for username {}: {}", steamId, user.getEmail(),
                                                        e.getMessage());
                                                }
                                            } else if (registeredUser.getEmail().equalsIgnoreCase(username)) {
                                                log.info("Steam account {} was already registered with user {}", steamId, username);
                                            } else {
                                                log.error("Steam account {} trying to be registered by {} was already registered with user {}",
                                                    steamId, username, registeredUser.getEmail());
                                                response.setError("A user account is already associated with this Steam account");
                                            }
                                        } else {
                                            log.error("Steam account {}-{} does not own the basic game {} ", steamId, username,
                                                checkAppOwnershipRS.getAppownership());
                                            response.setError("Steam ownership problem");
                                        }
                                    } else {
                                        response.setError("A valid Steam purchase has not been detected for this Steam account.");
                                        log.error("Steam user cannot validate the user:  he is not owner={}/player={}", steamId,
                                            reponseParams.getSteamId());
                                    }
                                } else {
                                    response.setError("A valid Steam purchase has not been detected for this Steam account: "
                                        + authenticateUserTicketRS.getError().getCode());
                                    log.error("Steam user cannot validate the user: Result not OK/{}", authenticateUserTicketRS.getError().getCode(),
                                        authenticateUserTicketRS.getError().getDescription());
                                }
                            } else if (authenticateUserTicketRS.getResponse().getError() != null) {
                                response.setError("A valid Steam purchase has not been detected for this Steam account: "
                                    + authenticateUserTicketRS.getResponse().getError().getDescription());
                                log.error("Steam user cannot validate the user: {}/{}", authenticateUserTicketRS.getResponse().getError().getCode(),
                                    authenticateUserTicketRS.getResponse().getError().getDescription());
                            }
                        } catch (ApiException e) {
                            log.error("ApiException obtaining steam if", e);
                        } catch (Exception e) {
                            log.error("Error obtaining steam id", e);
                        }
                    }
                    if (StringUtils.isBlank(response.getError())) {
                        // Check that the user has a valid steamId or NDA and
                        boolean validUser = false;
                        if (user.getAlpha() && user.getNda()) {
                            validUser = true;
                        } else if (StringUtils.isNotBlank(steamId)) {
                            log.info("Activated user {} with steam account {}", username, steamId);
                            validUser = true;
                            user.setAlpha(true);
                            user.setNda(true);
                            user.setSteamId(steamId);
                            user.store();
                        }
                        if (validUser) {
                            // If the user still has no pilot, create it
                            // For those logging for the first time
                            if (pilot == null) {
                                pilot = getUsers().createPilotFromUserAndProfile(profile, user);
                            } else {
                                pilot.setPilCallsign(profile.getCallsign());
                            }
                            // Let's verify if the user has had all the packs registered as orders
                            getUsers().verifySteamPacks(user.getId());
                            //
                            updatePilotVisit(version, rememberMe, ip, pilot);
                            response =
                                new LoginResponse(pilot.getPilToken(), pilot.getPilCallsign(), user.getEmail(), pilot.getPilRmbrToken(),
                                    pilot.getPilDisableChat(), pilot.getPilDisableRequests(), pilot.getPilUthId(), pilot.getPilDefaultScheme(),
                                    pilot.getPilUseCustomScheme() != null ? pilot.getPilUseCustomScheme() : false);
                            response.setSuccess(true);
                            log.info("{}/{}/{} logged in {}:{}:{}", username, profile.getCallsign(), pilot.getPilToken(), version, rememberMe, ip);
                        } else {
                            log.error("Invalid user, no NDA/BETA and no Steam id {}:{}:{}:{}:{}", username, ticket, version, rememberMe, ip);
                            response.setError("Account not authorised, no Steam credentials recognised");
                        }
                    }
                } else {
                    log.info("Wrong username/password for {}:{}:{}:{}", username, version, rememberMe, ip);
                    response.setError("Wrong username/password");
                }
            } else {
                log.info("Username does not exist {}:{}:{}:{}", username, version, rememberMe, ip);
                response.setError("No authorised account found with that email. Verify your data and try again or contact support@heavygear.com");
            }
        } catch (Exception e) {
            log.info("Error checking user {}:{}:{}:{}", username, version, rememberMe, ip, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response.setError("Error checking user");
        }
        return response;
    }

    private void updatePilotVisit(String version, String rememberMe, String ip, PilotRecord pilot) {
        // Set last ip and login
        pilot.setPilLastIp(ip);
        pilot.setPilLastLogin(nowTS());
        pilot.setPilLastVersion(version);
        //
        pilot.setPilLastPing(nowTS());
        // Set a session token
        pilot.setPilToken(newUUID());
        // Prepare the response
        // Create a remember me token if necessary
        if (StringUtils.isNotBlank(rememberMe)) {
            pilot.setPilRmbrToken(newUUID());
        }
        if (pilot.getPilUthId() == null) {
            pilot.setPilUthId("default");
        }
        pilot.store();
    }

    @ResponseBody
    @RequestMapping(value = "/gi/rest/paintschemes/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaintScheme> viewPaintScheme(@PathVariable String id) {
        PaintSchemeRecord instance = getPaintSchemesDB().findById(id);
        if (instance != null) {
            return new ResponseEntity<>(instance.into(PaintScheme.class), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    public static String removeNull(Object nullCheck) {
        if (nullCheck == null) {
            return "";
        } else {
            return nullCheck.toString();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/serverip", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public String serverip(HttpServletRequest requestContext) {
        log.debug("Finding my IP");
        String ipAddress = requestContext.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = requestContext.getRemoteAddr();
        }
        return ipAddress;
    }
}
