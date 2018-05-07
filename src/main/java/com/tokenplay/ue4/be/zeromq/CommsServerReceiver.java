package com.tokenplay.ue4.be.zeromq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.logic.DataProvider;
import com.tokenplay.ue4.logic.TaskManager;
import com.tokenplay.ue4.logic.TaskManager.CommsAction;
import com.tokenplay.ue4.tasks.ActivityChecker;
import com.tokenplay.ue4.www.controllers.GameAPI;

@Slf4j
@Data
public class CommsServerReceiver implements Runnable {

    public static final String BROKER_PROPERTY = "CommsServerReceiver.zeromq.broker";
    private final TaskManager taskManager;

    enum CommsWork {
        ServerStarted,
        SignalLogin,
        SignalGameCreated,
        SignalGameFinished,
        SignalMatchStarted,
        SignalMatchFinished,
        UpdateMatchBots,
        SignalLeaveServer,
        SignalLogout,
        GetGearDefinition,
        SignalEvent,
        SignalTecAddAction;
    }

    private boolean goOn = true;

    private boolean running = false;

    private String brokerAddress = null;

    // private SqlSessionFactory sessionFactory;

    private ObjectMapper mapper = new ObjectMapper();

    final TransactionTemplate transactionTemplate;

    final DataProvider dataProvider;

    final HazelcastInstance hazelcast;

    final Map<CommsWork, TaskManager.CommsAction> actions = new HashMap<>();

    public CommsServerReceiver(PlatformTransactionManager transactionManager, DataProvider dataProvider, TaskManager taskManager,
        HazelcastInstance hazelcast, String broker) {
        if (StringUtils.isBlank(brokerAddress)) {
            brokerAddress = broker;
        } else if (StringUtils.isNotBlank(System.getProperty(BROKER_PROPERTY))) {
            brokerAddress = System.getProperty(BROKER_PROPERTY);
        }
        transactionTemplate = new TransactionTemplate(transactionManager);
        this.dataProvider = dataProvider;
        this.hazelcast = hazelcast;
        this.taskManager = taskManager;
        //
        actions.put(CommsWork.ServerStarted, this.taskManager.getServerStartedAction());
        actions.put(CommsWork.SignalLogin, this.taskManager.getSignalLoginAction());
        actions.put(CommsWork.SignalGameCreated, this.taskManager.getSignalGameCreatedAction());
        actions.put(CommsWork.SignalGameFinished, this.taskManager.getSignalGameFinished());
        actions.put(CommsWork.SignalMatchStarted, this.taskManager.getSignalMatchStartedAction());
        actions.put(CommsWork.SignalMatchFinished, this.taskManager.getSignalMatchFinishedAction());
        actions.put(CommsWork.UpdateMatchBots, this.taskManager.getUpdateMatchBotsAction());
        actions.put(CommsWork.SignalLeaveServer, this.taskManager.getSignalLeaveServerAction());
        actions.put(CommsWork.SignalLogout, this.taskManager.getSignalLogoutAction());
        actions.put(CommsWork.GetGearDefinition, this.taskManager.getGetGearDescriptionAction());
        actions.put(CommsWork.SignalEvent, this.taskManager.getEventReceived());
        actions.put(CommsWork.SignalTecAddAction, this.taskManager.getSignalTecAddAction());
        log.info("Added actions to CommsWork list");
    }

    @Override
    public void run() {
        Context context = ZMQ.context(1);
        if (brokerAddress != null) {
            try {
                Socket socket = ActivityChecker.getSocket(context, ZMQ.REP);
                // Listen on the specified port until we are told to stop
                if (!brokerAddress.startsWith("tcp://*")) {
                    log.info("Connecting to broker on {}", brokerAddress);
                    socket.connect(brokerAddress);
                } else {
                    log.info("Publishing server at {}", brokerAddress);
                    socket.bind(brokerAddress);
                }
                running = true;
                while (goOn) {
                    try {
                        log.trace("Waiting for messages...");
                        byte[] messageReceived = socket.recv(0);
                        if (messageReceived != null) {
                            String msg = new String(messageReceived, "UTF-8");
                            log.debug("Message received {}", msg);
                            // if we have not received a request to stop...
                            if (goOn) {
                                log.debug("Processing request");
                                Map requestData = null;
                                Map responseData = null;
                                try {
                                    requestData = mapper.readValue(msg, Map.class);
                                } catch (IOException e) {
                                    log.error("Error processing request, bad format!: {}", msg, e);
                                    responseData = TaskManager.getErrorResponse(e);
                                }
                                if (requestData != null) {
                                    String requestType = (String) requestData.get("Type");
                                    CommsWork work = null;
                                    try {
                                        work = CommsWork.valueOf(requestType);
                                    } catch (Exception e) {
                                        log.error("Error processing request, unknown request type!: {}", requestType);
                                        responseData = TaskManager.getErrorResponse(e);
                                    }
                                    if (work != null) {
                                        final Map request = (Map) requestData.get("Content");
                                        final CommsWork taskWork = work;
                                        final Map<String, Object> taskResponse = new HashMap<>();
                                        try {
                                            boolean keepTrying = true;
                                            int numAttempts = 0;
                                            Exception lastEx = null;
                                            while (keepTrying && numAttempts < GameAPI.NUM_RETRIES) {
                                                numAttempts++;
                                                lastEx = null;
                                                try {
                                                    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                                                        @Override
                                                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                                                            CommsAction action = actions.get(taskWork);
                                                            if (action != null) {
                                                                action.perform(request, taskResponse, dataProvider, hazelcast);
                                                            } else {
                                                                log.error("Task received ({}) but no action configured", taskWork);
                                                            }
                                                        }
                                                    });
                                                    responseData = taskResponse;
                                                    keepTrying = false;
                                                } catch (Exception e) {
                                                    log.error("Error processing request ({}), retrying...", e.getMessage());
                                                    lastEx = e;
                                                    Thread.sleep(200);
                                                }
                                            }
                                            if (keepTrying) {
                                                log.error("Error processing request {}", taskWork.name(), lastEx);
                                                responseData = TaskManager.getErrorResponse(lastEx);
                                            }
                                        } catch (Exception e) {
                                            log.error("Error processing request {}", taskWork.name(), e);
                                            responseData = TaskManager.getErrorResponse(e);
                                        }
                                    }
                                }
                                String response = mapper.writeValueAsString(responseData);
                                log.debug("Sending response: {}", response);
                                // socket.send(response.getBytes("UTF-8"), 0);
                                socket.send(response, 0);
                            } else if ("STOP".equals(msg)) {
                                log.debug("Stopping server as requested");
                                socket.send("STOP_OK", 0);
                            } else {
                                log.debug("Stopping server as requested");
                            }
                        }
                    } catch (Exception e) {
                        log.error("Server communications error, stopping...", e);
                        goOn = false;
                    }
                }
                if (socket != null) {
                    socket.close();
                }
            } finally {
                context.term();
            }
        } else {
            log.error("Broker address not specified {} property is required.", BROKER_PROPERTY);
        }
        log.info("Server stopped");
    }

    public boolean askServerToStop() {
        boolean performed = false;
        goOn = false;
        if (running) {
            log.debug("Asking server to stop...");
            Context context = ZMQ.context(1);
            Socket socket = ActivityChecker.getSocket(context, ZMQ.REQ);
            try {
                log.debug("Connecting to broker at {}", brokerAddress);
                socket.connect(brokerAddress);
                log.debug("Sending a stop command...");
                socket.send("STOP", 0);
                log.debug("Stop command sent. Waiting for response...");
                socket.recv(0);
                log.debug("Response to stop command received!");
                performed = true;
            } catch (Exception e) {
                log.error("Server could not be signaled to stop", e);
            } finally {
                if (socket != null) {
                    socket.close();
                }
                context.term();
            }
        } else {
            log.debug("Server already stopped");
            // Already stopped
            performed = true;
        }
        return performed;
    }

    public boolean isRunning() {
        return running;
    }
}
