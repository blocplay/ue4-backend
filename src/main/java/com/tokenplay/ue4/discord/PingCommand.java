package com.tokenplay.ue4.discord;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnabledDiscordCommand
public class PingCommand implements CommandExecutor {
    @Command(aliases = "!ping")
    public String onCommand(String command, String[] args) {
        log.info("Received ping");
        return "Pong!";
    }
}
