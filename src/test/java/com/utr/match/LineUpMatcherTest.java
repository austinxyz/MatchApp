package com.utr.match;

import com.utr.match.model.Lineup;
import com.utr.match.model.Team;
import com.utr.match.strategy.BaseTeamStrategy;
import com.utr.match.strategy.MoreVariableTeamStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

class LineUpMatcherTest {

    @Test
    void analysis() {
        LineUpMatcher matcher = new LineUpMatcher();

        BaseTeamStrategy strategy = new MoreVariableTeamStrategy();

        Team team = new TeamLoader().initTeam("ZJU_BYD");

        List<Lineup> lineups = matcher.analysis(strategy, team);

        System.out.println(lineups);

    }
}