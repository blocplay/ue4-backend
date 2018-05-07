package com.tokenplay.ue4.tasks;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.configuration.BackendConfiguration;
import com.tokenplay.ue4.logic.DataProvider;

@Slf4j
@Data
@Component
public class CreditChecker {
    @Autowired
    private HazelcastInstance hazelcast;

    @Autowired
    BackendConfiguration backendConfiguration;

    @Autowired
    private DataProvider dataProvider;

    @Autowired
    private PlatformTransactionManager transactionManager;

    public static DateTimeFormatter DTF = DateTimeFormatter.ofPattern("MMM-dd-yyyy HH:mm:ss", Locale.ENGLISH);

    public static SimpleDateFormat SDF = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss z", Locale.ENGLISH);

    @Scheduled(fixedDelay = 60_000, initialDelay = 30_000)
    protected void execute() {
        if (backendConfiguration.isTasksEnabled()) {
            // We will just execute the task in the first member of the cluster, in
            // case there is more than one
            Cluster cluster = this.hazelcast.getCluster();
            if (cluster.getMembers().iterator().next().localMember()) {
                log.debug("Performing task in this cluster node {}...", this.hazelcast.getCluster().getLocalMember().getUuid());
                try {
                    final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                            checkCredit();
                        }
                    });
                } catch (Exception e) {
                    log.error("Error providing credit {}", e);
                }
            }
        } else {
            log.debug("Background tasks are disabled, nothing to do");
        }
    }

    /**
     * Provides Gears to all pilots that are missing some
     */
    private void checkCredit() {
        log.debug("Providing pilots with TEC who are missing credit");
        try {
            dataProvider.provideCredit();
        } catch (Exception e) {
            log.error("Providing credit for pilots {}", e);
        }
    }

}
