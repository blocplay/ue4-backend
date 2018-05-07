package com.tokenplay.ue4.be.zeromq;

import java.util.Map;

import com.tokenplay.ue4.logic.DataProvider;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;

public abstract class ServerCommand {
    public abstract Map<String, Object> prepareRequest(ServerRecord server, Map params, DataProvider dataProvider);
}
