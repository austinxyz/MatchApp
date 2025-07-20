package com.utr.match;

import com.utr.match.entity.*;
import com.utr.match.model.Team;
import com.utr.match.utr.CandidateTeam;
import com.utr.match.utr.UTRDivisionCandidateExcelExport;
import com.utr.match.utr.UTRDivisionPlayerExcelExport;
import com.utr.match.utr.UTRService;
import com.utr.model.League;
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
@RequestMapping("/utr")
@Api(tags = "UTR Management", description = "Operations related to UTR (Universal Tennis Rating) management")
public class UTRController {

    @Autowired
    private UTRService utrService;

    @CrossOrigin(origins = "*")
    @GetMapping("/events")
    @ApiOperation(value = "Get events", notes = "Retrieves all events based on status")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved events"),
        @ApiResponse(code = 404, message = "No events found")
    })
    public ResponseEntity<List<EventEntity>> events(
            @ApiParam(value = "Event status (active/inactive)", defaultValue = "active") 
            @RequestParam(value = "status", defaultValue = "active") String status
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
    @ApiOperation(value = "Get league by ID", notes = "Retrieves a league by its ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved league"),
        @ApiResponse(code = 404, message = "League not found")
    })
    public ResponseEntity<League> getLeague(
            @ApiParam(value = "League ID", required = true) @PathVariable("id") String id
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
    @ApiOperation(value = "Get team by ID", notes = "Retrieves a team by its ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved team"),
        @ApiResponse(code = 404, message = "Team not found")
    })
    public ResponseEntity<Team> getTeam(
            @ApiParam(value = "Team ID", required = true) @PathVariable("id") String id
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
    @ApiOperation(value = "Get candidate team by ID", notes = "Retrieves a candidate team by its ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved candidate team"),
        @ApiResponse(code = 404, message = "Candidate team not found")
    })
    public ResponseEntity<CandidateTeam> candidateTeam(
            @ApiParam(value = "Candidate team ID", required = true) @PathVariable("id") String id
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
    @ApiOperation(value = "Update candidate team UTRs", notes = "Updates UTR values for a candidate team")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully updated UTR values"),
        @ApiResponse(code = 404, message = "Candidate team not found")
    })
    public ResponseEntity<CandidateTeam> updateCandidatesUTR(
            @ApiParam(value = "Candidate team ID", required = true) @PathVariable("id") String id,
            @ApiParam(value = "Action to perform", required = true) @RequestParam("action") String action
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
    @ApiOperation(value = "Export division to Excel", notes = "Exports division data to Excel format")
    public ModelAndView exportDivisionToExcel(
            @ApiParam(value = "Division ID", required = true) @PathVariable("divisionId") String divisionId) {
        ModelAndView mav = new ModelAndView();
        mav.setView(new UTRDivisionCandidateExcelExport());

        CandidateTeam team = utrService.getCandidateTeam(Long.valueOf(divisionId));

        //send to excelImpl class
        mav.addObject("team", team);
        return mav;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/exportExcel/team/{teamId}")
    @ApiOperation(value = "Export team to Excel", notes = "Exports team data to Excel format")
    public ModelAndView exportTeamToExcel(
            @ApiParam(value = "Team ID", required = true) @PathVariable("teamId") String teamId) {
        ModelAndView mav = new ModelAndView();
        mav.setView(new UTRDivisionPlayerExcelExport());

        UTRTeamEntity team = utrService.getTeamEntity(teamId);

        //send to excelImpl class
        mav.addObject("team", team);
        return mav;
    }
    @CrossOrigin(origins = "*")
    @PutMapping("/divisions/{id}/candidate/{utrid}")
    @ApiOperation(value = "Add candidate to division", notes = "Adds a candidate to a division")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully added candidate"),
        @ApiResponse(code = 404, message = "Division not found")
    })
    public ResponseEntity<DivisionEntity> addCandidate(
            @ApiParam(value = "Division ID", required = true) @PathVariable("id") long id, 
            @ApiParam(value = "UTR ID", required = true) @PathVariable("utrid") String utrId ) {

        DivisionEntity div = utrService.getDivision(Long.valueOf(id));

        div = utrService.addCandidate(div, utrId);

        if (div != null) {
            return new ResponseEntity<>(div, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
