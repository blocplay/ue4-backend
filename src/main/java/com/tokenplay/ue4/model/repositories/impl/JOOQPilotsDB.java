package com.tokenplay.ue4.model.repositories.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.model.db.tables.Tue4GearInstance;
import com.tokenplay.ue4.model.db.tables.Tue4GearModel;
import com.tokenplay.ue4.model.db.tables.Tue4Match;
import com.tokenplay.ue4.model.db.tables.Tue4Participation;
import com.tokenplay.ue4.model.db.tables.Tue4Pilot;
import com.tokenplay.ue4.model.db.tables.Tue4Registration;
import com.tokenplay.ue4.model.db.tables.Tue4Server;
import com.tokenplay.ue4.model.db.tables.Tue4ServerAuthorised;
import com.tokenplay.ue4.model.db.tables.Tue4UiTheme;
import com.tokenplay.ue4.model.db.tables.Tue4UiThemeAttribute;
import com.tokenplay.ue4.model.db.tables.Users;
import com.tokenplay.ue4.model.db.tables.records.GearInstanceRecord;
import com.tokenplay.ue4.model.db.tables.records.MatchRecord;
import com.tokenplay.ue4.model.db.tables.records.PilotRecord;
import com.tokenplay.ue4.model.db.tables.records.ServerRecord;
import com.tokenplay.ue4.model.db.tables.records.UsersRecord;
import com.tokenplay.ue4.model.repositories.PilotsDB;
import com.tokenplay.ue4.model.repositories.ServersDB;
import com.tokenplay.ue4.www.controllers.CommonAPI;

@Repository
@Transactional
@Slf4j
public class JOOQPilotsDB implements PilotsDB {
    private static final String DEATHS_COLUMN = "DEATHS";
    private static final String KILLS_COLUMN = "KILLS";
    private static final String TOTAL_COLUMN = "TOTAL";
    static final String TEST_PILOT_ID = "59a3e59c622d4dc4be01e3d094d2341a";
    private final DSLContext jooq;

    @Autowired
    public JOOQPilotsDB(DSLContext jooq) {
        this.jooq = jooq;
    }

    @Transactional(readOnly = true)
    @Override
    public Result<PilotRecord> findAll() {
        return jooq.selectFrom(Tue4Pilot.PILOT).fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public PilotRecord findById(String id) {
        return jooq.selectFrom(Tue4Pilot.PILOT).where(Tue4Pilot.PILOT.PIL_ID.eq(id)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public PilotRecord findByToken(String token) {
        return jooq
            .selectFrom(Tue4Pilot.PILOT)
            .where(
                Tue4Pilot.PILOT.PIL_TOKEN.eq(token).or(Tue4Pilot.PILOT.PIL_ID.eq(JOOQPilotsDB.TEST_PILOT_ID).and(Tue4Pilot.PILOT.PIL_ID.eq(token))))
            .fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public PilotRecord findByUserId(Long id) {
        return jooq.selectFrom(Tue4Pilot.PILOT).where(Tue4Pilot.PILOT.PIL_USU_ID.eq(id)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public Pair<PilotRecord, UsersRecord> findByIdWithUser(String id) {
        Record record =
            jooq.selectFrom(Tue4Pilot.PILOT.join(Users.USERS).on(Tue4Pilot.PILOT.PIL_USU_ID.eq(Users.USERS.ID))).where(Tue4Pilot.PILOT.PIL_ID.eq(id))
                .fetchOne();
        return getPilotRecordUsersRecordPair(record);
    }

    @Transactional(readOnly = true)
    @Override
    public PilotRecord findByCallsign(String callsign) {
        return jooq.selectFrom(Tue4Pilot.PILOT).where(Tue4Pilot.PILOT.PIL_CALLSIGN.equalIgnoreCase(callsign)).fetchOne();
    }

    @Transactional(readOnly = true)
    @Override
    public Pair<PilotRecord, UsersRecord> findBySteamId(String steamId) {
        final Pair<PilotRecord, UsersRecord> result;
        //@formatter:off
        Record record =
                jooq.selectFrom(Tue4Pilot.PILOT
                    .join(Users.USERS)
                    .on(Tue4Pilot.PILOT.PIL_USU_ID.eq(Users.USERS.ID)))
                    .where(Users.USERS.STEAM_ID.eq(steamId))
                    .fetchOne();
        //@formatter:on
        result = getPilotRecordUsersRecordPair(record);
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> getAuthorisedPilotsForServer(final String srvId) {
        return jooq.select(Tue4ServerAuthorised.SERVER_AUTHORISED.SAU_PIL_ID).from(Tue4ServerAuthorised.SERVER_AUTHORISED)
            .where(Tue4ServerAuthorised.SERVER_AUTHORISED.SAU_SRV_ID.eq(srvId)).fetch(Tue4ServerAuthorised.SERVER_AUTHORISED.SAU_PIL_ID);
    }

    @Transactional(readOnly = true)
    @Override
    public Result<Record> findByTokenWithFullTheme(String token) {
        return jooq
            .selectDistinct()
            .from(
                Tue4Pilot.PILOT.leftOuterJoin(
                    Tue4UiTheme.UI_THEME.leftOuterJoin(Tue4UiThemeAttribute.UI_THEME_ATTRIBUTE).on(
                        Tue4UiThemeAttribute.UI_THEME_ATTRIBUTE.UTA_UTH_ID.eq(Tue4UiTheme.UI_THEME.UTH_ID))).on(
                    Tue4Pilot.PILOT.PIL_UTH_ID.eq(Tue4UiTheme.UI_THEME.UTH_ID)))
            .where(
                Tue4Pilot.PILOT.PIL_TOKEN.eq(token).or(Tue4Pilot.PILOT.PIL_ID.eq(JOOQPilotsDB.TEST_PILOT_ID).and(Tue4Pilot.PILOT.PIL_ID.eq(token))))
            .fetch();
    }

    @Transactional(readOnly = true)
    @Override
    public Object[] pilotStatsByToken(String token) {
        List<Field<?>> totals = new LinkedList<>();
        totals.add(Tue4Participation.PARTICIPATION.PAR_MCH_ID.countDistinct().as(JOOQPilotsDB.TOTAL_COLUMN));
        totals.add(Tue4Participation.PARTICIPATION.PAR_KILLS.sum().as(JOOQPilotsDB.KILLS_COLUMN));
        totals.add(Tue4Participation.PARTICIPATION.PAR_DEATHS.sum().as(JOOQPilotsDB.DEATHS_COLUMN));
        Record select =
            jooq.select(totals)
                .from(Tue4Participation.PARTICIPATION)
                .join(Tue4Pilot.PILOT)
                .on(Tue4Participation.PARTICIPATION.PAR_PIL_ID.eq(Tue4Pilot.PILOT.PIL_ID))
                .where(
                    Tue4Pilot.PILOT.PIL_TOKEN.eq(token).or(
                        Tue4Pilot.PILOT.PIL_ID.eq(JOOQPilotsDB.TEST_PILOT_ID).and(Tue4Pilot.PILOT.PIL_ID.eq(token))))
                .and(Tue4Participation.PARTICIPATION.PAR_DATE_SUMMARY.isNotNull()).fetchOne();
        Integer total = (Integer) select.getValue(JOOQPilotsDB.TOTAL_COLUMN);
        BigDecimal kills = (BigDecimal) select.getValue(JOOQPilotsDB.KILLS_COLUMN);
        BigDecimal deaths = (BigDecimal) select.getValue(JOOQPilotsDB.DEATHS_COLUMN);
        return new Integer[] {
            total != null ? total : 0, kills != null ? kills.intValue() : 0, deaths != null ? deaths.intValue() : 0};
    }

    @Transactional(readOnly = true)
    @Override
    public Result<PilotRecord> findInactivePilots(OffsetDateTime when) {
        return jooq.selectFrom(Tue4Pilot.PILOT).where(Tue4Pilot.PILOT.PIL_LAST_PING.lessThan(when)).fetch();
    }

    @Override
    public void clean(PilotRecord pilot) {
        pilot.setPilSrvId(null);
        pilot.setPilLastPing(null);
        //pilot.setPilToken(null);
        pilot.store();
    }

    @Override
    public Record findPrivateServerStatus(String token, String id) {
        Tue4Pilot origin = Tue4Pilot.PILOT.as("origin");
        Tue4Pilot inServer = Tue4Pilot.PILOT.as("inServer");
        List<Field<?>> columns = new LinkedList<>();
        columns.addAll(Arrays.asList(Tue4Server.SERVER.fields()));
        columns.addAll(Arrays.asList(Tue4Match.MATCH.fields()));
        List<Field<?>> total = new LinkedList<>();
        total.addAll(columns);
        total.add(inServer.PIL_ID.countDistinct().as("PILOTS"));
        return jooq
            .select(total)
            .from(
                origin.join(Tue4Server.SERVER).on(origin.PIL_ID.eq(Tue4Server.SERVER.SRV_PIL_ID)).leftOuterJoin(Tue4Match.MATCH)
                    .on(Tue4Server.SERVER.SRV_MCH_ID.eq(Tue4Match.MATCH.MCH_ID)).leftOuterJoin(inServer)
                    .on(inServer.PIL_SRV_ID.eq(Tue4Server.SERVER.SRV_ID))).where(origin.PIL_TOKEN.eq(token).and(Tue4Server.SERVER.SRV_ID.eq(id)))
            .groupBy(columns).fetchOne();
    }

    @Override
    public Record findMatchStatusByToken(String token) {
        Tue4Pilot origin = Tue4Pilot.PILOT.as("origin");
        Tue4Pilot inServer = Tue4Pilot.PILOT.as("inServer");
        List<Field<?>> columns = new LinkedList<>();
        columns.addAll(Arrays.asList(Tue4Server.SERVER.fields()));
        columns.addAll(Arrays.asList(Tue4Match.MATCH.fields()));
        List<Field<?>> total = new LinkedList<>();
        total.addAll(columns);
        total.add(inServer.PIL_ID.countDistinct().as("PILOTS"));
        return jooq
            .select(total)
            .from(
                origin.join(Tue4Server.SERVER).on(origin.PIL_SRV_ID.eq(Tue4Server.SERVER.SRV_ID)).join(Tue4Match.MATCH)
                    .on(Tue4Server.SERVER.SRV_MCH_ID.eq(Tue4Match.MATCH.MCH_ID)).leftOuterJoin(inServer)
                    .on(inServer.PIL_SRV_ID.eq(Tue4Server.SERVER.SRV_ID)).and(inServer.PIL_OFF_LIMITS.eq(Boolean.FALSE)))
            .where(origin.PIL_TOKEN.eq(token).or(origin.PIL_ID.eq(JOOQPilotsDB.TEST_PILOT_ID).and(origin.PIL_ID.eq(token)))).groupBy(columns)
            .fetchOne();
    }

    @Override
    public Result<Record> findMatchesStatByToken(String token) {
        List<Field<?>> columns = new LinkedList<>();
        columns.addAll(Arrays.asList(Tue4Server.SERVER.fields()));
        columns.addAll(Arrays.asList(Tue4Match.MATCH.fields()));
        List<Field<?>> total = new LinkedList<>();
        total.addAll(columns);
        total.add(Tue4Participation.PARTICIPATION.PAR_KILLS.count().as(JOOQPilotsDB.KILLS_COLUMN));
        total.add(Tue4Participation.PARTICIPATION.PAR_DEATHS.count().as(JOOQPilotsDB.DEATHS_COLUMN));

        return jooq
            .select(total)
            .from(
                Tue4Pilot.PILOT.join(Tue4Participation.PARTICIPATION).on(
                    Tue4Participation.PARTICIPATION.PAR_PIL_ID.eq(Tue4Pilot.PILOT.PIL_ID).and(
                        Tue4Participation.PARTICIPATION.PAR_DATE_SUMMARY.isNotNull())))
            .join(Tue4Match.MATCH)
            .on(Tue4Match.MATCH.MCH_ID.eq(Tue4Participation.PARTICIPATION.PAR_MCH_ID).and(
                Tue4Match.MATCH.MCH_DATE_END.greaterThan(DSL.currentOffsetDateTime().minus(7))))
            .join(Tue4Server.SERVER)
            .on(Tue4Server.SERVER.SRV_ID.eq(Tue4Match.MATCH.MCH_SRV_ID))
            .where(
                Tue4Pilot.PILOT.PIL_TOKEN.eq(token).or(Tue4Pilot.PILOT.PIL_ID.eq(JOOQPilotsDB.TEST_PILOT_ID).and(Tue4Pilot.PILOT.PIL_ID.eq(token))))
            .groupBy(columns).orderBy(Tue4Match.MATCH.MCH_DATE_INIT.desc()).fetch();
    }

    @Override
    public Pair<PilotRecord, UsersRecord> findPilotAndUser(String token) {
        final Pair<PilotRecord, UsersRecord> result;
        Record record =
            jooq.selectFrom(Tue4Pilot.PILOT.join(Users.USERS).on(Tue4Pilot.PILOT.PIL_USU_ID.eq(Users.USERS.ID)))
                .where(
                    Tue4Pilot.PILOT.PIL_TOKEN.eq(token).or(
                        Tue4Pilot.PILOT.PIL_ID.eq(JOOQPilotsDB.TEST_PILOT_ID).and(Tue4Pilot.PILOT.PIL_ID.eq(token)))).fetchOne();
        result = getPilotRecordUsersRecordPair(record);
        return result;
    }

    private Pair<PilotRecord, UsersRecord> getPilotRecordUsersRecordPair(Record record) {
        Pair<PilotRecord, UsersRecord> result;
        if (record != null && record.size() > 0) {
            PilotRecord pilot = record.into(PilotRecord.class);
            UsersRecord user = record.into(UsersRecord.class);
            result = new ImmutablePair<>(pilot, user);
        } else {
            result = null;
        }
        return result;
    }


    @Override
    public UsersRecord findUserFromSteamId(String steamId) {
        //@formatter:off
        return
                jooq
                    .selectFrom(Users.USERS)
                    .where(Users.USERS.STEAM_ID.eq(steamId))
                    .fetchOne();
        //@formatter:on
    }

    @Override
    public Pair<PilotRecord, ServerRecord> findPilotAndServer(String token, String serverId) {
        final Pair<PilotRecord, ServerRecord> result;
        Record record =
            jooq.selectFrom(Tue4Pilot.PILOT.leftOuterJoin(Tue4Server.SERVER).on(Tue4Server.SERVER.SRV_ID.eq(serverId)))
                .where(
                    Tue4Pilot.PILOT.PIL_TOKEN.eq(token).or(
                        Tue4Pilot.PILOT.PIL_ID.eq(JOOQPilotsDB.TEST_PILOT_ID).and(Tue4Pilot.PILOT.PIL_ID.eq(token)))).fetchOne();
        if (record != null && record.size() > 0) {
            PilotRecord pilot = record.into(PilotRecord.class);
            ServerRecord server = null;
            if (record.getValue(Tue4Pilot.PILOT.PIL_ID) != null) {
                server = record.into(ServerRecord.class);
            }
            result = new ImmutablePair<>(pilot, server);
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public Triple<PilotRecord, ServerRecord, MatchRecord> findPilotAndServerAndMatch(String token, String serverId) {
        final Triple<PilotRecord, ServerRecord, MatchRecord> result;
        Record record =
            jooq.select()
                .from(Tue4Pilot.PILOT)
                .join(Tue4Server.SERVER)
                .on(Tue4Server.SERVER.SRV_ID.eq(serverId))
                .join(Tue4Match.MATCH)
                .on(Tue4Match.MATCH.MCH_SRV_ID.eq(Tue4Server.SERVER.SRV_ID).and(Tue4Server.SERVER.SRV_MCH_ID.eq(Tue4Match.MATCH.MCH_ID)))
                .where(
                    Tue4Pilot.PILOT.PIL_TOKEN.eq(token).or(
                        Tue4Pilot.PILOT.PIL_ID.eq(JOOQPilotsDB.TEST_PILOT_ID).and(Tue4Pilot.PILOT.PIL_ID.eq(token))))
                .and(Tue4Server.SERVER.SRV_STATUS.eq(ServersDB.SERVER_ACTIVE_STATUS)).fetchOne();
        if (record != null && record.size() > 0) {
            PilotRecord pilot = record.into(PilotRecord.class);
            ServerRecord server = record.into(ServerRecord.class);
            MatchRecord match = record.into(MatchRecord.class);
            result = new ImmutableTriple<>(pilot, server, match);
        } else {
            result = null;
        }
        return result;
    }

    @Override
    public Result<GearInstanceRecord> findGearDefinitionsForPilot(String pilId) {
        return jooq.selectFrom(Tue4GearInstance.GEAR_INSTANCE).where(Tue4GearInstance.GEAR_INSTANCE.GEI_PIL_ID.eq(pilId)).fetch();
    }

    @Override
    public boolean isRegisteredForChampionship(PilotRecord pilot, String chaId) {
        Tue4Registration reg = Tue4Registration.REGISTRATION.as("reg");
        return jooq.selectFrom(reg).where(reg.REG_PIL_ID.eq(pilot.getPilId()).and(reg.REG_CHA_ID.eq(chaId))).fetch().isNotEmpty();
    }

    @Override
    public void provideGears() {
        Tue4Pilot pil = Tue4Pilot.PILOT.as("PIL");
        Tue4GearInstance gei = Tue4GearInstance.GEAR_INSTANCE.as("GEI");
        Tue4GearModel gem = Tue4GearModel.GEAR_MODEL.as("GEM");
        //Tue4


        //Check whether the pilot owns the Gear
        //Check whether the pilot owns the plate
        //Check whether the pilot owns the gun

        //Populate the Instance table with the Asset IDs



        Result<Record> missing =
            jooq.select().from(pil, gem).whereNotExists(jooq.selectFrom(gei).where(gei.GEI_PIL_ID.eq(pil.PIL_ID).and(gei.GEI_GEM_ID.eq(gem.GEM_ID))))
                .fetch();
        List<GearInstanceRecord> instances = new ArrayList<>();
        for (Record missingInstance : missing) {
            log.info("Missing: {} -> {}", missingInstance.getValue(pil.PIL_CALLSIGN), missingInstance.getValue(gem.GEM_NAME));
            GearInstanceRecord newInstance = new GearInstanceRecord();
            newInstance.setGeiId(CommonAPI.newUUID());
            newInstance.setGeiName(missingInstance.getValue(gem.GEM_NAME));
            newInstance.setGeiGemId(missingInstance.getValue(gem.GEM_ID));
            newInstance.setGeiPilId(missingInstance.getValue(pil.PIL_ID));
            newInstance.setGeiDefaultScheme(null);
            newInstance.setGeiGesEngine(missingInstance.getValue(gem.GEM_GES_ENGINE));
            newInstance.setGeiGesFootLeft(missingInstance.getValue(gem.GEM_GES_FOOT_LEFT));
            newInstance.setGeiGesFootRight(missingInstance.getValue(gem.GEM_GES_FOOT_RIGHT));
            newInstance.setGeiGesFuelTank(missingInstance.getValue(gem.GEM_GES_FUEL_TANK));
            newInstance.setGeiGesHandLeft(missingInstance.getValue(gem.GEM_GES_HAND_LEFT));
            newInstance.setGeiGesHandRight(missingInstance.getValue(gem.GEM_GES_HAND_RIGHT));
            newInstance.setGeiGesHead(missingInstance.getValue(gem.GEM_GES_HEAD));
            newInstance.setGeiGesHip(missingInstance.getValue(gem.GEM_GES_HIP));
            newInstance.setGeiGesLowerArmLeft(missingInstance.getValue(gem.GEM_GES_LOWER_ARM_LEFT));
            newInstance.setGeiGesLowerArmRight(missingInstance.getValue(gem.GEM_GES_LOWER_ARM_RIGHT));
            newInstance.setGeiGesLowerLegLeft(missingInstance.getValue(gem.GEM_GES_LOWER_LEG_LEFT));
            newInstance.setGeiGesLowerLegRight(missingInstance.getValue(gem.GEM_GES_LOWER_LEG_RIGHT));
            newInstance.setGeiGesPylonLeft(missingInstance.getValue(gem.GEM_GES_PYLON_LEFT));
            newInstance.setGeiGesPylonRight(missingInstance.getValue(gem.GEM_GES_PYLON_RIGHT));
            newInstance.setGeiGesShoulderLeft(missingInstance.getValue(gem.GEM_GES_SHOULDER_LEFT));
            newInstance.setGeiGesShoulderRight(missingInstance.getValue(gem.GEM_GES_SHOULDER_RIGHT));
            newInstance.setGeiGesTorso(missingInstance.getValue(gem.GEM_GES_TORSO));
            newInstance.setGeiGesUpperArmLeft(missingInstance.getValue(gem.GEM_GES_UPPER_ARM_LEFT));
            newInstance.setGeiGesUpperArmRight(missingInstance.getValue(gem.GEM_GES_UPPER_ARM_RIGHT));
            newInstance.setGeiGesUpperLegLeft(missingInstance.getValue(gem.GEM_GES_UPPER_LEG_LEFT));
            newInstance.setGeiGesUpperLegRight(missingInstance.getValue(gem.GEM_GES_UPPER_LEG_RIGHT));
            newInstance.setIiGesEngineHeavyMountLeft(missingInstance.getValue(gem.GEM_GES_ENGINE_HEAVY_MOUNT_LEFT));
            newInstance.setIiGesEngineHeavyMountRight(missingInstance.getValue(gem.GEM_GES_ENGINE_HEAVY_MOUNT_RIGHT));
            newInstance.setIiGesFuelTankStorage(missingInstance.getValue(gem.GEM_GES_FUEL_TANK_STORAGE));
            newInstance.setIiGesLeftCollarMount(missingInstance.getValue(gem.GEM_GES_LEFT_COLLAR_MOUNT));
            newInstance.setIiGesRightCollarMount(missingInstance.getValue(gem.GEM_GES_RIGHT_COLLAR_MOUNT));
            newInstance.setIiGesUpperLegOutLeft(missingInstance.getValue(gem.GEM_GES_UPPER_LEG_OUT_LEFT));
            newInstance.setIiGesUpperLegOutRight(missingInstance.getValue(gem.GEM_GES_UPPER_LEG_OUT_RIGHT));

            instances.add(newInstance);
        }
        if (instances.size() > 0) {
            log.info("Inserting missing gears...");
            jooq.batchInsert(instances).execute();
            log.info("Gears inserted!");
        } else {
            log.debug("Nothing to do, move along");
        }
    }

    @Override
    public void provideCredit() {
        /*
         * Tue4Pilot pil = Tue4Pilot.PILOT.as("PIL");
         * Tue4BmCredit cre = Tue4BmCredit.BM_CREDIT.as("CRE");
         * 
         * Result<Record> p = jooq.select()
         * .from(pil)
         * .whereNotExists(jooq.selectFrom(cre)
         * .where(cre.BM_PILID.eq(pil.PIL_ID)))
         * .fetch();
         * 
         * //Now that we know the pilots lets find out what credit they have
         * 
         * System.out.println("Calculate missing credit");
         * List<BmCreditRecord> instances = new ArrayList<>();
         * for (Record pilots : p)
         * {
         * //Calculate for the Competitor Pack
         * //System.out.println("Calculate for the Competitor Pack");
         * 
         * Result<Record1<Long>> cp = jooq.select( Orderitems.ORDERITEMS.QUANTITY )
         * .from( GameUsers.USERS )
         * .join( Orders.ORDERS ).on( GameUsers.USERS.ID.equal( Orders.ORDERS.USER_ID ) )
         * .join( Orderitems.ORDERITEMS ).on( Orders.ORDERS.ID.equal( Orderitems.ORDERITEMS.ORDER_ID ) )
         * .join( Tue4Pilot.PILOT ).on( Tue4Pilot.PILOT.PIL_USU_ID.equal( GameUsers.USERS.ID ) )
         * .where( Orders.ORDERS.STATUS.equal( DSL.inline( String.valueOf( "APPROVED" ) ) )
         * .and( Orderitems.ORDERITEMS.PRODUCT_ID.equal( Long.valueOf( 1 ) ) )
         * .and( Tue4Pilot.PILOT.PIL_ID.equal( DSL.inline( String.valueOf( pilots.getValue(pil.PIL_ID) ) ) ) ) )
         * .fetch( );
         * 
         * for (Record1<Long> quantity : cp)
         * {
         * //Calculate for quantity
         * BmCreditRecord newInstance = new BmCreditRecord();
         * newInstance.setBmCreditId(CommonAPI.newUUID());
         * newInstance.setBmCreditDescription("Competitor Pack");
         * newInstance.setBmPilid(String.valueOf(pilots.getValue(pil.PIL_ID)));
         * Long q = quantity.getValue(Orderitems.ORDERITEMS.QUANTITY)*150000; //credit for cp
         * newInstance.setBmCreditissued(q.intValue());
         * 
         * instances.add(newInstance);
         * }
         * 
         * }
         */

        //Competitor Pack

        /*
         * Result<Record> missing = jooq.select()
         * .from(pil, cre, ord, itm, prd)
         * .whereNotExists(jooq.selectFrom(cre)
         * .where(cre.BM_PILID.eq(pil.PIL_ID)
         * .and(ord.STATUS.eq("APPROVED")
         * .and(prd.ID.eq(Long.valueOf(1))))))
         * .fetch();
         * 
         * List<BmCreditRecord> instances = new ArrayList<>();
         * 
         * for (Record missingInstance : missing)
         * {
         * log.info("Missing: {} -> {}", missingInstance.getValue(pil.PIL_CALLSIGN), missingInstance.getValue(prd.TITLE));
         * BmCreditRecord newInstance = new BmCreditRecord();
         * newInstance.setBmCreditId(CommonAPI.newUUID());
         * newInstance.setBmCreditDescription(missingInstance.getValue(prd.TITLE));
         * newInstance.setBmPilid(missingInstance.getValue(pil.PIL_ID));
         * newInstance.setBmCreditissued(150000);
         * instances.add(newInstance);
         * }
         * 
         * if (instances.size() > 0)
         * {
         * log.info("Inserting missing credit...");
         * jooq.batchInsert(instances).execute();
         * log.info("Credit inserted!");
         * }
         * else
         * {
         * log.debug("Nothing to do, move along");
         * }
         */
    }
}
