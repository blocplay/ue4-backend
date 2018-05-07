package com.tokenplay.ue4.be.zeromq;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import com.hazelcast.core.HazelcastInstance;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.configuration.HazelCastConfiguration;
import com.tokenplay.ue4.discord.DiscordConfiguration.DiscordAgent;
import com.tokenplay.ue4.logic.DataProvider;
import com.tokenplay.ue4.logic.TaskManager;

@Component
@Slf4j
@ConfigurationProperties(prefix = "comms")
@Data
public class CommsServerBean {
    private static final int ZMQ_THREADS = 1;

    @Autowired
    HazelcastInstance hazelcast;

    @Autowired
    TaskManager taskManager;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    DataProvider dataProvider;

    @Autowired(required = false)
    DiscordAgent discordAgent;

    private String broker;

    private ExecutorService pool;

    private List<CommsServerReceiver> workerList;

    @PostConstruct
    public void init() {
        //
        log.debug("Initialising map of alive servers...");
        hazelcast.getMap(HazelCastConfiguration.LIVE_SERVERS_MAP_NAME);
        log.debug("... map initalised!");
        //
        log.debug("Initialising ZeroMQ communications... {} thread(s)", CommsServerBean.ZMQ_THREADS);
        pool = Executors.newFixedThreadPool(CommsServerBean.ZMQ_THREADS);
        workerList = new ArrayList<>(CommsServerBean.ZMQ_THREADS);
        for (int i = 0; i < CommsServerBean.ZMQ_THREADS; i++) {
            CommsServerReceiver csr = new CommsServerReceiver(transactionManager, dataProvider, taskManager, hazelcast, broker);
            workerList.add(csr);
            pool.execute(csr);
        }
    }

    @PreDestroy
    public void clean() {
        log.debug("Cleaning up ZeroMQ communications");
        for (CommsServerReceiver csr : workerList) {
            log.debug("Asking CommsServerReceiver to stop");
            csr.askServerToStop();
        }
        pool.shutdownNow();
    }
}
