package com.tokenplay.ue4.discord;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.CommandHandler;
import de.btobastian.sdcf4j.handler.Discord4JHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.model.db.tables.pojos.Server;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

@Configuration
@ConfigurationProperties(prefix = "discord")
@ConditionalOnProperty(name = "discord.enabled", havingValue = "true", matchIfMissing = false)
@Data
@Slf4j
public class DiscordConfiguration implements ApplicationContextAware {
    private String token;
    private String channelId;
    private boolean serverNewsEnabled = false;
    private String championshipChannelId;
    private boolean serverChampionshipNewsEnabled = true;

    @Autowired
    ApplicationContext applicationContext;

    @Data
    public static class DiscordAgent implements Closeable {

        private IDiscordClient client;
        private final DiscordConfiguration discordConfiguration;

        private DiscordAgent(DiscordConfiguration discordConfiguration, List<Class> enabledCommands) {
            this.discordConfiguration = discordConfiguration;
            ClientBuilder clientBuilder = new ClientBuilder();// Creates the ClientBuilder instance
            clientBuilder.withToken(discordConfiguration.getToken());// Adds the login info to the builder
            try {
                client = clientBuilder.build();// Creates the client instance and logs the client in
                // Discord4J
                CommandHandler cmdHandler = new Discord4JHandler(client);
                client.getDispatcher().registerListener(new DiscordEventListener(discordConfiguration));
                enabledCommands.forEach(clazz -> {
                    try {
                        cmdHandler.registerCommand((CommandExecutor) clazz.newInstance());
                    } catch (Exception e) {
                        log.error("Could not register {} command", clazz.getName(), e);
                    }
                });
                client.login();
                log.debug("Discord client initiated login");
            } catch (DiscordException | RateLimitException e) {// This is thrown if there was a problem building the client
                log.error("Error connecting bot to discord", e);
            }
        }

        @Override
        public void close() throws IOException {
            if (client != null) {
                try {
                    client.logout();
                } catch (DiscordException e) {
                    log.error("Error login out from discord", e);
                }
            }
        }

        public void sendNewsMessage(Server server, String message) {
            log.debug("Sending news to discord ");
            if ((discordConfiguration.isServerChampionshipNewsEnabled() && StringUtils.isNoneEmpty(server.getSrvChaId(),
                discordConfiguration.getChampionshipChannelId()))
                || discordConfiguration.isServerNewsEnabled()) {
                if (client != null) {
                    try {
                        IChannel channel = null;
                        if (StringUtils.isNoneEmpty(server.getSrvChaId(), discordConfiguration.getChampionshipChannelId())) {
                            channel = client.getChannelByID(discordConfiguration.getChampionshipChannelId());
                        } else {
                            channel = client.getChannelByID(discordConfiguration.getChannelId());
                        }
                        if (channel != null) {
                            channel.sendMessage("'" + server.getSrvAlias() + "' " + message);
                        } else {
                            log.error("Unable to find news channel: {}", discordConfiguration.getChannelId());
                        }
                    } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
                        log.error("Error sending news to discord {}", e.getMessage());
                    }
                } else {
                    log.error("Unable send news to discord, client is null");
                }
            }
        }
    }

    @Bean
    public DiscordAgent discordAgent() {
        log.debug("Creating a DiscordAgent...");
        if (StringUtils.isNoneEmpty(token, channelId)) {
            return new DiscordAgent(this, compileEnabledCommandsList());
        } else {
            throw new IllegalArgumentException("discord.token and discord.channelId properties are mandatory");
        }
    }

    private List<Class> compileEnabledCommandsList() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(EnabledDiscordCommand.class));
        List<Class> enabledComands = scanner.findCandidateComponents(EnabledDiscordCommand.class.getPackage().getName()).stream().map(bd -> {
            log.debug("BeanDefinition detected: {}", bd.getBeanClassName());
            Class clazz = null;
            try {
                clazz = Class.forName(bd.getBeanClassName(), true, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException e) {
                log.error("Error detecting command list", e);
            }
            return clazz;
        }).collect(Collectors.toList());;
        return enabledComands;
    }

}
