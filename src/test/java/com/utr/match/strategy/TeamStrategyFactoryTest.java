package com.utr.match.strategy;

import com.utr.match.TeamLoader;
import com.utr.match.model.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SpringBootTest
class TeamStrategyFactoryTest {

    @Autowired
    TeamLoader loader;

    @Test
    void getStrategy() {
        BaseTeamStrategy strategy = TeamStrategyFactory.getStrategy(1);

        Team team = loader.initTeam("ZJU-BYD");

        strategy.analysisLineups(team);

        System.out.println(team.getPreferedLineups());
    }

    @Test
    void testFixedStrategy() {
        FixedPairTeamStrategy strategy = (FixedPairTeamStrategy) TeamStrategyFactory.getStrategy(3);
        Map<String, Set<String>> pairs = new HashMap<>();
        Set<String> mdPairs = new HashSet<>();
        //mdPairs.add("Xu  Peng,Tian Lu");
        //mdPairs.add("Tian Lu,Xu  Peng");
        mdPairs.add("Tian Lu");
        pairs.put("MD", mdPairs);

        strategy.setFixedPairs(pairs);

        Team team = loader.initTeam("ZJU-BYD");

        strategy.analysisLineups(team);

        System.out.println(team.getPreferedLineups());
    }

    @Test
    void testFixedMoreVarableStrategy() {
        FixedPairTeamStrategy strategy = (FixedPairWithMoreVariableTeamStrategy) TeamStrategyFactory.getStrategy(4);
        Map<String, Set<String>> pairs = new HashMap<>();
        Set<String> mdPairs = new HashSet<>();
//        mdPairs.add("Xu  Peng,Tian Lu");
        //mdPairs.add("Tian Lu,Xu  Peng");
        mdPairs.add("Tian Lu");
        pairs.put("MD", mdPairs);

        Set<String> d2Pairs = new HashSet<>();
        d2Pairs.add("Dai  Ian,Li Haoyang");
        pairs.put("D2", d2Pairs);

        strategy.setFixedPairs(pairs);

        Team team = loader.initTeam("ZJU-BYD");

        strategy.analysisLineups(team);

        System.out.println(team.getPreferedLineups());
    }

}