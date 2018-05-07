package com.tokenplay.ue4.chat;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.discord.DiscordConfiguration.DiscordAgent;
import com.tokenplay.ue4.model.db.tables.pojos.Server;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;

@Slf4j
@Component
@Data
public class CommunicationsManager implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired(required = false)
    ChatManager chatManager;

    @Autowired(required = false)
    DiscordAgent discordAgent;

    private boolean iircEnabled = false;

    private boolean discordEnabled = false;

    @PostConstruct
    public void postConstruct() {
        iircEnabled = chatManager != null;
        discordEnabled = discordAgent != null;

        log.info("Communications manager started: IIRC communications are {}abled; Discord communications are {}abled", iircEnabled ? "en" : "dis",
            discordEnabled ? "en" : "dis");
    }

    @Async
    public void sendServerNews(ServerRecord serverRecord, String message) {
        sendServerNews(serverRecord.into(Server.class), message);
    }

    @Async
    public void sendServerNews(Server server, String message) {
        if (iircEnabled) {
            chatManager.sendNews("'" + server.getSrvAlias() + "' " + message);
        }
        if (discordEnabled) {
            discordAgent.sendNewsMessage(server, message);
        }
    }

    @Async
    public void createServerCommunicationsRoom(ServerRecord server) {
        log.info("Starting a server {}", server.getSrvAlias());
        sendServerNews(server, "is up & running!");
    }

    @Async
    public void emptyServerRoom(Server server) {
        sendServerNews(server, "has been shutdown!");
        if (iircEnabled) {
            chatManager.emptyServerRoom(server.getSrvName(), server.getSrvId());
        }
    }

    @Async
    public void reconnectoToServerCommunicationsRoom(ServerRecord server, String mapId, String gameMode) {
        if (iircEnabled) {
            chatManager.createServerRoom(server.getSrvAlias(), server.getSrvId(), false);
        }
        sendServerNews(server, " is preparing a match in " + mapId + "(" + gameMode + ")");
    }
}
