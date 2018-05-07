package com.tokenplay.ue4.tasks;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import com.tokenplay.ue4.configuration.BackendConfiguration;
import com.tokenplay.ue4.configuration.HazelCastConfiguration;
import com.tokenplay.ue4.logic.DataProvider;
import com.tokenplay.ue4.model.db.tables.Tue4EventScore;
import com.tokenplay.ue4.model.db.tables.Tue4Match;
import com.tokenplay.ue4.model.db.tables.Tue4MatchEvent;
import com.tokenplay.ue4.model.db.tables.Tue4Participation;
import com.tokenplay.ue4.model.db.tables.Tue4Pilot;
import com.tokenplay.ue4.model.db.tables.Tue4ScoreConfig;
import com.tokenplay.ue4.model.db.tables.Tue4Server;
import com.tokenplay.ue4.model.db.tables.records.EventScoreRecord;
import com.tokenplay.ue4.model.db.tables.records.ScoreConfigRecord;

@Slf4j
@Data
@Component
public class LeaderboardUpdater {
    @Autowired
    private HazelcastInstance hazelcast;

    @Autowired
    BackendConfiguration backendConfiguration;

    @Autowired
    private DataProvider dataProvider;

    public static final String COUNT_COLUMN = "COUNTER";

    public final static Comparator<PilotStats> STATS_COMPARATOR = new Comparator<PilotStats>() {
        @Override
        public int compare(PilotStats st1, PilotStats st2) {
            if (st1.getAjustedAvgScore() != null && st2.getAjustedAvgScore() != null
                && st1.getAjustedAvgScore().compareTo(st2.getAjustedAvgScore()) != 0) {
                return st2.getAjustedAvgScore().compareTo(st1.getAjustedAvgScore());
            } else if (st1.getMatches() != st2.getMatches()) {
                return st2.getMatches() - st1.getMatches();
            } else {
                return st1.getCallsign().compareToIgnoreCase(st2.getCallsign());
            }
        }
    };

    @Data
    @EqualsAndHashCode(of = {"id"})
    public static class PilotStats implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String id;
        private final String callsign;
        private int score;
        private int position;
        private int matches;
        private int deaths;
        private int kills;

        Map<String, Integer> eventsCount = new HashMap<>();

        private BigDecimal avgScore;
        private BigDecimal ajustedAvgScore;
        private BigDecimal factor;

        void updateDeaths(int deaths, Map<String, Integer> scoreByEvent) {
            this.deaths = deaths;
            addEvents("DEATHS", deaths, scoreByEvent);
        }

        void updateMatches(int matches, Map<String, BigDecimal> factors) {
            this.matches = matches;
            avgScore = BigDecimal.valueOf(score).divide(BigDecimal.valueOf(matches), MathContext.DECIMAL128);
            factor =
                new BigDecimal(matches).divide(factors.get("NEUTRAL_NUMBER_OF_MATCHES"), MathContext.DECIMAL128).max(factors.get("MINIMUM_FACTOR"))
                    .min(factors.get("MAXIMUM_FACTOR")).subtract(BigDecimal.ONE).divide(BigDecimal.TEN, MathContext.DECIMAL128).add(BigDecimal.ONE);
            ajustedAvgScore = avgScore.multiply(factor);
        }

        public void addEvents(String event, int count, Map<String, Integer> scoreByEvent) {
            if (scoreByEvent != null && scoreByEvent.containsKey(event)) {
                if ("KILL_GEAR".equals(event)) {
                    kills = count;
                }
                score += count * scoreByEvent.get(event);
            }
            eventsCount.put(event, count);
        }
    }

    @Scheduled(fixedDelay = 300_000, initialDelay = 5_000)
    protected void execute() {
        if (backendConfiguration.isTasksEnabled()) {
            IMap<String, PilotStats> statsCache = hazelcast.getMap(HazelCastConfiguration.LEADERBOARD_MAP_NAME);
            updateLeaderboard(dataProvider, statsCache);
        } else {
            log.debug("Background tasks are disabled, nothing to do");
        }
    }

    public static Set<PilotStats> updateLeaderboard(DataProvider dataProvider, String scoreConfigName) {
        log.debug("Updating leaderboard...");
        Map<String, BigDecimal> factors = new HashMap<>();
        Map<String, Integer> scoreByEvent = new HashMap<>();
        obtainScoreConfiguration(dataProvider, scoreConfigName, factors, scoreByEvent);
        //
        if (!scoreByEvent.isEmpty()) {
            //
            long monthsToConsider = factors.get("MONTHS_TO_CONSIDER").longValue();
            //
            Map<String, PilotStats> pilotsById = new HashMap<>();
            Set<PilotStats> sortedPilots = new TreeSet<>(STATS_COMPARATOR);
            Result<Record> events = getEventsFromPilots(dataProvider.getJooq(), monthsToConsider);
            for (Record record : events) {
                String pilotId = record.getValue(Tue4Pilot.PILOT.PIL_ID);
                String pilotCallsign = record.getValue(Tue4Pilot.PILOT.PIL_CALLSIGN);
                String event = record.getValue(Tue4MatchEvent.MATCH_EVENT.MEV_TYPE);
                Integer count = (Integer) record.getValue(COUNT_COLUMN);
                PilotStats pilotStats = pilotsById.get(pilotId);
                if (pilotStats == null) {
                    pilotStats = new PilotStats(pilotId, pilotCallsign);
                    pilotsById.put(pilotId, pilotStats);
                }
                pilotStats.addEvents(event, count, scoreByEvent);
            }
            //
            Result<Record> kills = getKillsFromPilots(dataProvider.getJooq(), monthsToConsider);
            for (Record record : kills) {
                String pilotId = record.getValue(Tue4Pilot.PILOT.PIL_ID);
                String pilotCallsign = record.getValue(Tue4Pilot.PILOT.PIL_CALLSIGN);
                Integer count = (Integer) record.getValue(COUNT_COLUMN);
                PilotStats pilotStats = pilotsById.get(pilotId);
                if (pilotStats == null) {
                    pilotStats = new PilotStats(pilotId, pilotCallsign);
                    pilotsById.put(pilotId, pilotStats);
                }
                pilotStats.updateDeaths(count, scoreByEvent);
            }

            Result<Record> matches = getMatchesFromPilots(dataProvider.getJooq(), monthsToConsider);
            for (Record record : matches) {
                String pilotId = record.getValue(Tue4Pilot.PILOT.PIL_ID);
                Integer count = (Integer) record.getValue(COUNT_COLUMN);
                PilotStats pilotStats = pilotsById.get(pilotId);
                if (pilotStats != null) {
                    pilotStats.updateMatches(count, factors);
                }
            }
            //
            sortedPilots.addAll(pilotsById.values().stream().filter(p -> {
                return p.getMatches() > 0;
            }).collect(Collectors.toList()));
            return sortedPilots;
        } else {
            log.error("No score configuration found for id {}.", scoreConfigName);
            return new TreeSet<>();
        }
    }

    public static void obtainScoreConfiguration(DataProvider dataProvider, String scoreConfigName, Map<String, BigDecimal> factors,
        Map<String, Integer> scoreByEvent) {
        //
        ScoreConfigRecord scoreConfig =
            dataProvider.getJooq().selectFrom(Tue4ScoreConfig.SCORE_CONFIG).where(Tue4ScoreConfig.SCORE_CONFIG.SCO_NAME.eq(scoreConfigName))
                .fetchOne();
        List<EventScoreRecord> eventScores =
            dataProvider
                .getJooq()
                .selectFrom(
                    Tue4EventScore.EVENT_SCORE.join(Tue4ScoreConfig.SCORE_CONFIG).on(
                        Tue4ScoreConfig.SCORE_CONFIG.SCO_ID.eq(Tue4EventScore.EVENT_SCORE.ESC_SCO_ID)))
                .where(Tue4ScoreConfig.SCORE_CONFIG.SCO_NAME.eq(scoreConfigName)).fetch().into(EventScoreRecord.class);
        if (scoreConfig != null && eventScores != null) {
            if (log.isDebugEnabled()) {
                log.debug("Score config used: {}", scoreConfigName);
                log.debug("MINIMUM_FACTOR: {}", scoreConfig.getScoMinimumFactor());
                log.debug("MAXIMUM_FACTOR: {}", scoreConfig.getScoMaximumFactor());
                log.debug("NEUTRAL_NUMBER_OF_MATCHES: {}", scoreConfig.getScoNeutralMatches());
                log.debug("MONTHS_TO_CONSIDER: {}", scoreConfig.getScoMonthsToConsider());
            }
            //
            for (EventScoreRecord eventScore : eventScores) {
                if (log.isDebugEnabled()) {
                    log.debug("{}: {}", eventScore.getEscEventType(), eventScore.getEscEventScore());
                }
                scoreByEvent.put(eventScore.getEscEventType(), eventScore.getEscEventScore());
            }
            factors.put("MINIMUM_FACTOR", scoreConfig.getScoMinimumFactor());
            factors.put("MAXIMUM_FACTOR", scoreConfig.getScoMaximumFactor());
            factors.put("NEUTRAL_NUMBER_OF_MATCHES", scoreConfig.getScoNeutralMatches());
            factors.put("MONTHS_TO_CONSIDER", scoreConfig.getScoMonthsToConsider());
        }
    }

    private static Result<Record> getEventsFromPilots(DSLContext jooq, long monthsToConsider) {
        final Condition eventTypeCondition =
            Tue4MatchEvent.MATCH_EVENT.MEV_TYPE.in("TEAMKILL_GEAR", "KILL_GEAR", "TEAM_PLATE_DESTROYED", "PLATE_DESTROYED");
        return getEventsFromPilotsFromPreviousMonths(jooq, monthsToConsider, eventTypeCondition);
    }

    private static Result<Record> getKillsFromPilots(DSLContext jooq, long monthsToConsider) {
        final Condition eventTypeCondition = Tue4MatchEvent.MATCH_EVENT.MEV_TYPE.eq("KILL_GEAR");
        return getEventsFromPilotsFromPreviousMonths(jooq, monthsToConsider, eventTypeCondition);
    }

    public static Result<Record> getEventsFromPilotsFromOneMatch(DSLContext jooq, String mchId) {
        final Condition eventTypeCondition =
            Tue4MatchEvent.MATCH_EVENT.MEV_TYPE.in("TEAMKILL_GEAR", "KILL_GEAR", "TEAM_PLATE_DESTROYED", "PLATE_DESTROYED");
        final Condition matchCondition = Tue4Match.MATCH.MCH_ID.eq(mchId);
        return getEventsFromPilotsAndMatches(jooq, eventTypeCondition, matchCondition);
    }

    private static Result<Record> getEventsFromPilotsFromPreviousMonths(DSLContext jooq, long monthsToConsider, final Condition eventTypeCondition) {
        OffsetDateTime now = OffsetDateTime.now().minusMonths(monthsToConsider).withHour(0).withMinute(0).withSecond(0).withNano(0);
        final Condition matchCondition =
            Tue4Match.MATCH.MCH_DATE_INIT.ge(now).and(Tue4Match.MATCH.MCH_DATE_END.isNotNull())
                .and(Tue4Server.SERVER.SRV_SANCTIONED.eq(Boolean.TRUE));
        return getEventsFromPilotsAndMatches(jooq, eventTypeCondition, matchCondition);
    }

    private static Result<Record> getEventsFromPilotsAndMatches(DSLContext jooq, final Condition eventTypeCondition, final Condition matchCondition) {
        List<Field<?>> columns = new LinkedList<>();
        columns.addAll(Arrays.asList(Tue4Pilot.PILOT.PIL_ID));
        columns.addAll(Arrays.asList(Tue4Pilot.PILOT.PIL_CALLSIGN));
        columns.addAll(Arrays.asList(Tue4MatchEvent.MATCH_EVENT.MEV_TYPE));
        List<Field<?>> total = new LinkedList<>();
        total.addAll(columns);
        total.add(Tue4MatchEvent.MATCH_EVENT.MEV_ID.count().as(COUNT_COLUMN));
        Result<Record> records =
            jooq.select(total)
                .from(
                    Tue4Match.MATCH.join(Tue4Server.SERVER).on(Tue4Match.MATCH.MCH_SRV_ID.eq(Tue4Server.SERVER.SRV_ID))
                        .join(Tue4Participation.PARTICIPATION).on(Tue4Participation.PARTICIPATION.PAR_MCH_ID.eq(Tue4Match.MATCH.MCH_ID))
                        .join(Tue4Pilot.PILOT).on(Tue4Pilot.PILOT.PIL_ID.eq(Tue4Participation.PARTICIPATION.PAR_PIL_ID))
                        .join(Tue4MatchEvent.MATCH_EVENT).on(Tue4MatchEvent.MATCH_EVENT.MEV_PAR_ID_TARGET.eq(Tue4Participation.PARTICIPATION.PAR_ID)))
                .where(matchCondition.and(eventTypeCondition)).groupBy(columns).orderBy(Tue4Pilot.PILOT.PIL_CALLSIGN).fetch();
        return records;
    }

    private static Result<Record> getMatchesFromPilots(DSLContext jooq, long monthsToConsider) {
        List<Field<?>> columns = new LinkedList<>();
        columns.addAll(Arrays.asList(Tue4Pilot.PILOT.PIL_ID));
        List<Field<?>> total = new LinkedList<>();
        total.addAll(columns);
        total.add(Tue4Match.MATCH.MCH_ID.countDistinct().as(COUNT_COLUMN));
        OffsetDateTime now = OffsetDateTime.now().minusMonths(monthsToConsider).withHour(0).withMinute(0).withSecond(0).withNano(0);
        Result<Record> records =
            jooq.select(total)
                .from(
                    Tue4Match.MATCH.join(Tue4Server.SERVER).on(Tue4Match.MATCH.MCH_SRV_ID.eq(Tue4Server.SERVER.SRV_ID))
                        .join(Tue4Participation.PARTICIPATION).on(Tue4Participation.PARTICIPATION.PAR_MCH_ID.eq(Tue4Match.MATCH.MCH_ID))
                        .join(Tue4Pilot.PILOT).on(Tue4Pilot.PILOT.PIL_ID.eq(Tue4Participation.PARTICIPATION.PAR_PIL_ID))
                        .join(Tue4MatchEvent.MATCH_EVENT).on(Tue4MatchEvent.MATCH_EVENT.MEV_PAR_ID_SOURCE.eq(Tue4Participation.PARTICIPATION.PAR_ID)))
                .where(
                    Tue4Match.MATCH.MCH_DATE_INIT.ge(now).and(Tue4Match.MATCH.MCH_DATE_END.isNotNull())
                        .and(Tue4Server.SERVER.SRV_SANCTIONED.eq(Boolean.TRUE))).groupBy(columns).orderBy(Tue4Pilot.PILOT.PIL_CALLSIGN).fetch();
        return records;
    }

    public static void updateLeaderboard(DataProvider dataProvider, IMap<String, PilotStats> statsCache) {
        Set<PilotStats> sortedPilots = updateLeaderboard(dataProvider, "DEFAULT");
        statsCache.clear();
        int counter = 1;
        for (PilotStats pilotStats : sortedPilots) {
            pilotStats.setPosition(counter++);
            statsCache.set(pilotStats.getId(), pilotStats);
            log.debug("Pilot: {}", pilotStats);
            log.debug("-------------------------------------------");
        }
    }
}
