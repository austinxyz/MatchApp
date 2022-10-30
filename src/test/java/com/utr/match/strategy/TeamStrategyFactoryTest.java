package com.utr.match.strategy;

import com.utr.match.TeamLoader;
import com.utr.match.model.Team;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class TeamStrategyFactoryTest {

    @Test
    void getStrategy() {
        BaseTeamStrategy strategy = TeamStrategyFactory.getStrategy(0);

        Team team = new TeamLoader().initTeam("ZJU_BYD");

        strategy.analysisLineups(team);

        System.out.println(team.getPreferedLineups());
    }

    @Test
    void testFixedStrategy() {
        FixedPairTeamStrategy strategy = (FixedPairTeamStrategy) TeamStrategyFactory.getStrategy(3);
        Map<String, String> pairs = new HashMap<>();
        pairs.put("MD", "Xu  Peng+Tian Lu");

        strategy.setFixedPairs(pairs);

        Team team = new TeamLoader().initTeam("ZJU_BYD");

        strategy.analysisLineups(team);

        System.out.println(team.getPreferedLineups());
    }

    @Test
    void testFixedMoreVarableStrategy() {
        FixedPairTeamStrategy strategy = (FixedPairWithMoreVariableTeamStrategy) TeamStrategyFactory.getStrategy(4);
        Map<String, String> pairs = new HashMap<>();
        pairs.put("MD", "Xu  Peng+Tian Lu");

        strategy.setFixedPairs(pairs);

        Team team = new TeamLoader().initTeam("ZJU_BYD");

        strategy.analysisLineups(team);

        System.out.println(team.getPreferedLineups());
    }
}