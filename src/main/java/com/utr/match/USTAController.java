package com.utr.match;


import com.utr.match.entity.*;
import com.utr.match.usta.USTASiteParser;
import com.utr.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
            Player utrPlayer = loader.getPlayer(player.getUtrId());
            player.setdUTR(utrPlayer.getdUTR());
            player.setsUTR(utrPlayer.getsUTR());
            player.setdUTRStatus(utrPlayer.getdUTRStatus());
            player.setsUTRStatus(utrPlayer.getsUTRStatus());
            player.setSuccessRate(utrPlayer.getSuccessRate());
/*            if (utrPlayer.getDynamicRating() == null) {
                try {
                    String dr = parser.getDynamicRating(player.getTennisRecordLink());
                    utrPlayer.setDynamicRating(dr);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            player.setDynamicRating(utrPlayer.getDynamicRating());*/
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

        List<USTATeam> teams = teamRepository.findByDivision_Id(Long.valueOf(divId));

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
}
