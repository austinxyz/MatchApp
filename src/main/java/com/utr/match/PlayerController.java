package com.utr.match;


import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.PlayerRepository;
import com.utr.match.usta.USTAService;
import com.utr.match.usta.USTATeamImportor;
import com.utr.match.usta.po.USTATeamMemberPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    PlayerRepository playerRepo;

    @Autowired
    USTAService service;

    @Autowired
    USTATeamImportor importor;

    @CrossOrigin(origins = "*")
    @GetMapping("/{id}")
    public ResponseEntity<PlayerEntity> player(@PathVariable("id") String id,
                                                 @RequestParam("action") String action
    ) {
        PlayerEntity player = service.getPlayer(id);

        if (player != null) {
            if (action.equals("updateUTRId")) {
                importor.updatePlayerUTRID(player);
            }
            return ResponseEntity.ok(player);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/{id}/teams")
    public ResponseEntity<List<USTATeamMemberPO>> playerTeams(@PathVariable("id") String id
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
    public ResponseEntity<List<PlayerEntity>> searchByName(@RequestParam("name") String name
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
    public ResponseEntity<List<PlayerEntity>> searchByUTR(@RequestParam("USTARating") String ustaRating,
                                                            @RequestParam(value = "utrLimit", defaultValue = "16.0") String utrLimitValue,
                                                            @RequestParam(value = "utr", defaultValue = "0.0") String utrValue,
                                                            @RequestParam(value = "type", defaultValue = "double") String type,
                                                            @RequestParam(value = "gender", defaultValue = "M") String gender,
                                                            @RequestParam(value = "ageRange") String ageRange,
                                                            @RequestParam(value = "ratedOnly", defaultValue = "false") String ratedOnlyStr,
                                                            @RequestParam(value = "start", defaultValue = "0") int start,
                                                            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        List<PlayerEntity> members = service.searchByUTR(ustaRating, utrLimitValue,
                utrValue, type, gender, ageRange, ratedOnlyStr, start, size);

        if (!members.isEmpty()) {
            return ResponseEntity.ok(members);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/statUTR")
    public ResponseEntity<Map<String, Object>> statUTR(@RequestParam("USTARating") String ustaRating,
                                                       @RequestParam(value = "ratedOnly", defaultValue = "false") String ratedOnlyStr,
                                                       @RequestParam(value = "ignoreZeroUTR", defaultValue = "false") String ignoreZeroUTRStr,
                                                       @RequestParam(value = "type", defaultValue = "double") String type,
                                                       @RequestParam(value = "gender", defaultValue = "M") String gender,
                                                       @RequestParam(value = "ageRange") String ageRange
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
    public ResponseEntity<PlayerEntity> createPlayer(@RequestBody PlayerEntity player) {

        PlayerEntity member = service.createPlayer(player);

        return ResponseEntity.ok(member);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/utr/{id}")
    public ResponseEntity<PlayerEntity> playerByUTR(@PathVariable("id") String utrId,
                                                          @RequestParam(value = "action", defaultValue = "search") String action
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

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/{id}")
    public ResponseEntity<PlayerEntity> updatePlayer(@PathVariable("id") String id, @RequestBody PlayerEntity player) {

        PlayerEntity member = service.updatePlayer(id, player);

        if (member != null) {
            return new ResponseEntity<>(member, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/usta/{norcalId}")
    public ResponseEntity<PlayerEntity> getPlayerByNorcalId(@PathVariable("norcalId") String norcalId,
                                                              @RequestParam(value = "action", defaultValue = "search") String action
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
