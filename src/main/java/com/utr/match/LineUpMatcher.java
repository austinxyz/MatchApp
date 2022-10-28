package com.utr.match;

import com.utr.match.model.Team;
import com.utr.match.strategy.BaseTeamStrategy;

public class LineUpMatcher {

    public void analysis(BaseTeamStrategy strategy, Team team) {
        strategy.analysisLineups(team);
    }

}
