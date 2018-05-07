package com.tokenplay.ue4.be.zeromq;

import java.util.HashMap;
import java.util.Map;

import com.tokenplay.ue4.logic.DataProvider;
import com.tokenplay.ue4.logic.TaskManager;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;
import com.tokenplay.ue4.www.api.JSONResponse;
import com.tokenplay.ue4.www.api.ServerCommandResponse;

public class ServerTasks {

    final static ServerCommand SetMapCycleCommand = new ServerCommand() {

        @Override
        public Map<String, Object> prepareRequest(ServerRecord server, Map params, DataProvider dataProvider) {
            Map<String, Object> response = new HashMap<>();
            TaskManager.getMapCycleFromServer(response, server, dataProvider);
            return response;
        }

    };

    final static ServerCommand StopMapCommand = new ServerCommand() {

        @Override
        public Map<String, Object> prepareRequest(ServerRecord server, Map params, DataProvider dataProvider) {
            Map<String, Object> response = new HashMap<>();
            // Nothing to do right now
            return response;
        }

    };

    final static ResponseProcessor SetMapCycleProcessor = new ResponseProcessor() {
        @Override
        public JSONResponse perform(ServerRecord server, Map params, Map request, Map response, DataProvider dataProvider) {
            JSONResponse jsonResponse = new ServerCommandResponse(server.getSrvIp(), server.getSrvId(), server.getSrvAlias(), server.getSrvName());
            jsonResponse.setSuccess("Ok".equals(response.get("Result")));
            return jsonResponse;
        }
    };

    final static ResponseProcessor StopMapProcessor = new ResponseProcessor() {
        @Override
        public JSONResponse perform(ServerRecord server, Map params, Map request, Map response, DataProvider dataProvider) {
            JSONResponse jsonResponse = new ServerCommandResponse(server.getSrvIp(), server.getSrvId(), server.getSrvAlias(), server.getSrvName());
            jsonResponse.setSuccess("Ok".equals(response.get("Result")));
            return jsonResponse;
        }
    };

    public enum ServerWork {
        SetMapCycle(SetMapCycleCommand, SetMapCycleProcessor),
        StopMap(StopMapCommand, StopMapProcessor);

        // ////////////////////////////////////////////////////////////////////////////////////////
        // ////////////////////////////////////////////////////////////////////////////////////////
        // ////////////////////////////////////////////////////////////////////////////////////////

        private final ServerCommand command;
        private final ResponseProcessor processor;

        ServerWork(ServerCommand command, ResponseProcessor processor) {
            this.command = command;
            this.processor = processor;
        }

        public ServerCommand getCommand() {
            return command;
        }

        public ResponseProcessor getProcessor() {
            return processor;
        }
    }
}
