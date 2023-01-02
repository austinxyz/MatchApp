package com.utr.match;


import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    PlayerRepository playerRepo;

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
    @PostMapping("/")
    public PlayerEntity createPlayer(@RequestBody PlayerEntity player) {
        return playerRepo.save(player);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerEntity> updatePlayer(@PathVariable("id") long id, @RequestBody PlayerEntity player) {
        Optional<PlayerEntity> playerData = playerRepo.findById(id);

        if (playerData.isPresent()) {
            PlayerEntity _player = playerData.get();
            _player.setFirstName(player.getFirstName());
            _player.setLastName(player.getLastName());
            _player.setBirthYear(player.getBirthYear());
            _player.setBirthMonth(player.getBirthMonth());
            _player.setUstaId(player.getUstaId());
            _player.setUtrId(player.getUtrId());
            _player.setUstaRating(player.getUstaRating());
            _player.setArea(player.getArea());
            _player.setGender(player.getGender());
            _player.setLefty(player.isLefty());
            _player.setMemo(player.getMemo());
            _player.setSummary(player.getSummary());

            return new ResponseEntity<>(playerRepo.save(_player), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
