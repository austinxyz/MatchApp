package com.utr.match;

import com.utr.match.model.Team;
import com.utr.match.strategy.BaseTeamStrategy;
import com.utr.match.strategy.MoreVariableTeamStrategy;
import org.junit.jupiter.api.Test;

class LineUpMatcherTest {

    @Test
    void analysis() {
        LineUpMatcher matcher = new LineUpMatcher();

        BaseTeamStrategy strategy = new MoreVariableTeamStrategy();

        Team team = new TeamLoader().initTeam("ZJU_BYD");

        matcher.analysis(strategy, team);

        System.out.println(team.getPreferedLineups());

    }
}