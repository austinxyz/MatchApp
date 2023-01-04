package com.utr.match;


import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.PlayerRepository;
import com.utr.model.PlayerResult;
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
    @GetMapping("/utrid/{utrid}")
    public ResponseEntity<PlayerEntity> searchByUtrId(@PathVariable("utrid") String utrId
    ) {
        PlayerEntity player = playerRepo.findByUtrId(utrId);

        if (player!= null) {
            return ResponseEntity.ok(player);
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
        likeString = "%" + reverseName(name) + "%";
        players.addAll(playerRepo.findByNameLike(likeString));

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
            PlayerResult result = loader.searchPlayerResult(player.getUstaId());
            player.setFirstName(result.getPlayer().getFirstName());
            player.setLastName(result.getPlayer().getLastName());
            player.setGender(result.getPlayer().getGender());
        }

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
