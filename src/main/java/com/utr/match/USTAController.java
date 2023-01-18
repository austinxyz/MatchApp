package com.utr.match;


import com.utr.match.entity.*;
import com.utr.match.usta.USTASiteParser;
import com.utr.match.usta.USTATeamImportor;
import com.utr.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
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
    USTASiteParser parser;
    @Autowired
    private USTADivisionRepository divisionRepository;
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private USTATeamImportor importor;

    @Autowired
    private USTATeamMatchRepository matchRepository;

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
        for (PlayerEntity player: ustaTeam.getPlayers()) {
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
            player.setdUTR(utrPlayer.getdUTR());
            player.setsUTR(utrPlayer.getsUTR());
            player.setdUTRStatus(utrPlayer.getdUTRStatus());
            player.setsUTRStatus(utrPlayer.getsUTRStatus());

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

        List<USTATeam> teams = teamRepository.findByDivision_IdOrderByAreaAsc(Long.valueOf(divId));

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

        if (action.equals("refresh")) {

            Optional<USTATeam> team = teamRepository.findById(Long.valueOf(id));

            if (team.isPresent()) {
                importor.updateTeamPlayersUTRID(team.get());

                return new ResponseEntity<>(team.get(), HttpStatus.OK);
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

                importor.refreshTeamMatcheScores(team.get());

                List<USTATeamMatch> matches = matchRepository.findByTeamOrderByMatchDateAsc(team.get());

                return new ResponseEntity<>(matches, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
