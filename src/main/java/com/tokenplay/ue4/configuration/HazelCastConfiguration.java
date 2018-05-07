package com.tokenplay.ue4.configuration;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.spring.context.SpringManagedContext;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.tasks.LeaderboardUpdater;
import com.tokenplay.ue4.www.api.ClusterMembersResponse;

@Configuration
@ConfigurationProperties(prefix = "hazelcast")
@Data
@Slf4j
public class HazelCastConfiguration implements ApplicationContextAware {
    public static final String LIVE_SERVERS_MAP_NAME = "LiveServersMap";
    public static final String LEADERBOARD_MAP_NAME = "LeaderboardMap";
    public static final List<String> HAZELCAST_MAPS = Arrays.asList(LIVE_SERVERS_MAP_NAME, LEADERBOARD_MAP_NAME);


    public static final int MULTICAST_PORT = 46322;
    private static final int DEFAULT_PORT = 4600;

    private boolean multicast;
    private int port = DEFAULT_PORT;
    private String ip;
    private URL clusterInfo = null;

    @Autowired
    ApplicationContext applicationContext;

    @Bean(name = "hazelcast")
    public HazelcastInstance buildHazelcast() throws UnknownHostException {
        log.info("Hazelcast initialization...");
        Config cfg = new XmlConfigBuilder().build();
        cfg.setProperty("hazelcast.logging.type", "slf4j");
        //
        final SpringManagedContext smc = new SpringManagedContext();
        smc.setApplicationContext(applicationContext);
        cfg.setManagedContext(smc);

        final NetworkConfig network = cfg.getNetworkConfig();
        final JoinConfig join = network.getJoin();
        final MulticastConfig multicastConfig = join.getMulticastConfig();
        if (multicast) {
            log.info("Hazelcast using multicast at port {}", MULTICAST_PORT);
            multicastConfig.setEnabled(true);
            multicastConfig.setMulticastPort(MULTICAST_PORT);
            multicastConfig.setMulticastTimeoutSeconds(2);
            cfg.setProperty("hazelcast.initial.min.cluster.size", "1");
        } else {
            log.info("Hazelcast using unicast ");
            cfg.setProperty("hazelcast.socket.client.bind.any", "false");
            multicastConfig.setEnabled(false);
            join.getTcpIpConfig().setEnabled(true);
            network.setPort(port);
            final TcpIpConfig tcpIpConfig = join.getTcpIpConfig();
            tcpIpConfig.setEnabled(true);
            final String address;
            if (StringUtils.isNotBlank(System.getProperty("hostaddress"))) {
                address = System.getProperty("hostaddress");
                log.info("Using provided address {}", address);
            } else if (StringUtils.isNotBlank(ip)) {
                address = ip;
                log.info("Using provided ip {}", address);
            } else {
                address = InetAddress.getLocalHost().getHostAddress();
                log.info("Using Java localhost address {}", address);
            }
            log.info("Hazelcast being published at address {}:{}", address, port);
            network.setPublicAddress(address);
            network.getInterfaces().setEnabled(true).addInterface(address);
            tcpIpConfig.addMember(address);
            if (clusterInfo != null) {
                log.info("Seeding hazelcast cluster using url: {}", clusterInfo);
                try {
                    RestTemplate restTemplate = new RestTemplate();
                    ClusterMembersResponse clusterResponse = restTemplate.getForObject(clusterInfo.toString(), ClusterMembersResponse.class);
                    clusterResponse.getMembers().stream().filter(member -> !address.equals(member)).forEach(member -> {
                        log.info("Adding {} to cluster", member);
                        tcpIpConfig.addMember(member);
                    });
                } catch (Exception e) {
                    log.error("Error seeding cluster info: {}", e.getMessage());
                }
            }
        }

        HAZELCAST_MAPS.forEach(mapName -> {
            MapConfig mapConfig = cfg.getMapConfig(mapName);
            mapConfig.setStatisticsEnabled(false);
        });

        HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);
        log.info("Hazelcast Cluster Members: " + instance.getCluster().getMembers().size());
        //
        return instance;
    }

    @Bean
    public CacheInitialiser servletContextListenerAgent() {
        return new CacheInitialiser();
    }

    @Bean(name = LEADERBOARD_MAP_NAME)
    public IMap<String, LeaderboardUpdater.PilotStats> buildHotelReviewMap(@Named("hazelcast")
    final HazelcastInstance hazelcast) {
        return hazelcast.getMap(LEADERBOARD_MAP_NAME);
    }

}
