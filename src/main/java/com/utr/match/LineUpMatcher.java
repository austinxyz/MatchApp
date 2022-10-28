package com.utr.match;

import com.utr.match.model.Lineup;
import com.utr.match.model.Team;
import com.utr.match.strategy.BaseTeamStrategy;

import java.util.ArrayList;
import java.util.List;

public class LineUpMatcher {

    public List<Lineup> analysis(BaseTeamStrategy strategy, Team team) {

        strategy.matchingPairs(team);

        team.matchingLineup(0);

        return strategy.getPreferedLineups(team);
    }

}
