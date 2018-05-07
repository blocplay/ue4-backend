package com.tokenplay.ue4.chat;

import com.tokenplay.ue4.model.db.tables.records.PilotRecord;


public interface ChatManager {

    void emptyServerRoom(String serverName, String serverId);

    void sendNews(String message);

    void inviteToServerRoom(String serverName, String serverId, PilotRecord pilot);

    void createServerRoom(String serverAlias, String serverId, boolean isCreation);

    void sendMessage(String channelName, String message);

}
