package com.utr.match.strategy;

import com.utr.match.TeamLoader;
import com.utr.match.model.Team;
import org.junit.jupiter.api.Test;

class TeamStrategyFactoryTest {

    @Test
    void getStrategy() {
        BaseTeamStrategy strategy = TeamStrategyFactory.getStrategy(0);

        Team team = new TeamLoader().initTeam("ZJU_BYD");

        strategy.analysisLineups(team);

        System.out.println(team.getPreferedLineups());
    }
}