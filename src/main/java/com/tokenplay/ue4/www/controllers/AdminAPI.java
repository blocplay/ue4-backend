package com.tokenplay.ue4.www.controllers;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.tokenplay.ue4.configuration.HazelCastConfiguration;
import com.tokenplay.ue4.model.db.tables.pojos.Server;
import com.tokenplay.ue4.www.api.ClusterMembersResponse;
import com.tokenplay.ue4.www.api.ClusterPopulationResponse;
import com.tokenplay.ue4.www.api.JSONResponse;
import com.tokenplay.ue4.www.caching.LiveServer;

@Data
@EqualsAndHashCode(callSuper = false)
@RestController
@RequestMapping(value = "/admin")
public class AdminAPI extends CommonAPI {
    @Autowired
    HazelcastInstance hazelcast;

    @RequestMapping(value = "/cluster/info", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public JSONResponse clusterInfo() {
        JSONResponse response = new JSONResponse();
        if (hazelcast != null && CollectionUtils.isNotEmpty(hazelcast.getCluster().getMembers())) {
            //@formatter:off
            response = new ClusterMembersResponse(
                    hazelcast
                        .getCluster()
                        .getMembers()
                        .stream()
                        .map(Member::getSocketAddress)
                        .map(address -> address.getHostString() + ":" + address.getPort())
                        .collect(Collectors.toList()));
            //@formatter:on
            response.setSuccess(true);
        }
        return response;
    }

    @RequestMapping(value = "/cluster/population", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public JSONResponse clusterPopulation() {
        JSONResponse response = new JSONResponse();
        if (hazelcast != null) {
            Map<String, LiveServer> liveServers = hazelcast.getMap(HazelCastConfiguration.LIVE_SERVERS_MAP_NAME);
            //@formatter:off
            Map<String,Long> serversByZone = 
                liveServers
                    .values()
                    .stream()
                        .map(LiveServer::getServer)
                        .collect(Collectors.groupingBy(Server::getSrvZone, Collectors.counting()));
            //@formatter:on
            response = new ClusterPopulationResponse(serversByZone);
            response.setSuccess(true);
        }
        return response;
    }
}
