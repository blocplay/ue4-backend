package com.tokenplay.ue4.www.api;

import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.tokenplay.ue4.tasks.LeaderboardUpdater.PilotStats;

@Data
@EqualsAndHashCode(callSuper = true)
public class LeaderBoardResponse extends JSONResponse {
    final PilotStats pilot;

    final Set<PilotStats> topPilots;

    final Set<PilotStats> surroundingPilots;
}
