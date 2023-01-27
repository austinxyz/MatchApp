package com.utr.match;


import com.utr.match.entity.*;
import com.utr.match.usta.USTATeamAnalyser;
import com.utr.match.usta.USTATeamAnalysisResult;
import com.utr.match.usta.USTATeamImportor;
import com.utr.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usta")
public class USTAController {

    @Autowired
    USTATeamRepository teamRepository;

    @Autowired
    TeamLoader loader;

    @Autowired
    private USTADivisionRepository divisionRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private USTATeamImportor importor;

    @Autowired
    private USTATeamMatchRepository matchRepository;

    @Autowired
    private USTAFlightRepository flightRepository;

    @Autowired
    private USTATeamAnalyser teamAnalyser;

    @Autowired
    private USTATeamLineScoreRepository lineScoreRepository;

    @CrossOrigin(origins = "*")
    @GetMapping("/teams")
    public ResponseEntity<List<USTATeam>> teams(
    ) {
        List<USTATeam> teams = teamRepository.findAll();

        if (teams.size() > 0) {
            return ResponseEntity.ok(teams);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/teams/{id}")
    public ResponseEntity<USTATeam> team(@PathVariable("id") String id
    ) {
        Optional<USTATeam> team = teamRepository.findById(Long.valueOf(id));

        if (team.isPresent()) {
            return ResponseEntity.ok(prepareUTRData(team.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private USTATeam prepareUTRData(USTATeam ustaTeam) {
        for (PlayerEntity player : ustaTeam.getPlayers()) {
            if (player.getUtrId() == null || player.getUtrId().trim().equals("")) {
                continue;
            }

            if (player.getUtrFetchedTime() != null) {
                continue;
            }
            Player utrPlayer = loader.getPlayer(player.getUtrId());

            if (player.getUtrFetchedTime() != null && utrPlayer.getUtrFetchedTime().before(player.getUtrFetchedTime())) {
                //the time get utr info from utr website is early the time stored in db, no need to refresh from utr website.
                continue;
            }
            player.setDUTR(utrPlayer.getdUTR());
            player.setSUTR(utrPlayer.getsUTR());
            player.setDUTRStatus(utrPlayer.getdUTRStatus());
            player.setSUTRStatus(utrPlayer.getsUTRStatus());

            if (utrPlayer.getSuccessRate() > 0.0) {
                player.setSuccessRate(utrPlayer.getSuccessRate());
            }
            if (utrPlayer.getWholeSuccessRate() > 0.0) {
                player.setWholeSuccessRate(utrPlayer.getWholeSuccessRate());
            }
            //player.setUtrFetchedTime(new Timestamp(System.currentTimeMillis()));

        }
        return ustaTeam;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/search/teams")
    public ResponseEntity<List<USTATeam>> searchTeam(@RequestParam(value = "query") String query
    ) {
        List<USTATeam> teams = teamRepository.findByNameLike(query);

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

        List<USTATeam> teams = teamRepository.findByDivision_IdOrderByUstaFlightAsc(Long.valueOf(divId));

        if (teams.size() > 0) {
            return ResponseEntity.ok(teams);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/divisions/{divId}/flights")
    public ResponseEntity<List<USTAFlight>> getFlightsByDivisions(@PathVariable("divId") String divId
    ) {

        List<USTAFlight> flights = flightRepository.findByDivision_Id(Long.valueOf(divId));

        if (flights.size() > 0) {
            return ResponseEntity.ok(flights);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/flights/{flightId}/teams")
    public ResponseEntity<List<USTATeam>> getTeamByFlight(@PathVariable("flightId") String flightId
    ) {
        List<USTATeam> teams = teamRepository.findByUstaFlight_Id(Long.valueOf(flightId));

        if (teams.size() > 0) {
            return ResponseEntity.ok(teams);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/divisions/")
    public ResponseEntity<List<USTADivision>> getDivisions(
    ) {

        List<USTADivision> divisions = divisionRepository.findAll();

        if (divisions.size() > 0) {
            return ResponseEntity.ok(divisions);
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

            Optional<USTATeam> team = teamRepository.findById(Long.valueOf(id));

            if (team.isPresent()) {
                importor.updateTeamPlayersUTRID(team.get());

                return new ResponseEntity<>(team.get(), HttpStatus.OK);
            }

        }

        if (action.equals("refreshValue")) {

            Optional<USTATeam> team = teamRepository.findById(Long.valueOf(id));

            if (team.isPresent()) {
                importor.updateTeamUTRInfo(team.get());

                return new ResponseEntity<>(team.get(), HttpStatus.OK);
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

            Optional<USTATeam> team = teamRepository.findById(Long.valueOf(id));

            if (team.isPresent()) {
                USTATeam existTeam = team.get();

                existTeam = importor.importUSTATeam(existTeam.getLink());

                return new ResponseEntity<>(existTeam, HttpStatus.OK);
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

            Optional<USTATeam> team = teamRepository.findById(Long.valueOf(id));

            if (team.isPresent()) {
                USTATeam existTeam = team.get();

                importor.updateTeamPlayersDR(existTeam);

                return new ResponseEntity<>(existTeam, HttpStatus.OK);
            }

        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/teams/{id}/matches")
    public ResponseEntity<List<USTATeamMatch>> getTeamMatchScores(@PathVariable("id") String id,
                                                                  @RequestParam(value = "action", defaultValue = "fetch") String action) {

        Optional<USTATeam> team = teamRepository.findById(Long.valueOf(id));

        if (action.equals("fetch")) {

            if (team.isPresent()) {

                List<USTATeamMatch> matches = matchRepository.findByTeamOrderByMatchDateAsc(team.get());

                return new ResponseEntity<>(matches, HttpStatus.OK);
            }
        }

        if (action.equals("updateScore")) {

            if (team.isPresent()) {

                importor.refreshTeamMatchesScores(team.get());

                List<USTATeamMatch> matches = matchRepository.findByTeamOrderByMatchDateAsc(team.get());

                return new ResponseEntity<>(matches, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/players/{id}/utrs")
    public ResponseEntity<PlayerEntity> getPlayerUtr(@PathVariable("id") String id,
                                                     @RequestParam(value = "action", defaultValue = "fetch") String action) {

        Optional<PlayerEntity> player = playerRepository.findById(Long.valueOf(id));

        if (action.equals("fetch")) {

            if (player.isPresent()) {

                return new ResponseEntity<>(player.get(), HttpStatus.OK);
            }
        }

        if (action.equals("refreshValue")) {

            if (player.isPresent()) {

                PlayerEntity thisPlayer = importor.updatePlayerUTRInfo(player.get(), true);

                return new ResponseEntity<>(thisPlayer, HttpStatus.OK);
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

        Optional<PlayerEntity> player = playerRepository.findById(Long.valueOf(id));

        if (player.isPresent()) {

            PlayerEntity thisPlayer = player.get();

            List<USTATeamLineScore> scores = lineScoreRepository.findByGuestLine_Player1OrHomeLine_Player2OrHomeLine_Player1OrGuestLine_Player2(
                    thisPlayer, thisPlayer, thisPlayer, thisPlayer);

            return new ResponseEntity<>(scores, HttpStatus.OK);
        } else {

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
