package com.utr.match;

import com.utr.match.model.Lineup;
import com.utr.match.model.Team;
import com.utr.match.strategy.BaseTeamStrategy;
import com.utr.match.strategy.FixedPairTeamStrategy;
import com.utr.match.strategy.TeamStrategyFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class TeamController {

    @CrossOrigin(origins = "*")
    @GetMapping("/team")
    public ResponseEntity<Team> team(@RequestParam(value="team", defaultValue = "ZJU_BYD") String teamName) {
        Team team = new TeamLoader().initTeam(teamName);

        if (team.getPlayers().size() > 0 ) {
            return ResponseEntity.ok(team);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/lineup")
    public ResponseEntity<List<Lineup>> analysis(@RequestParam(value="team", defaultValue = "ZJU_BYD") String teamName,
                                                 @RequestParam(value="strategy", defaultValue = "0") String strategyNo) {

        Team team = new TeamLoader().initTeam(teamName);

        BaseTeamStrategy strategy = TeamStrategyFactory.getStrategy(Integer.parseInt(strategyNo));

        strategy.analysisLineups(team);

        if (team.getPreferedLineups().size() > 0 ) {
            return ResponseEntity.ok(team.getPreferedLineups());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/fixedlineup")
    public ResponseEntity<List<Lineup>> analysisFixed(@RequestParam(value="team", defaultValue = "ZJU_BYD") String teamName,
                                                        @RequestParam(value="d1", defaultValue = "") String d1,
                                                        @RequestParam(value="d2", defaultValue = "") String d2,
                                                        @RequestParam(value="d3", defaultValue = "") String d3,
                                                        @RequestParam(value="md", defaultValue = "") String md,
                                                        @RequestParam(value="wd", defaultValue = "") String wd
                                                      ) {

        Team team = new TeamLoader().initTeam(teamName);

        FixedPairTeamStrategy strategy = (FixedPairTeamStrategy)TeamStrategyFactory.getStrategy(TeamStrategyFactory.FixedWithMoreVariable);

        Map<String, Set<String>> fixedPairs = new HashMap<>();
        initFixedPairs(d1, fixedPairs, "D1");
        initFixedPairs(d2, fixedPairs, "D2");
        initFixedPairs(d3, fixedPairs, "D3");
        initFixedPairs(md, fixedPairs, "MD");
        initFixedPairs(wd, fixedPairs, "WD");

        System.out.println(fixedPairs);

        strategy.setFixedPairs(fixedPairs);

        strategy.analysisLineups(team);

        if (team.getPreferedLineups().size() > 0 ) {
            return ResponseEntity.ok(team.getPreferedLineups());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private static void initFixedPairs(String pairNames, Map<String, Set<String>> fixedPairs, String lineName) {
        if (!pairNames.equals("")) {
            Set<String> pairs = new HashSet<>();
            if (pairNames.indexOf("_") > 0) {
                for (String pairName: pairNames.split("_")) {
                    pairs.add(pairName);
                }
            } else {
                pairs.add(pairNames);
            }
            fixedPairs.put(lineName, pairs);
        }
    }
}
