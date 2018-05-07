package com.tokenplay.ue4.be.zeromq;

import java.util.Map;

import com.tokenplay.ue4.logic.DataProvider;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;
import com.tokenplay.ue4.www.api.JSONResponse;

public abstract class ResponseProcessor {
    public abstract JSONResponse perform(ServerRecord server, Map params, Map request, Map response, DataProvider dataProvider);
}
