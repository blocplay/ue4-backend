package com.tokenplay.ue4.discord;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;

@Data
@Slf4j
public class DiscordEventListener {

    private final DiscordConfiguration discordConfiguration;

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        if (discordConfiguration.isServerNewsEnabled()) {
            detectChannel(event, "General", discordConfiguration.getChannelId());
        }
        if (discordConfiguration.isServerChampionshipNewsEnabled()) {
            detectChannel(event, "Championship", discordConfiguration.getChampionshipChannelId());
        }
    }

    private void detectChannel(ReadyEvent event, final String channelType, final String channelKey) {
        if (StringUtils.isNotEmpty(channelKey)) {
            IChannel channel = event.getClient().getChannelByID(channelKey);
            if (channel != null) {
                log.info("{} channel '#{}' detected...", channelType, channel.getName());
                //channel.sendMessage("Hello, I'm in");
            } else {
                log.error("{} channel not found: {}", channelType, channelKey);
            }
        }
    }

}
