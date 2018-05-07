package com.tokenplay.ue4.www.api;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class ServerStatusDataList {
    Map<String, ServerStatusData> servers = new HashMap<>();
}
