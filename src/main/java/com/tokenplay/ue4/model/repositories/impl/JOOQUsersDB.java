package com.tokenplay.ue4.model.repositories.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.model.db.tables.Orderitems;
import com.tokenplay.ue4.model.db.tables.Orders;
import com.tokenplay.ue4.model.db.tables.Profiles;
import com.tokenplay.ue4.model.db.tables.Tue4Pilot;
import com.tokenplay.ue4.model.db.tables.Users;
import com.tokenplay.ue4.model.db.tables.records.OrderitemsRecord;
import com.tokenplay.ue4.model.db.tables.records.OrdersRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.model.db.tables.records.ProfilesRecord;
import com.tokenplay.ue4.model.db.tables.records.UsersRecord;
import com.tokenplay.ue4.model.repositories.UsersDB;
import com.tokenplay.ue4.steam.client.SteamApiClient;
import com.tokenplay.ue4.steam.client.types.ApiException;
import com.tokenplay.ue4.steam.client.types.api.CheckAppOwnershipRS;
import com.tokenplay.ue4.utils.BCrypt;
import com.tokenplay.ue4.www.controllers.CommonAPI;

@Repository
@Slf4j
public class JOOQUsersDB implements UsersDB {
    private static final long COMPETITOR_PACK_ID = 1L;

    private static final long VETERAN_PACK_ID = 2L;

    private static final long ACE_PACK_ID = 3L;

    private static final int USER_PROFILE = 1;

    private final DSLContext jooq;

    @Autowired(required = false)
    SteamApiClient steamApiClient;

    @Autowired
    public JOOQUsersDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public UsersRecord findByEmail(String email) {
        return jooq.selectFrom(Users.USERS).where(Users.USERS.EMAIL.equalIgnoreCase(email.trim())).fetchOne();
    }

    @Transactional
    @Override
    @Async
    public void verifySteamPacks(long usuId) {
        if (steamApiClient != null) {
            try {
                // Check the packs that this user already has assigned
                //@formatter:off
            List<Long> packOrdersResult = jooq.selectDistinct(Orderitems.ORDERITEMS.PRODUCT_ID)
                .from(Orders.ORDERS)
                .join(Orderitems.ORDERITEMS)
                .on(Orderitems.ORDERITEMS.ORDER_ID.eq(Orders.ORDERS.ID))
                .where(Orders.ORDERS.USER_ID.eq(usuId).and(Orderitems.ORDERITEMS.PRODUCT_ID.in(COMPETITOR_PACK_ID,VETERAN_PACK_ID, ACE_PACK_ID)))
                .fetch()
                .getValues(Orderitems.ORDERITEMS.PRODUCT_ID)
            ;
            //@formatter:on
                // If the user does not have all the packs, we'll check in case he recently bought one
                if (CollectionUtils.isEmpty(packOrdersResult) || packOrdersResult.size() < 2) {
                    //
                    String steamId =
//@formatter:off
                    jooq.select(Users.USERS.STEAM_ID)
                        .from(Users.USERS)
                        .where(Users.USERS.ID.eq(usuId))
                        .fetchOne()
                        .getValue(Users.USERS.STEAM_ID);
                    //@formatter:on
                    //
                    if (steamId != null) {
                        try {
                            boolean ownsVeteranPack = false;
                            boolean ownsAcePack = false;
                            CheckAppOwnershipRS checkAppOwnershipRS = steamApiClient.checkVeteranPackOwnership(steamId);
                            if (checkAppOwnershipRS.ownsGame()) {
                                log.debug("User {}/{} id owns the veteran pack", usuId, steamId);
                                ownsVeteranPack = true;
                            }
                            checkAppOwnershipRS = steamApiClient.checkAcePackOwnership(steamId);
                            if (checkAppOwnershipRS.ownsGame()) {
                                log.debug("User {}/{} id owns the ace pack", usuId, steamId);
                                ownsAcePack = true;
                            }
                            final boolean requiresOrderForVeteranPack =
                                ownsVeteranPack && (CollectionUtils.isEmpty(packOrdersResult) || !packOrdersResult.contains(VETERAN_PACK_ID));
                            final boolean requiresOrderForAcePack =
                                ownsAcePack && (CollectionUtils.isEmpty(packOrdersResult) || !packOrdersResult.contains(ACE_PACK_ID));
                            final boolean requiresOrderForCompetitorsPack =
                                (CollectionUtils.isEmpty(packOrdersResult) && !requiresOrderForVeteranPack && !requiresOrderForAcePack);
                            if (requiresOrderForCompetitorsPack || requiresOrderForVeteranPack || requiresOrderForAcePack
                                || CollectionUtils.isEmpty(packOrdersResult)) {
                                log.info("User {} aquired some packs: Competitors Pack: {}/New Veteran Pack: {}/New Ace Pack:{}. Adding order...",
                                    usuId, requiresOrderForCompetitorsPack, requiresOrderForVeteranPack, requiresOrderForAcePack);
                                updateOrdersForPacks(requiresOrderForCompetitorsPack, requiresOrderForVeteranPack, requiresOrderForAcePack, usuId);
                            }
                        } catch (ApiException e) {
                            log.error("Error checking steam ownership", e);
                        }
                    } else {
                        log.debug("User {} has not steam id, not checking for packs");
                    }
                } else {
                    log.debug("User already has all the packs, nothing to do");
                }
            } catch (Exception e) {
                log.error("Error checking steam packs for user {} ", usuId, e);
            }
        }
    }

    @Transactional
    @Override
    public Triple<UsersRecord, ProfilesRecord, PilotRecord> insertUserAndProfile(String usuEmail, String usuPassword, String pilCallsign,
        String steamId) {
        Triple<UsersRecord, ProfilesRecord, PilotRecord> result;
        // Check if the Steam Id owns the game
        try {
            boolean ownsVeteranPack = false;
            boolean ownsAcePack = false;
            CheckAppOwnershipRS checkAppOwnershipRS = steamApiClient.checkBaseGameOwnership(steamId);
            if (checkAppOwnershipRS.ownsGame()) {
                log.info("User {}/{} id owns the game", usuEmail, steamId);
                UsersRecord usersRecord = new UsersRecord();
                usersRecord.setCreatedAt(OffsetDateTime.now());
                usersRecord.setUpdatedAt(OffsetDateTime.now());
                usersRecord.setActive(true);
                usersRecord.setEmail(usuEmail.trim());
                usersRecord.setSteamId(steamId);
                usersRecord.setRole(0L);
                usersRecord.setRoleId(1L);
                usersRecord.setNda(Boolean.TRUE);
                usersRecord.setAlpha(Boolean.TRUE);
                // TODO Supposedly they have read the EULA
                usersRecord.setEula(Boolean.TRUE);
                usersRecord.setPassword(BCrypt.hashpw(usuPassword, BCrypt.gensalt()));
                usersRecord.attach(jooq.configuration());
                usersRecord.store();
                //
                ProfilesRecord profilesRecord = new ProfilesRecord();
                profilesRecord.setCreatedAt(OffsetDateTime.now());
                profilesRecord.setUpdatedAt(OffsetDateTime.now());
                profilesRecord.setCallsign(pilCallsign);
                final Long usuId = usersRecord.getId();
                profilesRecord.setUserId(usuId);
                profilesRecord.attach(jooq.configuration());
                profilesRecord.store();
                //
                usersRecord.setProfileId(profilesRecord.getId());
                usersRecord.store();
                //
                checkAppOwnershipRS = steamApiClient.checkVeteranPackOwnership(steamId);
                if (checkAppOwnershipRS.ownsGame()) {
                    log.info("User {}/{} id owns the veteran pack", usuEmail, steamId);
                    ownsVeteranPack = true;
                }
                checkAppOwnershipRS = steamApiClient.checkAcePackOwnership(steamId);
                if (checkAppOwnershipRS.ownsGame()) {
                    log.info("User {}/{} id owns the ace pack", usuEmail, steamId);
                    ownsAcePack = true;
                }
                updateOrdersForPacks(!ownsVeteranPack && !ownsAcePack, ownsVeteranPack, ownsAcePack, usuId);
                //
                PilotRecord pilotRecord = createPilotFromUserAndProfile(profilesRecord, usersRecord);
                result = new ImmutableTriple<>(usersRecord, profilesRecord, pilotRecord);
            } else {
                log.error("Steam user {}/{} does not own the game. Trying to fool the system, are you?", usuEmail, steamId);
                throw new IllegalArgumentException("This steam user does now own the game");
            }
        } catch (ApiException e) {
            log.error("Error checking steam ownership", e);
            result = null;
        }
        return result;
    }

    private void updateOrdersForPacks(boolean ownsCompetitorsPack, boolean ownsVeteranPack, boolean ownsAcePack, final Long usuId) {
        //
        OrdersRecord ordersRecord = new OrdersRecord();
        ordersRecord.setUserId(usuId);
        ordersRecord.setCreatedAt(OffsetDateTime.now());
        ordersRecord.setProcessor("Steam");
        ordersRecord.setStatus("PENDING");
        ordersRecord.setUpdatedAt(OffsetDateTime.now());
        ordersRecord.attach(jooq.configuration());
        ordersRecord.store();
        //
        if (ownsVeteranPack) {
            generateOrderItem(ordersRecord, VETERAN_PACK_ID, "Veteran Pack");
        }
        if (ownsAcePack) {
            generateOrderItem(ordersRecord, ACE_PACK_ID, "Ace Pack");
        }
        if (ownsCompetitorsPack) {
            generateOrderItem(ordersRecord, COMPETITOR_PACK_ID, "Competitor Pack");
        }
        ordersRecord.setStatus("APPROVED");
        ordersRecord.store();
    }

    private void generateOrderItem(OrdersRecord ordersRecord, Long productId, String title) {
        OrderitemsRecord orderitemsRecord = new OrderitemsRecord();
        orderitemsRecord.setCreatedAt(OffsetDateTime.now());
        orderitemsRecord.setOrderId(ordersRecord.getId());
        orderitemsRecord.setProductId(productId);
        orderitemsRecord.setTitle(title);
        orderitemsRecord.setQuantity(1L);
        orderitemsRecord.setUpdatedAt(OffsetDateTime.now());
        orderitemsRecord.attach(jooq.configuration());
        orderitemsRecord.store();
    }

    @Override
    public boolean isDeveloper(UsersRecord user) {
        return user.getRoleId() > USER_PROFILE;
    }

    @Override
    public boolean validates(UsersRecord user, String password) {
        return BCrypt.checkpw(password, user.getPassword());
    }

    @Override
    public Triple<UsersRecord, ProfilesRecord, PilotRecord> findUserAndPilot(String username) {
        final Triple<UsersRecord, ProfilesRecord, PilotRecord> result;
        Record record =
            jooq.selectFrom(
                Users.USERS.join(Profiles.PROFILES).on(Profiles.PROFILES.USER_ID.eq(Users.USERS.ID)).leftOuterJoin(Tue4Pilot.PILOT)
                    .on(Tue4Pilot.PILOT.PIL_USU_ID.eq(Users.USERS.ID))).where(Users.USERS.EMAIL.equalIgnoreCase(username.trim())).fetchOne();
        if (record != null && record.size() > 0) {
            UsersRecord user = record.into(UsersRecord.class);
            ProfilesRecord profile = record.into(ProfilesRecord.class);
            PilotRecord pilot = null;
            if (record.getValue(Tue4Pilot.PILOT.PIL_ID) != null) {
                pilot = record.into(PilotRecord.class);
            }
            result = new ImmutableTriple<>(user, profile, pilot);
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public PilotRecord createPilotFromUserAndProfile(ProfilesRecord profile, UsersRecord user) {
        PilotRecord pilot;
        pilot = new PilotRecord();
        pilot.setPilId(CommonAPI.newUUID());
        pilot.setPilCallsign(profile.getCallsign());
        pilot.setPilUsuId(user.getId());
        pilot.setPilDisableChat(false);
        pilot.setPilDisableRequests(false);
        pilot.attach(jooq.configuration());
        pilot.store();
        return pilot;
    }
}
