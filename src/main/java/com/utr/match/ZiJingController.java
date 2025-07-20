package com.utr.match;

import com.utr.match.model.Lineup;
import com.utr.match.model.Team;
import com.utr.match.strategy.BaseTeamStrategy;
import com.utr.match.strategy.FixedPairWithMoreVariableTeamStrategy;
import com.utr.match.strategy.TeamStrategyFactory;
import com.utr.model.*;
import com.utr.player.PlayerAnalyser;
import com.utr.player.SingleAnalysisResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Api(tags = "Zijing Cup Management", description = "Operations related to Zijing Cup tournament management")
public class ZiJingController {

    @Autowired
    TeamLoader loader;

    boolean withToken = true;

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
    @GetMapping("/analysis/single/player1/{player1}/player2/{player2}")
    @ApiOperation(value = "Analyze single match", notes = "Analyzes a single match between two players")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully analyzed match"),
        @ApiResponse(code = 404, message = "Analysis not available")
    })
    public ResponseEntity<SingleAnalysisResult> singleAnalysis(
            @ApiParam(value = "Player 1 ID", required = true) @PathVariable("player1") String player1,
            @ApiParam(value = "Player 2 ID", required = true) @PathVariable("player2") String player2
    ) {
        SingleAnalysisResult result = PlayerAnalyser.getInstance().compareSingle(player1, player2);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/club/{clubId}")
    @ApiOperation(value = "Get club by ID", notes = "Retrieves a club by its ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved club"),
        @ApiResponse(code = 404, message = "Club not found")
    })
    public ResponseEntity<Club> club(
            @ApiParam(value = "Club ID", required = true) @PathVariable("clubId") String clubId) {
        Club club = loader.getClub(clubId, false);

        if (club != null) {
            return ResponseEntity.ok(club);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/search/players")
    @ApiOperation(value = "Search players", notes = "Searches for players based on a query string")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved players")
    })
    public ResponseEntity<List<Player>> searchPlayer(
            @ApiParam(value = "Search query", required = true) @RequestParam(value = "query") String query,
            @ApiParam(value = "Maximum number of results", defaultValue = "5") @RequestParam(value = "top", defaultValue = "5") int top) {

        List<Player> players = loader.queryPlayer(query, top, withToken);

        return ResponseEntity.ok(players);

    }

    @CrossOrigin(origins = "*")
    @GetMapping("/event/{eventId}")
    @ApiOperation(value = "Get event by ID", notes = "Retrieves an event by its ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved event"),
        @ApiResponse(code = 404, message = "Event not found")
    })
    public ResponseEntity<Event> event(
            @ApiParam(value = "Event ID", required = true) @PathVariable("eventId") String eventId) {
        Event event = loader.getEvent(eventId, false);

        if (event != null) {
            return ResponseEntity.ok(event);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/event/{eventId}/team/{teamId}")
    @ApiOperation(value = "Get team in event", notes = "Retrieves a team participating in a specific event")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved team"),
        @ApiResponse(code = 404, message = "Team not found in event")
    })
    public ResponseEntity<Team> eventTeam(
            @ApiParam(value = "Event ID", required = true) @PathVariable("eventId") String eventId, 
            @ApiParam(value = "Team ID", required = true) @PathVariable("teamId") String teamId) {
        Team team = loader.initTeam(teamId, eventId, withToken);

        if (team != null && team.getPlayers().size() > 0) {
            return ResponseEntity.ok(team);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/teams")
    @ApiOperation(value = "Get all teams", notes = "Retrieves all teams grouped by division")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved teams"),
        @ApiResponse(code = 404, message = "No teams found")
    })
    public ResponseEntity<List<Division>> teams() {
        List<Division> teams = loader.getDivisions();

        if (teams.size() > 0) {
            return ResponseEntity.ok(teams);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/team")
    @ApiOperation(value = "Get team by name", notes = "Retrieves a team by its name")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved team"),
        @ApiResponse(code = 404, message = "Team not found")
    })
    public ResponseEntity<Team> team(
            @ApiParam(value = "Team name", defaultValue = "ZJU") @RequestParam(value = "team", defaultValue = "ZJU") String teamName) {
        Team team = loader.initTeam(teamName);

        if (team!=null && team.getPlayers().size() > 0) {
            return ResponseEntity.ok(team);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/playerresult")
    @ApiOperation(value = "Get player results", notes = "Retrieves results for a specific player")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved player results"),
        @ApiResponse(code = 404, message = "Player results not found")
    })
    public ResponseEntity<PlayerResult> playerResult(
            @ApiParam(value = "Player ID", required = true) @RequestParam(value = "id") String id,
            @ApiParam(value = "Year (latest for most recent)", defaultValue = "latest") @RequestParam(value = "year", defaultValue = "latest") String year) {
        if (id == null || id.trim().equals("")) {
            return ResponseEntity.notFound().build();
        }

        PlayerResult player = loader.searchPlayerResult(id, year.equals("latest"), false);

        if (player != null) {
            return ResponseEntity.ok(player);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/lineup")
    @ApiOperation(value = "Analyze team lineups", notes = "Analyzes possible lineups for a team using a specified strategy")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully analyzed lineups"),
        @ApiResponse(code = 404, message = "No lineups found")
    })
    public ResponseEntity<List<Lineup>> analysis(
            @ApiParam(value = "Team name", defaultValue = "ZJU") @RequestParam(value = "team", defaultValue = "ZJU") String teamName,
            @ApiParam(value = "Strategy number", defaultValue = "0") @RequestParam(value = "strategy", defaultValue = "0") String strategyNo) {

        Team team = loader.initTeam(teamName);

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
    @ApiOperation(value = "Analyze fixed lineups", notes = "Analyzes lineups with fixed pairs for specific positions")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully analyzed fixed lineups"),
        @ApiResponse(code = 404, message = "No lineups found")
    })
    public ResponseEntity<List<Lineup>> analysisFixed(
            @ApiParam(value = "Team name", defaultValue = "ZJU-BYD") @RequestParam(value = "team", defaultValue = "ZJU-BYD") String teamName,
            @ApiParam(value = "Doubles 1 pairs", defaultValue = "") @RequestParam(value = "d1", defaultValue = "") String d1,
            @ApiParam(value = "Doubles 2 pairs", defaultValue = "") @RequestParam(value = "d2", defaultValue = "") String d2,
            @ApiParam(value = "Doubles 3 pairs", defaultValue = "") @RequestParam(value = "d3", defaultValue = "") String d3,
            @ApiParam(value = "Mixed doubles pairs", defaultValue = "") @RequestParam(value = "md", defaultValue = "") String md,
            @ApiParam(value = "Women's doubles pairs", defaultValue = "") @RequestParam(value = "wd", defaultValue = "") String wd,
            @ApiParam(value = "Use grant UTR", defaultValue = "true") @RequestParam(value = "grantUTR", defaultValue = "true") String grantUTR
    ) {

        Team team = loader.initTeam(teamName);

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
