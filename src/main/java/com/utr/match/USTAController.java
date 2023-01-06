package com.utr.match;


import com.utr.match.entity.USTATeam;
import com.utr.match.entity.USTATeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usta")
public class USTAController {

    @Autowired
    USTATeamRepository teamRepository;

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
            return ResponseEntity.ok(team.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
