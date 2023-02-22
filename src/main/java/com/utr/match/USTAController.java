package com.utr.match;


import com.utr.match.entity.*;
import com.utr.match.usta.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usta")
public class USTAController {

    @Autowired
    private USTAService ustaService;

    @Autowired
    private USTATeamImportor importor;

    @Autowired
    private USTATeamAnalyser teamAnalyser;

    @CrossOrigin(origins = "*")
    @GetMapping("/teams/{id}")
    public ResponseEntity<USTATeam> team(@PathVariable("id") String id,
                                         @RequestParam(value = "matches", defaultValue = "false") boolean includeMatches
    ) {
        USTATeam team = ustaService.getTeam(id, includeMatches);
        if (team != null) {
            return ResponseEntity.ok(team);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/search/teams")
    public ResponseEntity<List<USTATeam>> searchTeam(@RequestParam(value = "query") String query
    ) {
        List<USTATeam> teams = ustaService.searchTeam(query);

        if (teams.size() > 0) {
            return ResponseEntity.ok(teams);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/divisions/{divId}/teams")
    public ResponseEntity<List<USTATeam>> getTeamsByDivision(@PathVariable("divId") String divId
    ) {

        List<USTATeam> teams = ustaService.getTeamsByDivision(divId);

        if (teams.size() > 0) {
            return ResponseEntity.ok(teams);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/divisions/{divId}/flights")
    public ResponseEntity<List<USTAFlight>> getFlightsByDivision(@PathVariable("divId") String divId
    ) {

        List<USTAFlight> flights = ustaService.getFlightsByDivision(divId);

        if (flights.size() > 0) {
            return ResponseEntity.ok(flights);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/flights/{flightId}/teams")
    public ResponseEntity<List<USTATeam>> getTeamsByFlight(@PathVariable("flightId") String flightId
    ) {
        List<USTATeam> teams = ustaService.getTeamsByFlight(flightId);

        if (teams.size() > 0) {
            return ResponseEntity.ok(teams);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/leagues/{id}/divisions/")
    public ResponseEntity<List<USTADivision>> getDivisions(@PathVariable("id") String id
    ) {

        List<USTADivision> divisions = ustaService.getDivisions(id);

        if (divisions.size() > 0) {
            return ResponseEntity.ok(divisions);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/{year}/divisions/")
    public ResponseEntity<List<USTADivision>> getDivisionsByYear(@PathVariable("year") String year
    ) {

        List<USTADivision> divisions = ustaService.getDivisionsByYear(year);

        if (divisions.size() > 0) {
            return ResponseEntity.ok(divisions);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/{year}/leagues/")
    public ResponseEntity<List<USTALeague>> getLeagues(@PathVariable("year") String year
    ) {

        List<USTALeague> leagues = ustaService.getLeagues(year);

        if (leagues.size() > 0) {
            return ResponseEntity.ok(leagues);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/teams/{id}/utrs")
    public ResponseEntity<USTATeam> updatePlayersUTRId(@PathVariable("id") String id,
                                                       @RequestParam("action") String action
    ) {

        if (action.equals("refreshID")) {
            USTATeam team = ustaService.getTeam(id);

            if (team != null) {
                importor.updateTeamPlayersUTRID(team);
                return new ResponseEntity<>(team, HttpStatus.OK);
            }

        }

        if (action.equals("refreshValue")) {

            USTATeam team = ustaService.getTeam(id);

            if (team != null) {
                importor.updateTeamUTRInfo(team);
                return new ResponseEntity<>(team, HttpStatus.OK);
            }

        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @CrossOrigin(origins = "*")
    @GetMapping("/teams/{id}/players")
    public ResponseEntity<USTATeam> updatePlayers(@PathVariable("id") String id,
                                                  @RequestParam("action") String action
    ) {

        if (action.equals("refresh")) {

            USTATeam team = ustaService.getTeam(id);

            if (team != null) {

                USTATeamEntity entity = importor.importUSTATeam(team.getLink());

                team = ustaService.getTeam(id, true);

                return new ResponseEntity<>(team, HttpStatus.OK);
            }

        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/teams/{id}/drs")
    public ResponseEntity<USTATeam> updatePlayersDR(@PathVariable("id") String id,
                                                    @RequestParam("action") String action
    ) {

        if (action.equals("refresh")) {

            USTATeam team = ustaService.getTeam(id);
            if (team != null) {

                importor.updateTeamPlayersDR(team);

                team = ustaService.getTeam(id, true);

                return new ResponseEntity<>(team, HttpStatus.OK);
            }

        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/teams/{id}/matches")
    public ResponseEntity<List<USTATeamMatch>> getTeamMatchScores(@PathVariable("id") String id,
                                                                  @RequestParam(value = "action", defaultValue = "fetch") String action) {


        if (action.equals("fetch")) {

            USTATeam team = ustaService.getTeam(id, true);

            if (team != null) {

                List<USTATeamMatch> matches = team.getMatches();

                return new ResponseEntity<>(matches, HttpStatus.OK);
            }
        }

        if (action.equals("updateScore")) {

            USTATeam team = ustaService.getTeam(id);

            if (team != null) {

                importor.refreshTeamMatchesScores(team, team.getTeamEntity().getDivision());

                team = ustaService.getTeam(id, true);

                List<USTATeamMatch> matches = team.getMatches();

                return new ResponseEntity<>(matches, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/teams/{id}/lineStat")
    public ResponseEntity<USTATeam> getTeamLineStat(@PathVariable("id") String id) {

        USTATeam team = ustaService.getTeam(id, true);
        if (team != null) {
            return new ResponseEntity<>(team, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/players/{id}/utrs")
    public ResponseEntity<USTATeamMember> getPlayerUtr(@PathVariable("id") String id,
                                                       @RequestParam(value = "action", defaultValue = "fetch") String action) {

        USTATeamMember member = ustaService.getMember(id);

        if (action.equals("fetch")) {

            if (member != null) {

                return new ResponseEntity<>(member, HttpStatus.OK);
            }
        }
        if (action.equals("refreshUTRId")) {

            if (member != null) {

                member = importor.updatePlayerUTRID(member);

                return new ResponseEntity<>(member, HttpStatus.OK);
            }
        }

        if (action.equals("refreshUTRValue")) {

            if (member != null) {

                member = importor.updatePlayerUTRInfo(member, true);

                return new ResponseEntity<>(member, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/analysis/team/team1/{teamId1}/team2/{teamId2}")
    public ResponseEntity<USTATeamAnalysisResult> singleAnalysis(@PathVariable("teamId1") String teamId1,
                                                                 @PathVariable("teamId2") String teamId2
    ) {
        USTATeamAnalysisResult result = teamAnalyser.compareTeam(teamId1, teamId2);

        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/players/{id}/scores")
    public ResponseEntity<List<USTATeamLineScore>> getPlayerScores(@PathVariable("id") String id) {

        List<USTATeamLineScore> scores = ustaService.getPlayerScores(id);

        if (scores.size() > 0) {

            return new ResponseEntity<>(scores, HttpStatus.OK);
        } else {

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/score/{id}")
    public ResponseEntity<USTATeamLineScore> updateLineScoreInfo(@PathVariable("id") long id, @RequestBody USTATeamLineScore score) {
        USTATeamLineScore newScore = ustaService.updateLineScoreInfo(id, score);

        if (newScore != null) {

            return new ResponseEntity<>(newScore, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
