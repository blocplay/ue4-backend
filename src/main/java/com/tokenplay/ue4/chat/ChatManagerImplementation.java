package com.tokenplay.ue4.chat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Component
@ConfigurationProperties(prefix = "iirc")
@ConditionalOnProperty(name = "iirc.enabled", havingValue = "true", matchIfMissing = false)
public class ChatManagerImplementation extends ListenerAdapter<PircBotX> implements AutoCloseable, ChatManager {
    private ExecutorService executor;
    private PircBotX pircBotX;

    private static final String SERVER_CHATROOM_PREFFIX = "#server-";
    private static final SimpleDateFormat theSDF = new SimpleDateFormat("d MMM yyyy HH:mm:ss z", Locale.ENGLISH);

    @PostConstruct
    public void postConstruct() {
        // Connect to chat server
        log.debug("Configuring chat server...");
        executor = Executors.newFixedThreadPool(2);
        try {
            pircBotX = getPircBotx();
            pircBotX.startBot();
        } catch (IOException | IrcException e) {
            log.error("Unable to connect to chat manager, communications disabled", e);
            pircBotX = null;
        }
        log.info("Connection to chat server stablished");
    }

    public boolean isConnected() {
        return pircBotX != null && pircBotX.isConnected();
    }

    @Override
    public void close() {
        if (this.isConnected()) {
            log.info("Disconnecting from server");
            pircBotX.sendIRC().quitServer();
            pircBotX = null;
            executor.shutdownNow();
            log.info("... disconnected!");
        } else {
            log.error("Communications disabled, cannot disconnect again.");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tokenplay.ue4.chat.ChatManagerInterface#emptyServerRoom(java.lang.String, java.lang.String)
     */
    @Override
    @Async
    public void emptyServerRoom(String serverName, String serverId) {
        // If nick is not populated, it means we are not connected yet
        if (StringUtils.isNotBlank(pircBotX.getNick())) {
            for (Channel channel : pircBotX.getUserBot().getChannels()) {
                if (channel.getName().equals(getChannelName(serverName, serverId))) {
                    for (User user : channel.getUsers()) {
                        if (!user.equals(pircBotX.getUserBot())) {
                            log.debug("Kicking user {} from server room {}", user.getNick(), channel.getName());
                            channel.send().kick(user, "Server is closing the room");
                        }
                    }
                    log.debug("{} parting server room {}...", serverName, channel.getName());
                    channel.send().part("Server abandons the room");
                }
            }
        } else if (pircBotX != null) {
            log.debug("We cannot empty the room for server {}", serverName);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tokenplay.ue4.chat.ChatManagerInterface#sendNews(java.lang.String)
     */
    @Override
    @Async
    public void sendNews(String message) {
        this.sendMessage("#news", message);
    }

    //    private void sendMessage(String serverName, String serverId, String message) {
    //        this.sendMessage(getChannelName(serverName, serverId), message);
    //    }
    //
    //    private void sendMessage(String message) {
    //        this.sendMessage("#main", message);
    //    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tokenplay.ue4.chat.ChatManagerInterface#inviteToServerRoom(java.lang.String, java.lang.String,
     * com.tokenplay.ue4.model.db.tables.records.PilotRecord)
     */
    @Override
    @Async
    public void inviteToServerRoom(String serverName, String serverId, PilotRecord pilot) {
        pircBotX.sendIRC().invite(pilot.getPilCallsign(), getChannelName(serverName, serverId));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tokenplay.ue4.chat.ChatManagerInterface#createServerRoom(java.lang.String, java.lang.String, boolean)
     */
    @Override
    @Async
    public void createServerRoom(String serverAlias, String serverId, boolean isCreation) {
        if (isCreation) {
            log.debug("{} Joining/creating server room...", serverAlias);
        }
        pircBotX.sendIRC().joinChannel(getChannelName(serverAlias, serverId));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tokenplay.ue4.chat.ChatManagerInterface#sendMessage(java.lang.String, java.lang.String)
     */
    @Override
    @Async
    public void sendMessage(String channelName, String message) {
        if (StringUtils.isNotBlank(pircBotX.getNick())) {
            this.pircBotX.sendIRC().message(channelName, theSDF.format(new Date()) + ": " + message);
        } else {
            log.error("We Cannot send message to {}", channelName);
        }
    }


    private String getChannelName(String serverName, String serverId) {
        return SERVER_CHATROOM_PREFFIX + serverId;
    }

    private PircBotX getPircBotx() {
        // Setup this bot
        Configuration<PircBotX> configuration =
            new Configuration.Builder<PircBotX>()
                .setName("ue4BackEnd")
                .setLogin("ue4BackEnd")
                .setAutoNickChange(true)
                // .setCapEnabled(true)
                // .addCapHandler(new
                // TLSCapHandler(new
                // UtilSSLSocketFactory().trustAllCertificates(),
                // true))
                .addListener(this).setServerHostname("chat.heavygear.com").setServerPort(6666).addAutoJoinChannel("#main")
                .addAutoJoinChannel("#news").buildConfiguration();
        // bot.connect throws various exceptions for failures
        PircBotX bot = new PircBotX(configuration);
        return bot;
    }
}
