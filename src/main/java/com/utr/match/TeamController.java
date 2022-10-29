package com.utr.match;

import com.utr.match.model.Team;
import com.utr.match.strategy.BaseTeamStrategy;
import com.utr.match.strategy.TeamStrategyFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeamController {

    @GetMapping("/team")
    public ResponseEntity<Team> team(@RequestParam(value="team", defaultValue = "ZJU_BYD") String teamName) {
        Team team = new TeamLoader().initTeam(teamName);

        if (team.getPlayers().size() > 0 ) {
            return ResponseEntity.ok(team);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/lineup")
    public ResponseEntity<Team> analysis(@RequestParam(value="team", defaultValue = "ZJU_BYD") String teamName,
                                                 @RequestParam(value="strategy", defaultValue = "0") String strategyNo) {

        Team team = new TeamLoader().initTeam(teamName);

        BaseTeamStrategy strategy = TeamStrategyFactory.getStrategy(Integer.parseInt(strategyNo));

        strategy.analysisLineups(team);

        if (team.getPreferedLineups().size() > 0 ) {
            return ResponseEntity.ok(team);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
