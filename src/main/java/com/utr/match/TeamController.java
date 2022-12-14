package com.utr.match;

import com.utr.match.model.Lineup;
import com.utr.match.model.Team;
import com.utr.match.strategy.BaseTeamStrategy;
import com.utr.match.strategy.FixedPairWithMoreVariableTeamStrategy;
import com.utr.match.strategy.TeamStrategyFactory;
import com.utr.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class TeamController {

    private static void initFixedPairs(String pairNames, Map<String, Set<String>> fixedPairs, String lineName) {
        if (!pairNames.equals("")) {
            Set<String> pairs = new HashSet<>();
            if (pairNames.indexOf("_") > 0) {
                Collections.addAll(pairs, pairNames.split("_"));
            } else {
                pairs.add(pairNames);
            }
            fixedPairs.put(lineName, pairs);
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/club/{clubId}")
    public ResponseEntity<Club> club(@PathVariable("clubId") String clubId) {
        Club club = TeamLoader.getInstance().getClub(clubId);

        if (club != null) {
            return ResponseEntity.ok(club);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/search/players")
    public ResponseEntity<List<Player>> searchPlayer(@RequestParam(value = "query", defaultValue = "Yanzhao Xu") String query,
                                                     @RequestParam(value = "top", defaultValue = "5") int top) {

        List<Player> players = TeamLoader.getInstance().queryPlayer(query, top);

        return ResponseEntity.ok(players);

    }

    @CrossOrigin(origins = "*")
    @GetMapping("/event/{eventId}")
    public ResponseEntity<Event> event(@PathVariable("eventId") String eventId) {
        Event event = TeamLoader.getInstance().getEvent(eventId);

        if (event != null) {
            return ResponseEntity.ok(event);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/event/{eventId}/team/{teamId}")
    public ResponseEntity<Team> eventTeam(@PathVariable("eventId") String eventId, @PathVariable("teamId") String teamId) {
        Team team = TeamLoader.getInstance().initTeam(teamId, eventId);

        if (team != null && team.getPlayers().size() > 0) {
            return ResponseEntity.ok(team);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/teams")
    public ResponseEntity<List<Division>> teams() {
        List<Division> teams = TeamLoader.getInstance().getDivisions();

        if (teams.size() > 0) {
            return ResponseEntity.ok(teams);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/team")
    public ResponseEntity<Team> team(@RequestParam(value = "team", defaultValue = "ZJU-BYD") String teamName) {
        Team team = TeamLoader.getInstance().initTeam(teamName);

        if (team.getPlayers().size() > 0) {
            return ResponseEntity.ok(team);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/playerresult")
    public ResponseEntity<PlayerResult> playerResult(@RequestParam(value = "id", defaultValue = "1316122") String id) {
        PlayerResult player = TeamLoader.getInstance().searchPlayerResult(id);

        if (player != null) {
            return ResponseEntity.ok(player);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/lineup")
    public ResponseEntity<List<Lineup>> analysis(@RequestParam(value = "team", defaultValue = "ZJU-BYD") String teamName,
                                                 @RequestParam(value = "strategy", defaultValue = "0") String strategyNo) {

        Team team = TeamLoader.getInstance().initTeam(teamName);

        BaseTeamStrategy strategy = TeamStrategyFactory.getStrategy(Integer.parseInt(strategyNo));

        strategy.analysisLineups(team);

        if (team.getPreferedLineups().size() > 0) {
            return ResponseEntity.ok(team.getPreferedLineups());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/fixedlineup")
    public ResponseEntity<List<Lineup>> analysisFixed(@RequestParam(value = "team", defaultValue = "ZJU-BYD") String teamName,
                                                      @RequestParam(value = "d1", defaultValue = "") String d1,
                                                      @RequestParam(value = "d2", defaultValue = "") String d2,
                                                      @RequestParam(value = "d3", defaultValue = "") String d3,
                                                      @RequestParam(value = "md", defaultValue = "") String md,
                                                      @RequestParam(value = "wd", defaultValue = "") String wd,
                                                      @RequestParam(value = "grantUTR", defaultValue = "true") String grantUTR
    ) {

        Team team = TeamLoader.getInstance().initTeam(teamName);

        FixedPairWithMoreVariableTeamStrategy strategy = (FixedPairWithMoreVariableTeamStrategy) TeamStrategyFactory.getStrategy(TeamStrategyFactory.FixedWithMoreVariable);

        Map<String, Set<String>> fixedPairs = new HashMap<>();
        initFixedPairs(d1, fixedPairs, "D1");
        initFixedPairs(d2, fixedPairs, "D2");
        initFixedPairs(d3, fixedPairs, "D3");
        initFixedPairs(md, fixedPairs, "MD");
        initFixedPairs(wd, fixedPairs, "WD");

        strategy.setUseGrantUTR(grantUTR.equals("true"));

        strategy.setFixedPairs(fixedPairs);

        strategy.analysisLineups(team);

        if (team.getPreferedLineups().size() > 0) {
            return ResponseEntity.ok(team.getPreferedLineups());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
