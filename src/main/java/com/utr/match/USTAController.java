package com.utr.match;


import com.utr.match.entity.*;
import com.utr.match.usta.USTATeam;
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

    @Autowired
    private USTALeagueRepository leagueRepository;

    @CrossOrigin(origins = "*")
    @GetMapping("/teams")
    public ResponseEntity<List<USTATeamEntity>> teams(
    ) {
        List<USTATeamEntity> teams = teamRepository.findAll();

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
        Optional<USTATeamEntity> team = teamRepository.findById(Long.valueOf(id));

        if (team.isPresent()) {
            USTATeam ustaTeam = new USTATeam(team.get());
            return ResponseEntity.ok(ustaTeam);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

/*private USTATeamEntity prepareUTRData(USTATeamEntity ustaTeam) {
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
}*/

    @CrossOrigin(origins = "*")
    @GetMapping("/search/teams")
    public ResponseEntity<List<USTATeamEntity>> searchTeam(@RequestParam(value = "query") String query
    ) {
        List<USTATeamEntity> teams = teamRepository.findByNameLike("%"+query+"%");

        if (teams.size() > 0) {
            return ResponseEntity.ok(teams);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/divisions/{divId}/teams")
    public ResponseEntity<List<USTATeamEntity>> getTeamsByDivision(@PathVariable("divId") String divId
    ) {

        List<USTATeamEntity> teams = teamRepository.findByDivision_IdOrderByUstaFlightAsc(Long.valueOf(divId));

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
    public ResponseEntity<List<USTATeamEntity>> getTeamByFlight(@PathVariable("flightId") String flightId
    ) {
        List<USTATeamEntity> teams = teamRepository.findByUstaFlight_Id(Long.valueOf(flightId));

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

        List<USTADivision> divisions = divisionRepository.findByLeague_Id(Long.valueOf(id));

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

        List<USTADivision> divisions = divisionRepository.findByLeague_Year(year);

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

        List<USTALeague> leagues = leagueRepository.findByYear(year);

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

            Optional<USTATeamEntity> team = teamRepository.findById(Long.valueOf(id));

            if (team.isPresent()) {
                USTATeamEntity existTeam = team.get();
                importor.updateTeamPlayersUTRID(existTeam);

                return new ResponseEntity<>(new USTATeam(existTeam), HttpStatus.OK);
            }

        }

        if (action.equals("refreshValue")) {

            Optional<USTATeamEntity> team = teamRepository.findById(Long.valueOf(id));

            if (team.isPresent()) {
                USTATeamEntity existTeam = team.get();
                importor.updateTeamUTRInfo(existTeam);

                return new ResponseEntity<>(new USTATeam(existTeam), HttpStatus.OK);
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

            Optional<USTATeamEntity> team = teamRepository.findById(Long.valueOf(id));

            if (team.isPresent()) {
                USTATeamEntity existTeam = team.get();

                existTeam = importor.importUSTATeam(existTeam.getLink());

                return new ResponseEntity<>(new USTATeam(existTeam), HttpStatus.OK);
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

            Optional<USTATeamEntity> team = teamRepository.findById(Long.valueOf(id));

            if (team.isPresent()) {
                USTATeamEntity existTeam = team.get();

                importor.updateTeamPlayersDR(existTeam);

                return new ResponseEntity<>(new USTATeam(existTeam), HttpStatus.OK);
            }

        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/teams/{id}/matches")
    public ResponseEntity<List<USTATeamMatch>> getTeamMatchScores(@PathVariable("id") String id,
                                                                  @RequestParam(value = "action", defaultValue = "fetch") String action) {

        Optional<USTATeamEntity> team = teamRepository.findById(Long.valueOf(id));

        if (action.equals("fetch")) {

            if (team.isPresent()) {

                List<USTATeamMatch> matches = matchRepository.findByTeamOrderByMatchDateAsc(team.get());

                return new ResponseEntity<>(matches, HttpStatus.OK);
            }
        }

        if (action.equals("updateScore")) {

            if (team.isPresent()) {

                importor.refreshTeamMatchesScores(team.get(), team.get().getDivision());

                List<USTATeamMatch> matches = matchRepository.findByTeamOrderByMatchDateAsc(team.get());

                return new ResponseEntity<>(matches, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/teams/{id}/lineStat")
    public ResponseEntity<USTATeam> getTeamLineStat(@PathVariable("id") String id)  {

        Optional<USTATeamEntity> team = teamRepository.findById(Long.valueOf(id));

        if (team.isPresent()) {
            USTATeam ustaTeam = new USTATeam(team.get());
            for (USTATeamMatch match: matchRepository.findByTeamOrderByMatchDateAsc(team.get())) {
                if (match.getScoreCard() != null) {
                    ustaTeam.addScore(match.getScoreCard());
                }
            }
            return new ResponseEntity<>(ustaTeam, HttpStatus.OK);
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

    @CrossOrigin(origins = "*")
    @PutMapping("/score/{id}")
    public ResponseEntity<USTATeamLineScore> updateLineScoreInfo(@PathVariable("id") long id, @RequestBody USTATeamLineScore score) {
        Optional<USTATeamLineScore> scoreData = lineScoreRepository.findById(id);

        if (scoreData.isPresent()) {
            USTATeamLineScore _score = scoreData.get();
            _score.setVideoLink(score.getVideoLink());
            _score.setComment(score.getComment());

            return new ResponseEntity<>(lineScoreRepository.save(_score), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
