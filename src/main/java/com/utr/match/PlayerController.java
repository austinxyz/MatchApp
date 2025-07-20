package com.utr.match;

import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.PlayerRepository;
import com.utr.match.usta.USTAService;
import com.utr.match.usta.USTATeamImportor;
import com.utr.match.usta.po.USTATeamMemberPO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/players")
@Api(tags = "Player Management", description = "Operations related to player management")
public class PlayerController {

    @Autowired
    PlayerRepository playerRepo;

    @Autowired
    USTAService service;

    @Autowired
    USTATeamImportor importor;

    @CrossOrigin(origins = "*")
    @GetMapping("/{id}")
    @ApiOperation(value = "Get player by ID", notes = "Retrieves a player by their ID and performs optional actions")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved player"),
        @ApiResponse(code = 404, message = "Player not found")
    })
    public ResponseEntity<PlayerEntity> player(
            @ApiParam(value = "Player ID", required = true) @PathVariable("id") String id,
            @ApiParam(value = "Action to perform (updateUTRId, updateDR)", required = true) @RequestParam("action") String action
    ) {
        PlayerEntity player = service.getPlayer(id);

        if (player != null) {
            if (action.equals("updateUTRId")) {
                importor.updatePlayerUTRID(player);
            }
            if (action.equals("updateDR")) {
                importor.updatePlayerDR(player);
            }
            return ResponseEntity.ok(player);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/{id}/teams")
    @ApiOperation(value = "Get player teams", notes = "Retrieves all teams associated with a player")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved teams"),
        @ApiResponse(code = 404, message = "No teams found for this player")
    })
    public ResponseEntity<List<USTATeamMemberPO>> playerTeams(
            @ApiParam(value = "Player ID", required = true) @PathVariable("id") String id
    ) {
        List<USTATeamMemberPO> members = service.getTeamMembersByPlayer(id);

        if (members.size() > 0) {
            return ResponseEntity.ok(members);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/search")
    @ApiOperation(value = "Search players by name", notes = "Searches for players whose names match the provided string")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved players"),
        @ApiResponse(code = 404, message = "No players found")
    })
    public ResponseEntity<List<PlayerEntity>> searchByName(
            @ApiParam(value = "Player name to search for", required = true) @RequestParam("name") String name
    ) {
        List<PlayerEntity> members = service.searchPlayersByName(name);

        if (members.size() > 0) {
            return ResponseEntity.ok(members);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/searchUTR")
    @ApiOperation(value = "Search players by UTR criteria", notes = "Searches for players based on UTR and other criteria")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved players"),
        @ApiResponse(code = 404, message = "No players found")
    })
    public ResponseEntity<List<PlayerEntity>> searchByUTR(
            @ApiParam(value = "USTA Rating", required = true) @RequestParam("USTARating") String ustaRating,
            @ApiParam(value = "UTR Limit", defaultValue = "16.0") @RequestParam(value = "utrLimit", defaultValue = "16.0") String utrLimitValue,
            @ApiParam(value = "UTR Value", defaultValue = "0.0") @RequestParam(value = "utr", defaultValue = "0.0") String utrValue,
            @ApiParam(value = "Type (single/double)", defaultValue = "double") @RequestParam(value = "type", defaultValue = "double") String type,
            @ApiParam(value = "Gender (M/F)", defaultValue = "M") @RequestParam(value = "gender", defaultValue = "M") String gender,
            @ApiParam(value = "Age Range") @RequestParam(value = "ageRange") String ageRange,
            @ApiParam(value = "Rated Only", defaultValue = "false") @RequestParam(value = "ratedOnly", defaultValue = "false") String ratedOnlyStr,
            @ApiParam(value = "Start Index", defaultValue = "0") @RequestParam(value = "start", defaultValue = "0") int start,
            @ApiParam(value = "Page Size", defaultValue = "10") @RequestParam(value = "size", defaultValue = "10") int size,
            @ApiParam(value = "Ascending Order", defaultValue = "false") @RequestParam(value = "asc", defaultValue = "false") String asc,
            @ApiParam(value = "Bay Area Only", defaultValue = "false") @RequestParam(value = "bayArea", defaultValue = "false") String bayArea
    ) {
        List<PlayerEntity> members = service.searchByUTR(ustaRating, utrLimitValue,
                utrValue, type, gender, ageRange, ratedOnlyStr, start, size, asc.equals("true"), bayArea.equals("true"));

        if (!members.isEmpty()) {
            return ResponseEntity.ok(members);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/statUTR")
    @ApiOperation(value = "Get UTR statistics", notes = "Retrieves statistics about UTR ratings based on various criteria")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved statistics"),
        @ApiResponse(code = 404, message = "No statistics found")
    })
    public ResponseEntity<Map<String, Object>> statUTR(
            @ApiParam(value = "USTA Rating", required = true) @RequestParam("USTARating") String ustaRating,
            @ApiParam(value = "Rated Only", defaultValue = "false") @RequestParam(value = "ratedOnly", defaultValue = "false") String ratedOnlyStr,
            @ApiParam(value = "Ignore Zero UTR", defaultValue = "false") @RequestParam(value = "ignoreZeroUTR", defaultValue = "false") String ignoreZeroUTRStr,
            @ApiParam(value = "Type (single/double)", defaultValue = "double") @RequestParam(value = "type", defaultValue = "double") String type,
            @ApiParam(value = "Gender (M/F)", defaultValue = "M") @RequestParam(value = "gender", defaultValue = "M") String gender,
            @ApiParam(value = "Age Range") @RequestParam(value = "ageRange") String ageRange
    ) {

        Map<String, Object> result = service.statUTR(ustaRating, ratedOnlyStr,
                ignoreZeroUTRStr, type, gender, ageRange);

        if (!result.isEmpty()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/")
    @ApiOperation(value = "Create player", notes = "Creates a new player")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully created player")
    })
    public ResponseEntity<PlayerEntity> createPlayer(
            @ApiParam(value = "Player details", required = true) @RequestBody PlayerEntity player) {

        PlayerEntity member = service.createPlayer(player);

        return ResponseEntity.ok(member);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/utr/{id}")
    @ApiOperation(value = "Get player by UTR ID", notes = "Retrieves a player by their UTR ID and performs optional actions")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved player"),
        @ApiResponse(code = 404, message = "Player not found")
    })
    public ResponseEntity<PlayerEntity> playerByUTR(
            @ApiParam(value = "UTR ID", required = true) @PathVariable("id") String utrId,
            @ApiParam(value = "Action to perform", defaultValue = "search") @RequestParam(value = "action", defaultValue = "search") String action,
            @ApiParam(value = "Double UTR value", defaultValue = "0.0") @RequestParam(value = "dutr", defaultValue = "0.0") String dUTRString,
            @ApiParam(value = "Single UTR value", defaultValue = "0.0") @RequestParam(value = "sutr", defaultValue = "0.0") String sUTRString
    ) {

        if (action.equals("search")) {

            PlayerEntity member = service.getPlayerByUTRId(utrId);

            if (member != null) {
                return ResponseEntity.ok(member);
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        if (action.equals("refreshUTRValue")) {

            PlayerEntity member = service.updatePlayerUTRValue(utrId);

            if (member != null) {
                return ResponseEntity.ok(member);
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        if (action.equals("updateUTRValue")) {

            double dUTRValue = Double.parseDouble(dUTRString);
            double sUTRValue = Double.parseDouble(sUTRString);

            PlayerEntity member = service.updatePlayerUTRValue(utrId, dUTRValue, sUTRValue);

            if (member != null) {
                return ResponseEntity.ok(member);
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/{id}")
    @ApiOperation(value = "Update player", notes = "Updates an existing player")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully updated player"),
        @ApiResponse(code = 404, message = "Player not found")
    })
    public ResponseEntity<PlayerEntity> updatePlayer(
            @ApiParam(value = "Player ID", required = true) @PathVariable("id") String id, 
            @ApiParam(value = "Updated player details", required = true) @RequestBody PlayerEntity player) {

        System.out.println("USTA Rating:" + player.getUstaRating());
        PlayerEntity member = service.updatePlayer(id, player);

        if (member != null) {
            return new ResponseEntity<>(member, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/usta/{norcalId}")
    @ApiOperation(value = "Get player by USTA NorCal ID", notes = "Retrieves a player by their USTA NorCal ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved player"),
        @ApiResponse(code = 404, message = "Player not found")
    })
    public ResponseEntity<PlayerEntity> getPlayerByNorcalId(
            @ApiParam(value = "USTA NorCal ID", required = true) @PathVariable("norcalId") String norcalId,
            @ApiParam(value = "Action to perform", defaultValue = "search") @RequestParam(value = "action", defaultValue = "search") String action
    ) {

        if (action.equals("search")) {
            PlayerEntity member = service.getPlayerByNorcalId(norcalId);

            if (member != null) {
                return ResponseEntity.ok(member);
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
