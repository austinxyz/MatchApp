package com.utr.match;


import com.utr.match.entity.*;
import com.utr.match.model.Team;
import com.utr.match.utr.CandidateTeam;
import com.utr.match.utr.UTRDivisionCandidateExcelExport;
import com.utr.match.utr.UTRDivisionPlayerExcelExport;
import com.utr.match.utr.UTRService;
import com.utr.model.League;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/utr")
public class UTRController {

    @Autowired
    private UTRService utrService;

    @CrossOrigin(origins = "*")
    @GetMapping("/events")
    public ResponseEntity<List<EventEntity>> events(@RequestParam(value = "status", defaultValue = "active") String status
    ) {
        List<EventEntity> events = utrService.getEvents(status.equals("active"));

        if (!events.isEmpty()) {
            return ResponseEntity.ok(events);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/leagues/{id}")
    public ResponseEntity<League> getLeague(@PathVariable("id") String id
    ) {
        League league = utrService.getLeague(id);

        if (league != null) {
            return ResponseEntity.ok(league);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/teams/{id}")
    public ResponseEntity<Team> getTeam(@PathVariable("id") String id
    ) {
        Team team = utrService.getTeam(id);

        if (team != null) {
            return ResponseEntity.ok(team);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/candidateTeams/{id}")
    public ResponseEntity<CandidateTeam> candidateTeam(@PathVariable("id") String id
    ) {
        CandidateTeam team = utrService.getCandidateTeam(Long.valueOf(id));

        if (team != null) {
            return ResponseEntity.ok(team);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/candidateTeams/{id}/utrs")
    public ResponseEntity<CandidateTeam> updateCandidatesUTR(@PathVariable("id") String id,
                                                       @RequestParam("action") String action
    ) {

        if (action.equals("refreshValue")) {

            DivisionEntity division = utrService.getDivision(Long.valueOf(id));

            if (division != null) {
                CandidateTeam team = utrService.updateCandidatesUTRValue(division, false, false);
                return new ResponseEntity<>(team, HttpStatus.OK);
            }

        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/exportExcel/divisions/{divisionId}")
    public ModelAndView exportDivisionToExcel(@PathVariable("divisionId") String divisionId) {
        ModelAndView mav = new ModelAndView();
        mav.setView(new UTRDivisionCandidateExcelExport());

        CandidateTeam team = utrService.getCandidateTeam(Long.valueOf(divisionId));

        //send to excelImpl class
        mav.addObject("team", team);
        return mav;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/exportExcel/team/{teamId}")
    public ModelAndView exportTeamToExcel(@PathVariable("teamId") String teamId) {
        ModelAndView mav = new ModelAndView();
        mav.setView(new UTRDivisionPlayerExcelExport());

        UTRTeamEntity team = utrService.getTeamEntity(teamId);

        //send to excelImpl class
        mav.addObject("team", team);
        return mav;
    }
    @CrossOrigin(origins = "*")
    @PutMapping("/divisions/{id}/candidate/{utrid}")
    public ResponseEntity<DivisionEntity> addCandidate(@PathVariable("id") long id, @PathVariable("utrid") String utrId ) {

        DivisionEntity div = utrService.getDivision(Long.valueOf(id));

        div = utrService.addCandidate(div, utrId);

        if (div != null) {
            return new ResponseEntity<>(div, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}