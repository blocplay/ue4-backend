package com.tokenplay.ue4.configuration;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Configuration
@ConfigurationProperties(prefix = "backend")
@Slf4j
@Data
public class BackendConfiguration implements EnvironmentAware {

    private Environment environment;

    private boolean tasksEnabled = true;

    @PostConstruct
    public void postConstruct() {
        log.info("Background tasks are {}abled", tasksEnabled ? "en" : "dis");
    }

}
