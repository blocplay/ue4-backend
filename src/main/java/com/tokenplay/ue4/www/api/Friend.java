package com.tokenplay.ue4.www.api;

import java.sql.Timestamp;
import java.text.Collator;
import java.time.OffsetDateTime;
import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.tokenplay.ue4.model.repositories.RelationshipsDB.FriendRecord;
import com.tokenplay.ue4.model.repositories.RelationshipsDB.IgnoredRecord;
import com.tokenplay.ue4.www.controllers.ui.ClientInterfaceAPI;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Friend {
    public static final String STATUS_ACCEPTED = "ACCEPTED";

    public static final String STATUS_PENDING = "PENDING";

    public static final String STATUS_IGNORED = "IGNORED";

    public static final String STATUS_WAITING = "WAITING";

    @Data
    private static class ServerStatus {
        private final String serverId;
        private final String serverName;
        private final String mapName;
    }

    private String callsign;

    private String status;

    @JsonIgnore
    private OffsetDateTime lastUpdate;

    private Boolean online;

    private ServerStatus serverStatus;

    @JsonProperty("lastUpdate")
    private String printLastUpdate() {
        return lastUpdate != null ? ClientInterfaceAPI.df.format(lastUpdate) : "";
    }

    public static final Comparator<Friend> BY_NAME = new Comparator<Friend>() {
        private Collator comparator = Collator.getInstance();

        @Override
        public int compare(Friend f1, Friend f2) {
            return comparator.compare(f1.getCallsign(), f2.getCallsign());
        }
    };

    public Friend(String callsign, String status, OffsetDateTime lastUpdate) {
        this(callsign, status, lastUpdate, null, null);
    }

    public Friend(FriendRecord record, String token) {
        Boolean online = record.getFriend().getPilLastPing() != null ? Boolean.TRUE : Boolean.FALSE;
        String callsign = record.getFriend().getPilCallsign();

        ServerStatus serverStatus = null;

        if (online) {
            if (record.getServer() != null) {
                String mapName = null;
                if (record.getMatch() != null) {
                    mapName = record.getMatch().getMchMapId();
                }
                serverStatus = new ServerStatus(record.getServer().getSrvId(), record.getServer().getSrvName(), mapName);
            }
        }
        setCallsign(callsign);
        setLastUpdate(record.getRelationship().getRelLastUpdate());
        setOnline(online);
        setServerStatus(serverStatus);
        setStatus(record.getRelationship().getRelStatus());
        if (record.getFriend().getPilId().equals(record.getRelationship().getRelPilIdTarget()) && STATUS_PENDING.equals(getStatus())) {
            setStatus(Friend.STATUS_WAITING);
        }
    }

    public Friend(IgnoredRecord record, String token) {
        setCallsign(record.getFriend().getPilCallsign());
        setLastUpdate(record.getRelationship().getRelLastUpdate());
        setOnline(Boolean.FALSE);
        setStatus(record.getRelationship().getRelStatus());
    }
}
