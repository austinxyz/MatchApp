package com.utr.match;

import com.utr.match.entity.*;
import com.utr.match.usta.*;
import com.utr.match.usta.po.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/usta")
@Api(tags = "USTA Management", description = "Operations related to USTA (United States Tennis Association) management")
public class USTAController {

    @Autowired
    private USTAService ustaService;


    @Autowired
    private USTATeamImportor importor;

    @Autowired
    private USTAMatchImportor matchImportor;

    @Autowired
    private USTATeamAnalyser teamAnalyser;

    @CrossOrigin(origins = "*")
    @GetMapping("/teams/{id}")
    @ApiOperation(value = "Get team by ID", notes = "Retrieves a USTA team by its ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved team"),
        @ApiResponse(code = 404, message = "Team not found")
    })
    public ResponseEntity<USTATeam> team(
            @ApiParam(value = "Team ID", required = true) @PathVariable("id") String id,
            @ApiParam(value = "Include matches", defaultValue = "false") @RequestParam(value = "matches", defaultValue = "false") boolean includeMatches
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
    @ApiOperation(value = "Search teams", notes = "Searches for USTA teams based on a query string")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved teams"),
        @ApiResponse(code = 404, message = "No teams found")
    })
    public ResponseEntity<List<USTATeam>> searchTeam(
            @ApiParam(value = "Search query", required = true) @RequestParam(value = "query") String query
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
    @ApiOperation(value = "Get teams by division", notes = "Retrieves all teams in a specific division")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved teams"),
        @ApiResponse(code = 404, message = "No teams found")
    })
    public ResponseEntity<List<USTATeam>> getTeamsByDivision(
            @ApiParam(value = "Division ID", required = true) @PathVariable("divId") String divId
    ) {

        List<USTATeam> teams = ustaService.getTeamsByDivision(divId);

        if (teams.size() > 0) {
            return ResponseEntity.ok(teams);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/site/divisions/{id}/teams")
    @ApiOperation(value = "Get teams from USTA site", notes = "Retrieves teams from the USTA website for a specific division")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved teams"),
        @ApiResponse(code = 404, message = "No teams found")
    })
    public ResponseEntity<List<USTATeamPO>> getTeamsFromUSTASite(
            @ApiParam(value = "Division ID", required = true) @PathVariable("id") String id
    ) {

        List<USTATeamPO> teams = ustaService.getTeamsFromUSTASite(id);

        if (teams.size() > 0) {
            return ResponseEntity.ok(teams);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/divisions/{divId}/flights")
    @ApiOperation(value = "Get flights by division", notes = "Retrieves all flights in a specific division")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved flights"),
        @ApiResponse(code = 404, message = "No flights found")
    })
    public ResponseEntity<List<USTAFlight>> getFlightsByDivision(
            @ApiParam(value = "Division ID", required = true) @PathVariable("divId") String divId
    ) {

        List<USTAFlight> flights = ustaService.getFlightsByDivision(divId);

        if (flights.size() > 0) {
            return ResponseEntity.ok(flights);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/divisions/{divId}/candidateTeams")
    @ApiOperation(value = "Get candidate teams by division", notes = "Retrieves all candidate teams in a specific division")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved candidate teams"),
        @ApiResponse(code = 404, message = "No candidate teams found")
    })
    public ResponseEntity<List<USTACandidateTeam>> getCanidateTeamsByDivision(
            @ApiParam(value = "Division ID", required = true) @PathVariable("divId") String divId
    ) {

        List<USTACandidateTeam> teams = ustaService.getCandidateTeamsByDivision(divId);

        if (teams.size() > 0) {
            return ResponseEntity.ok(teams);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/candidateTeams/{id}")
    @ApiOperation(value = "Get candidate team by ID", notes = "Retrieves a candidate team by its ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved candidate team"),
        @ApiResponse(code = 404, message = "Candidate team not found")
    })
    public ResponseEntity<USTACandidateTeam> getCanidateTeamById(
            @ApiParam(value = "Candidate team ID", required = true) @PathVariable("id") String id
    ) {

        USTACandidateTeam team = ustaService.getCandidateTeam(id);

        if (team != null) {
            return ResponseEntity.ok(team);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/flights/{flightId}/teams")
    @ApiOperation(value = "Get teams by flight", notes = "Retrieves all teams in a specific flight")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved teams"),
        @ApiResponse(code = 404, message = "No teams found")
    })
    public ResponseEntity<List<USTATeam>> getTeamsByFlight(
            @ApiParam(value = "Flight ID", required = true) @PathVariable("flightId") String flightId
    ) {
        List<USTATeam> teams = ustaService.getTeamsByFlight(flightId);

        if (teams.size() > 0) {
            return ResponseEntity.ok(teams);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/leagues/{id}/divisions")
    @ApiOperation(value = "Get divisions by league", notes = "Retrieves all divisions in a specific league")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved divisions"),
        @ApiResponse(code = 404, message = "No divisions found")
    })
    public ResponseEntity<List<USTADivision>> getDivisions(
            @ApiParam(value = "League ID", required = true) @PathVariable("id") String id
    ) {

        List<USTADivision> divisions = ustaService.getDivisions(id);

        if (divisions.size() > 0) {
            return ResponseEntity.ok(divisions);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/{year}/divisions")
    @ApiOperation(value = "Get divisions by year", notes = "Retrieves all divisions for a specific year")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved divisions"),
        @ApiResponse(code = 404, message = "No divisions found")
    })
    public ResponseEntity<List<USTADivision>> getDivisionsByYear(
            @ApiParam(value = "Year", required = true) @PathVariable("year") String year
    ) {

        List<USTADivision> divisions = ustaService.getDivisionsByYear(year);

        if (divisions.size() > 0) {
            return ResponseEntity.ok(divisions);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/open/divisions")
    @ApiOperation(value = "Get open divisions", notes = "Retrieves all open divisions")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved divisions"),
        @ApiResponse(code = 404, message = "No divisions found")
    })
    public ResponseEntity<List<USTADivision>> getOpenDivisions() {

        List<USTADivision> divisions = ustaService.getOpenDivisions();

        if (divisions.size() > 0) {
            return ResponseEntity.ok(divisions);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/{year}/leagues")
    @ApiOperation(value = "Get leagues by year", notes = "Retrieves all leagues for a specific year")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved leagues"),
        @ApiResponse(code = 404, message = "No leagues found")
    })
    public ResponseEntity<List<USTALeague>> getLeagues(
            @ApiParam(value = "Year", required = true) @PathVariable("year") String year) {


        List<USTALeague> leagues = ustaService.getLeagues(year);

        if (leagues.size() > 0) {
            return ResponseEntity.ok(leagues);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/open/leagues")
    @ApiOperation(value = "Get open leagues", notes = "Retrieves all open leagues")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved leagues"),
        @ApiResponse(code = 404, message = "No leagues found")
    })
    public ResponseEntity<List<USTALeague>> getLeagues() {

        List<USTALeague> leagues = ustaService.getOpenLeagues();

        if (leagues.size() > 0) {
            return ResponseEntity.ok(leagues);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/current/leagues")
    @ApiOperation(value = "Get leagues from USTA site", notes = "Retrieves all leagues from the USTA website")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved leagues"),
        @ApiResponse(code = 404, message = "No leagues found")
    })
    public ResponseEntity<List<USTALeaguePO>> getLeaguesFromUSTA() {

        List<USTALeaguePO> leagues = ustaService.getLeaguesFromUSTASite();

        if (leagues.size() > 0) {
            return ResponseEntity.ok(leagues);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/teams/{id}/utrs")
    @ApiOperation(value = "Update team players UTR", notes = "Updates UTR information for players in a team")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully updated UTR information"),
        @ApiResponse(code = 404, message = "Team not found")
    })
    public ResponseEntity<USTATeam> updatePlayersUTRId(
            @ApiParam(value = "Team ID", required = true) @PathVariable("id") String id,
            @ApiParam(value = "Action to perform (refreshID, refreshValue)", required = true) @RequestParam("action") String action
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
    @PostMapping("/teams")
    @ApiOperation(value = "Create team", notes = "Creates a new USTA team")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully created team")
    })
    public ResponseEntity<USTATeam> createTeam(
            @ApiParam(value = "Team details", required = true) @RequestBody USTATeamPO team) {

        USTATeamEntity entity = importor.importUSTATeam(team.getLink());

        USTATeam newTeam = new USTATeam(entity);

        return new ResponseEntity<>(newTeam, HttpStatus.OK);

    }

    @CrossOrigin(origins = "*")
    @GetMapping("/teams/{id}/players")
    @ApiOperation(value = "Update team players", notes = "Updates player information for a team")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully updated players"),
        @ApiResponse(code = 404, message = "Team not found")
    })
    public ResponseEntity<USTATeam> updatePlayers(
            @ApiParam(value = "Team ID", required = true) @PathVariable("id") String id,
            @ApiParam(value = "Action to perform", required = true) @RequestParam("action") String action
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
    @ApiOperation(value = "Update team players DR", notes = "Updates Dynamic Rating information for players in a team")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully updated DR information"),
        @ApiResponse(code = 404, message = "Team not found")
    })
    public ResponseEntity<USTATeam> updatePlayersDR(
            @ApiParam(value = "Team ID", required = true) @PathVariable("id") String id,
            @ApiParam(value = "Action to perform", required = true) @RequestParam("action") String action
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
    @PostMapping("/site/divisions/")
    @ApiOperation(value = "Import division", notes = "Imports a division from the USTA website")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully imported division"),
        @ApiResponse(code = 404, message = "Division not found")
    })
    public ResponseEntity<USTADivisionPO> importDivision(
            @ApiParam(value = "Division details", required = true) @RequestBody USTADivisionPO division
    ) {

        USTADivisionPO div = ustaService.importDivisionFromUSTASite(division.getUSTALeagueId(), division.getLeagueName());

        if (div != null) {
            return new ResponseEntity<>(div, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/site/flight/teams")
    @ApiOperation(value = "Import flight teams", notes = "Imports teams for a flight from the USTA website")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully imported teams"),
        @ApiResponse(code = 404, message = "Flight not found")
    })
    public ResponseEntity<List<USTATeamPO>> importFlightTeams(
            @ApiParam(value = "Flight details", required = true) @RequestBody USTAFlightPO flight
    ) {

        List<USTATeamPO> result = ustaService.importTeamsFromUSTASite(flight.getId(), flight.getLink());

        if (!result.isEmpty()) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/teams/{id}/matches")
    @ApiOperation(value = "Get team match scores", notes = "Retrieves match scores for a team")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved match scores"),
        @ApiResponse(code = 404, message = "Team not found")
    })
    public ResponseEntity<List<USTAMatch>> getTeamMatchScores(
            @ApiParam(value = "Team ID", required = true) @PathVariable("id") String id,
            @ApiParam(value = "Action to perform", defaultValue = "fetch") @RequestParam(value = "action", defaultValue = "fetch") String action) {


        if (action.equals("fetch")) {

            USTATeam team = ustaService.getTeam(id, true);

            if (team != null) {

                List<USTAMatch> matches = team.getMatches();

                return new ResponseEntity<>(matches, HttpStatus.OK);
            }
        }

        if (action.equals("updateScore")) {

            USTATeam team = ustaService.getTeam(id);

            if (team != null) {

                matchImportor.refreshMatchesScores(team, team.getTeamEntity().getDivision());

                team = ustaService.getTeam(id, true);

                List<USTAMatch> matches = team.getMatches();

                return new ResponseEntity<>(matches, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/teams/{id}/lineStat")
    @ApiOperation(value = "Get team line statistics", notes = "Retrieves line statistics for a team")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved line statistics"),
        @ApiResponse(code = 404, message = "Team not found")
    })
    public ResponseEntity<USTATeam> getTeamLineStat(
            @ApiParam(value = "Team ID", required = true) @PathVariable("id") String id) {

        USTATeam team = ustaService.getTeam(id, true);
        if (team != null) {
            return new ResponseEntity<>(team, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/players/{id}/utrs")
    @ApiOperation(value = "Get player UTR", notes = "Retrieves or updates UTR information for a player")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved/updated UTR information"),
        @ApiResponse(code = 404, message = "Player not found")
    })
    public ResponseEntity<PlayerEntity> getPlayerUtr(
            @ApiParam(value = "Player ID", required = true) @PathVariable("id") String id,
            @ApiParam(value = "Action to perform", defaultValue = "fetch") @RequestParam(value = "action", defaultValue = "fetch") String action) {

        PlayerEntity member = ustaService.getPlayer(id);

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

                member = importor.updatePlayerUTRInfo(member, true, true);

                return new ResponseEntity<>(member, HttpStatus.OK);
            }
        }

        if (action.equals("refreshDR")) {

            if (member != null) {

                member = importor.updatePlayerDR(member);

                return new ResponseEntity<>(member, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/analysis/team/team1/{teamId1}/team2/{teamId2}")
    @ApiOperation(value = "Analyze team match", notes = "Analyzes a match between two teams")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully analyzed match"),
        @ApiResponse(code = 404, message = "Analysis not available")
    })
    public ResponseEntity<USTATeamAnalysisResult> singleAnalysis(
            @ApiParam(value = "Team 1 ID", required = true) @PathVariable("teamId1") String teamId1,
            @ApiParam(value = "Team 2 ID", required = true) @PathVariable("teamId2") String teamId2
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
    @ApiOperation(value = "Get player scores", notes = "Retrieves match scores for a player")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved scores"),
        @ApiResponse(code = 404, message = "No scores found")
    })
    public ResponseEntity<List<USTAMatchLinePO>> getPlayerScores(
            @ApiParam(value = "Player ID", required = true) @PathVariable("id") String id) {

        List<USTAMatchLinePO> scores = ustaService.getPlayerScores(id);

        if (scores.size() > 0) {

            return new ResponseEntity<>(scores, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/score/{id}")
    @ApiOperation(value = "Update line score", notes = "Updates information for a match line score")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully updated score"),
        @ApiResponse(code = 404, message = "Score not found")
    })
    public ResponseEntity<USTAMatchLine> updateLineScoreInfo(
            @ApiParam(value = "Score ID", required = true) @PathVariable("id") long id, 
            @ApiParam(value = "Updated score details", required = true) @RequestBody USTAMatchLine score) {
        USTAMatchLine newScore = ustaService.updateLineScoreInfo(id, score);

        if (newScore != null) {

            return new ResponseEntity<>(newScore, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/exportExcel/team/team1/{teamId1}/team2/{teamId2}")
    @ApiOperation(value = "Export team analysis to Excel", notes = "Exports team analysis to Excel format")
    public ModelAndView exportAnalysisToExcel(
            @ApiParam(value = "Team 1 ID", required = true) @PathVariable("teamId1") String teamId1,
            @ApiParam(value = "Team 2 ID", required = true) @PathVariable("teamId2") String teamId2) {
        ModelAndView mav = new ModelAndView();
        mav.setView(new USTATeamAnalyserExcelExport());

        USTATeamAnalysisResult result = teamAnalyser.compareTeam(teamId1, teamId2);

        //send to excelImpl class
        mav.addObject("analysisresult", result);
        return mav;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/exportExcel/team/{teamId}")
    @ApiOperation(value = "Export team to Excel", notes = "Exports team data to Excel format")
    public ModelAndView exportTeamToExcel(
            @ApiParam(value = "Team ID", required = true) @PathVariable("teamId") String teamId) {
        ModelAndView mav = new ModelAndView();
        mav.setView(new USTATeamExcelExport());

        USTATeam team = ustaService.getTeam(teamId, true);

        //send to excelImpl class
        mav.addObject("team", team);
        return mav;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/candidateTeams/{id}/utrs")
    @ApiOperation(value = "Update candidate team UTRs", notes = "Updates UTR values for a candidate team")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully updated UTR values"),
        @ApiResponse(code = 404, message = "Candidate team not found")
    })
    public ResponseEntity<USTACandidateTeam> updateCandidatesUTR(
            @ApiParam(value = "Candidate team ID", required = true) @PathVariable("id") String id,
            @ApiParam(value = "Action to perform", required = true) @RequestParam("action") String action
    ) {

        if (action.equals("refreshValue")) {

            USTACandidateTeam team = ustaService.getCandidateTeam(id);

            if (team != null) {
                importor.updateUSTACandidateListUTRInfo(team.getCandidates(), false, false);
                return new ResponseEntity<>(team, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/exportExcel/candidateTeam/{id}")
    @ApiOperation(value = "Export candidate team to Excel", notes = "Exports candidate team data to Excel format")
    public ModelAndView exportDivisionToExcel(
            @ApiParam(value = "Candidate team ID", required = true) @PathVariable("id") String id) {
        ModelAndView mav = new ModelAndView();
        mav.setView(new USTACandidateTeamExcelExport());

        USTACandidateTeam team = ustaService.getCandidateTeam(id);

        //send to excelImpl class
        mav.addObject("candidateTeam", team);
        return mav;
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/candidateTeam/{id}/candidate/{utrid}")
    @ApiOperation(value = "Add candidate to team", notes = "Adds a candidate to a candidate team")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully added candidate"),
        @ApiResponse(code = 404, message = "Candidate team not found")
    })
    public ResponseEntity<USTACandidateTeam> addCandidate(
            @ApiParam(value = "Candidate team ID", required = true) @PathVariable("id") String id, 
            @ApiParam(value = "UTR ID", required = true) @PathVariable("utrid") String utrId ) {

        USTACandidateTeam team = ustaService.getCandidateTeam(id);

        if (team != null) {
            team = ustaService.addCandidate(team, utrId);
            return new ResponseEntity<>(team, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
