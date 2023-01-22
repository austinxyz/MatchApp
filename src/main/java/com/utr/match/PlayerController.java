package com.utr.match;


import com.utr.match.entity.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    PlayerRepository playerRepo;

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
                                                          @RequestParam("utr") String utrValue,
                                                          @RequestParam(value = "type", defaultValue = "double") String type,
                                                          @RequestParam(value = "gender", defaultValue = "M") String gender,
                                                          @RequestParam(value = "start", defaultValue = "0") int start,
                                                          @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable firstPage = PageRequest.of(start, size);
        PlayerSpecification ustaRatingSpec = new PlayerSpecification(new SearchCriteria("ustaRating", ":", ustaRating));
        PlayerSpecification UTRSpec;
        if (type.equalsIgnoreCase("double")) {
            UTRSpec = new PlayerSpecification(new SearchCriteria("dUTR", ">", Double.valueOf(utrValue)),
                    new OrderByCriteria("dUTR", false));
        } else {
            UTRSpec = new PlayerSpecification(new SearchCriteria("sUTR", ">", Double.valueOf(utrValue)),
                    new OrderByCriteria("sUTR", false));
        }
        PlayerSpecification genderSpec = new PlayerSpecification(new SearchCriteria("gender", ":", gender));
        Page<PlayerEntity> players = playerRepo.findAll(Specification.where(ustaRatingSpec).and(UTRSpec).and(genderSpec), firstPage);

        if (!players.isEmpty()) {
            return ResponseEntity.ok(players.get().collect(Collectors.toList()));
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
