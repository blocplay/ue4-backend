package com.tokenplay.ue4.model.repositories;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.scheduling.annotation.Async;

import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.model.db.tables.records.ProfilesRecord;
import com.tokenplay.ue4.model.db.tables.records.UsersRecord;

public interface UsersDB {
    UsersRecord findByEmail(String email);

    boolean isDeveloper(UsersRecord user);

    boolean validates(UsersRecord user, String password);

    Triple<UsersRecord, ProfilesRecord, PilotRecord> findUserAndPilot(String username);

    Triple<UsersRecord, ProfilesRecord, PilotRecord> insertUserAndProfile(String usuEmail, String usuPassword, String pilCallsign, String steamId);

    PilotRecord createPilotFromUserAndProfile(ProfilesRecord profile, UsersRecord user);

    @Async
    void verifySteamPacks(long usuId);
}
