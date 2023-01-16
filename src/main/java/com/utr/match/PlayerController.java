package com.utr.match;


import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.PlayerRepository;
import com.utr.model.Player;
import com.utr.model.PlayerResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

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
    @GetMapping("/search")
    public ResponseEntity<List<PlayerEntity>> searchByName(@RequestParam("name") String name
    ) {
        String likeString = "%" + name + "%";
        List<PlayerEntity> players = playerRepo.findByNameLike(likeString);
        String reverseString = "%" + reverseName(name) + "%";

        if (!likeString.equals(reverseString)) {
            players.addAll(playerRepo.findByNameLike(reverseString));
        }

        if (players.size() > 0) {
            return ResponseEntity.ok(players);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private String reverseName(String name) {

        int index = name.indexOf(' ');
        if (index > 0 && index < name.length()-1) {
            String first = name.substring(0, index);
            String last = name.substring(index+1);
            return last + " " + first;
        }

        return name;
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/")
    public PlayerEntity createPlayer(@RequestBody PlayerEntity player) {

        if (player.getUtrId() != null) {
            PlayerEntity existedPlayer = playerRepo.findByUtrId(player.getUtrId());
            if (existedPlayer !=null) {
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
                                                        @RequestParam("action") String action
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

            playerData.setsUTR(player.getsUTR());
            playerData.setdUTR(player.getdUTR());
            playerData.setsUTRStatus(player.getsUTRStatus());
            playerData.setdUTRStatus(player.getdUTRStatus());
            playerData.setSuccessRate(player.getSuccessRate());
            playerData.setWholeSuccessRate(player.getWholeSuccessRate());
            playerData.setUtrFetchedTime(new Timestamp(System.currentTimeMillis()));

            playerRepo.save(playerData);

            return new ResponseEntity<>(playerData, HttpStatus.OK);
        }

        if (action.equals("search")) {
            PlayerEntity player = playerRepo.findByUtrId(utrId);

            if (player!= null) {
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
