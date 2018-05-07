package com.tokenplay.ue4.steam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.steam.client.SteamApiClient;

@Configuration
@ConditionalOnProperty(prefix = "steam", name = "enabled", matchIfMissing = true)
@Data
@Slf4j
public class SteamApiConfiguration implements ApplicationContextAware {
    @Autowired
    ApplicationContext applicationContext;

    @Bean(destroyMethod = "close")
    public SteamApiClient buildSteamApiClient() {
        // 
        log.info("Initialising steam client");
        final SteamApiClient steamApiClient = new SteamApiClient();
        steamApiClient.init();
        return steamApiClient;
    }

}
