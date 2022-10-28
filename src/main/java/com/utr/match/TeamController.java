package com.utr.match;

import com.utr.match.model.Lineup;
import com.utr.match.model.Player;
import com.utr.match.model.Team;
import com.utr.match.strategy.BaseTeamStrategy;
import com.utr.match.strategy.LimitedLinesTeamStrategy;
import com.utr.match.strategy.MoreVariableTeamStrategy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<List<Lineup>> analysis(@RequestParam(value="team", defaultValue = "ZJU_BYD") String teamName,
                                                 @RequestParam(value="strategy", defaultValue = "0") String strategyNo) {

        Team team = new TeamLoader().initTeam(teamName);

        LineUpMatcher matcher = new LineUpMatcher();

        BaseTeamStrategy strategy = getStrategy(Integer.parseInt(strategyNo));

        List<Lineup> lineups = matcher.analysis(strategy, team);

        if (lineups.size() > 0 ) {
            return ResponseEntity.ok(lineups);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private BaseTeamStrategy getStrategy(int strategyNo) {
        switch (strategyNo) {
            case 0: return new BaseTeamStrategy();
            case 1: return new MoreVariableTeamStrategy();
            case 2: return new LimitedLinesTeamStrategy();
            default: return new MoreVariableTeamStrategy();
        }
    }
}
