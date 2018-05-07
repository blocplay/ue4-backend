package com.tokenplay.ue4.www.controllers;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.tokenplay.ue4.model.repositories.*;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.be.zeromq.ServerTasks.ServerWork;
import com.tokenplay.ue4.logic.DataProvider;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;
import com.tokenplay.ue4.www.api.JSONResponse;

@Data
@EqualsAndHashCode(callSuper = false)
@Controller
@Transactional
@Slf4j
public class CommonAPI {

    public static String prefix = "tcp://";
    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private DSLContext jooq;

    @Autowired
    private DataProvider dataProvider;

    @Autowired
    private UsersDB users;

    @Autowired
    private PilotsDB pilots;

    @Autowired
    private ParticipationsDB participations;

    @Autowired
    private ServersDB servers;

    @Autowired
    private MatchesDB matches;

    @Autowired
    private UiThemesDB themes;

    @Autowired
    private GearModelsDB gearModelsDB;

    @Autowired
    private GearSectionsDB gearSectionsDB;

    @Autowired
    private GearInstancesDB gearInstancesDB;

    @Autowired
    private InventoryLocationsDB inventoryLocationsDB;

    @Autowired
    private InventoryObjectsDB inventoryObjectsDB;

    @Autowired
    private InventoryInstancesDB inventoryInstancesDB;

    @Autowired
    private PaintSchemesDB paintSchemesDB;

    @Autowired
    private MapCycleDB mapCyclesDB;

    @Autowired
    private AccountsDB accountsDB;

    @Autowired
    private AssetsDB assetsDB;

    @Autowired
    CorporationsDB corporationsDB;

    @Autowired
    private EquipmentDB equipmentDB;

    @Autowired
    private GearsDB gearDB;

    @Autowired
    private OrderDB orderDB;

    @Autowired
    private PlanetsDB planetsDB;

    @Autowired
    private StarsDB starsDB;

    @Autowired
    private TransactionsDB transactionsDB;

    @Autowired
    private ChampionshipsDB championshipsDB;

    @Autowired
    private MatchSessionsDB matchSessionsDB;

    @Autowired
    private MatchSessionParticipantsDB matchSessionParticipantsDB;

    @Autowired
    private MessagesDB messagesDB;

    public static String newUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public OffsetDateTime nowTS() {
        return servers.getNow();
    }

    @ModelAttribute("ip")
    public String populateIp(HttpServletRequest request) {
        String fwdIp = request.getHeader("X-Forwarded-For");
        return (fwdIp != null) ? fwdIp : request.getRemoteAddr();
    }

    public JSONResponse performServerWork(ServerRecord server, Map params, ServerWork work) {
        JSONResponse resultJsonResponse = new JSONResponse();
        if (server != null && ServersDB.SERVER_ACTIVE_STATUS.equals(server.getSrvStatus()) && server.getSrvVisible()) {
            Context context = null;
            ZMQ.Socket socket = null;
            try {
                context = ZMQ.context(1);
                socket = context.socket(ZMQ.REQ);
                socket.setSendTimeOut(5000);
                socket.setReceiveTimeOut(5000);
                socket.setLinger(0);
                log.debug("Connecting to server {} ({}{})...", new Object[] {
                    server.getSrvAlias(), prefix, servers.getSrvZMQAddress(server)});
                socket.connect(prefix + servers.getSrvZMQAddress(server));
                log.debug("Sending command to server {}...", server.getSrvAlias());
                Map<String, Object> requestMap = work.getCommand().prepareRequest(server, params, dataProvider);
                if (requestMap != null) {
                    requestMap.put("Type", work.name());
                    String requestData = mapper.writeValueAsString(requestMap);
                    log.debug("Sending message... {} to {}", requestData, server.getSrvAlias());
                    socket.send(requestData, 0);
                    log.debug("Message sent to server {}. Waiting for response...", server.getSrvAlias());
                    byte[] messageReceived = socket.recv(0);
                    if (messageReceived != null) {
                        String msg = new String(messageReceived, "UTF-8");
                        log.debug("Response to command received from server {}. Performing some action with it: {}", server.getSrvAlias(), msg);
                        // Doing something with the response
                        try {
                            Map responseData = mapper.readValue(msg, Map.class);
                            log.debug("Processing response {} to server", msg, server.getSrvAlias());
                            resultJsonResponse = work.getProcessor().perform(server, params, requestMap, responseData, dataProvider);
                        } catch (Exception e) {
                            log.error("Error processing response, bad format!: {}", msg, e);
                            resultJsonResponse.setError("Error processing response, bad format!" + e.getMessage());
                        }
                    }
                } else {
                    log.error("Error preparing request for command {}: request null", work.name());
                    resultJsonResponse.setError("ErrorPreparingRequest, data is null");
                }
            } catch (Exception e) {
                log.error("Error sending message", e);
                resultJsonResponse.setError("Error sending message" + e.getMessage());
            } finally {
                if (socket != null) {
                    socket.close();
                }
                if (context != null) {
                    context.term();
                }
            }
        }
        return resultJsonResponse;
    }

}
