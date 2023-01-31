package com.utr.match;


import com.utr.match.entity.*;
import com.utr.match.usta.USTATeamImportor;
import com.utr.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    PlayerRepository playerRepo;

    @Autowired
    USTATeamImportor importor;

    @Autowired
    TeamLoader loader;

    @CrossOrigin(origins = "*")
    @GetMapping("/")
    public ResponseEntity<List<PlayerEntity>> players(
    ) {
        List<PlayerEntity> players = playerRepo.findAll();

        if (players.size() > 0) {
            return ResponseEntity.ok(players);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/{id}")
    public ResponseEntity<PlayerEntity> player(@PathVariable("id") String id,
                                                     @RequestParam("action") String action
    ) {
        Optional<PlayerEntity> player = playerRepo.findById(Long.valueOf(id));

        if (player.isPresent()) {
            if (action.equals("updateUTRId")) {
                return ResponseEntity.ok(importor.updatePlayerUTRID(player.get()));
            }
            return ResponseEntity.ok(player.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/{id}/teams")
    public ResponseEntity<Set<USTATeam>> playerTeams(@PathVariable("id") String id
    ) {
        Optional<PlayerEntity> player = playerRepo.findById(Long.valueOf(id));

        if (player.isPresent()) {
            return ResponseEntity.ok(player.get().getTeams());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/search")
    public ResponseEntity<List<PlayerEntity>> searchByName(@RequestParam("name") String name
    ) {
        List<PlayerEntity> players;

        if (isUTRId(name)) {
            players = new ArrayList<>();
            PlayerEntity player = playerRepo.findByUtrId(name);
            if (player != null) {
                players.add(player);
            }
        } else {

            String likeString = "%" + name + "%";
            players = playerRepo.findByNameLike(likeString);
            String reverseString = "%" + reverseName(name) + "%";

            if (!likeString.equals(reverseString)) {
                players.addAll(playerRepo.findByNameLike(reverseString));
            }
        }

        if (players.size() > 0) {
            return ResponseEntity.ok(players);
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
                                                          @RequestParam(value = "start", defaultValue = "0") int start,
                                                          @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable firstPage = PageRequest.of(start, size);
        PlayerSpecification ustaRatingSpec = new PlayerSpecification(new SearchCriteria("ustaRating", ":", ustaRating));
        PlayerSpecification UTRSpec;
        PlayerSpecification utrLimitSpec;
        if (type.equalsIgnoreCase("double")) {
            UTRSpec = new PlayerSpecification(new SearchCriteria("dUTR", ">", Double.valueOf(utrValue)),
                    new OrderByCriteria("dUTR", false));
            utrLimitSpec = new PlayerSpecification(new SearchCriteria("dUTR", "<", utrLimitValue));
        } else {
            UTRSpec = new PlayerSpecification(new SearchCriteria("sUTR", ">", Double.valueOf(utrValue)),
                    new OrderByCriteria("sUTR", false));
            utrLimitSpec = new PlayerSpecification(new SearchCriteria("sUTR", "<", utrLimitValue));
        }
        PlayerSpecification genderSpec = new PlayerSpecification(new SearchCriteria("gender", ":", gender));
        PlayerSpecification ageRangeSpec = new PlayerSpecification(new SearchCriteria("ageRange", ">", ageRange));
        Specification spec = Specification.where(ustaRatingSpec).and(utrLimitSpec).and(UTRSpec).and(genderSpec).and(ageRangeSpec);

        Page<PlayerEntity> players = playerRepo.findAll(spec, firstPage);

        if (!players.isEmpty()) {
            return ResponseEntity.ok(players.get().collect(Collectors.toList()));
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


        Pageable firstPage = PageRequest.of(0, 1);
        PlayerSpecification ratedOnlySpec = null;
        PlayerSpecification ignoreZeroUTRSpec = null;

        boolean ratedOnly = !ratedOnlyStr.equals("false");
        boolean ignoreZeroUTR = !ignoreZeroUTRStr.equals("false");

        OrderByCriteria orderBy;
        if (type.equalsIgnoreCase("double")) {
            orderBy = new OrderByCriteria("dUTR", false);
            if (ratedOnly) {
                ratedOnlySpec = new PlayerSpecification(new SearchCriteria("dUTRStatus", ":", "Rated"));
            }
            if (ignoreZeroUTR) {
                ignoreZeroUTRSpec = new PlayerSpecification(new SearchCriteria("dUTR", ">", 0.1D));
            }
        } else {
            orderBy = new OrderByCriteria("sUTR", false);
            if (ratedOnly) {
                ratedOnlySpec = new PlayerSpecification(new SearchCriteria("sUTRStatus", ":", "Rated"));
            }
            if (ignoreZeroUTR) {
                ignoreZeroUTRSpec = new PlayerSpecification(new SearchCriteria("sUTR", ">", 0.1D));
            }
        }

        PlayerSpecification ustaRatingSpec = new PlayerSpecification(new SearchCriteria("ustaRating", ":", ustaRating), orderBy);
        PlayerSpecification genderSpec = new PlayerSpecification(new SearchCriteria("gender", ":", gender));
        PlayerSpecification ageRangeSpec = new PlayerSpecification(new SearchCriteria("ageRange", ">", ageRange));

        Specification spec = Specification.where(ustaRatingSpec).and(genderSpec).and(ageRangeSpec);

        if (ratedOnly && ratedOnlySpec!=null) {
            spec = spec.and(ratedOnlySpec);
        }

        if (ignoreZeroUTR && ignoreZeroUTRSpec!=null) {
            spec = spec.and(ignoreZeroUTRSpec);
        }

        Page<PlayerEntity> players = playerRepo.findAll(spec, firstPage);

        if (!players.isEmpty()) {

            Map<String, Object> result = new HashMap<>();
            result.put("totalNumber", String.valueOf(players.getTotalElements()));
            PlayerEntity topPlayer = players.get().collect(Collectors.toList()).get(0);
            result.put("topPlayer", topPlayer);

            Pageable midPage = PageRequest.of(players.getTotalPages()/2, 1);

            players = playerRepo.findAll(spec, midPage);
            PlayerEntity midPlayer = players.get().collect(Collectors.toList()).get(0);
            result.put("midPlayer", midPlayer);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean isUTRId(String name) {

        char c = name.charAt(0);

        return c >= '0' && c <= '9';
    }

    private String reverseName(String name) {

        int index = name.indexOf(' ');
        if (index > 0 && index < name.length() - 1) {
            String first = name.substring(0, index);
            String last = name.substring(index + 1);
            return last + " " + first;
        }

        return name;
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/")
    public PlayerEntity createPlayer(@RequestBody PlayerEntity player) {

        if (player.getUtrId() != null) {
            PlayerEntity existedPlayer = playerRepo.findByUtrId(player.getUtrId());
            if (existedPlayer != null) {
                return existedPlayer;
            }
            Player utrPlayer = loader.getPlayer(player.getUtrId());
            player.setFirstName(utrPlayer.getFirstName());
            player.setLastName(utrPlayer.getLastName());
            player.setGender(utrPlayer.getGender());
        }

        playerRepo.save(player);

        return playerRepo.findByUtrId(player.getUtrId());
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/utr/{id}")
    public ResponseEntity<PlayerEntity> updatePlayerUTR(@PathVariable("id") String utrId,
                                                        @RequestParam(value = "action", defaultValue = "search") String action
    ) {

        if (action.equals("refreshUTR")) {

            PlayerEntity playerData = playerRepo.findByUtrId(utrId);

            if (playerData == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            loader.searchPlayerResult(utrId, false);

            loader.searchPlayerResult(utrId, true);

            Player player = loader.getPlayer(utrId);

            if (player == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            playerData.setSUTR(player.getsUTR());
            playerData.setDUTR(player.getdUTR());
            playerData.setSUTRStatus(player.getsUTRStatus());
            playerData.setDUTRStatus(player.getdUTRStatus());
            playerData.setSuccessRate(player.getSuccessRate());
            playerData.setWholeSuccessRate(player.getWholeSuccessRate());
            playerData.setUtrFetchedTime(new Timestamp(System.currentTimeMillis()));

            playerRepo.save(playerData);

            return new ResponseEntity<>(playerData, HttpStatus.OK);
        }

        if (action.equals("search")) {
            PlayerEntity player = playerRepo.findByUtrId(utrId);

            if (player != null) {
                return ResponseEntity.ok(player);
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/{id}")
    public ResponseEntity<PlayerEntity> updatePlayer(@PathVariable("id") long id, @RequestBody PlayerEntity player) {
        Optional<PlayerEntity> playerData = playerRepo.findById(id);

        if (playerData.isPresent()) {
            PlayerEntity _player = playerData.get();
            _player.setUstaId(player.getUstaId());
            _player.setUtrId(player.getUtrId());

            return new ResponseEntity<>(playerRepo.save(_player), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
