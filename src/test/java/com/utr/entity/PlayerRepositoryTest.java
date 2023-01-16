package com.utr.entity;

import com.utr.match.TeamLoader;
import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.PlayerRepository;
import com.utr.match.usta.USTATeamImportor;
import com.utr.model.Player;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.List;

@SpringBootTest
class PlayerRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(PlayerRepositoryTest.class);

    @Autowired
    private PlayerRepository playerRepo;

    @Autowired
    TeamLoader loader;

    @Test
    void findAll() {
        List<PlayerEntity> players = playerRepo.findAll();

        for (PlayerEntity player : players) {

            if (player.isRefreshedUTR()) {
                System.out.println(player.getName() + " has latest UTR, skip");
                continue;
            }

            String utrId = player.getUtrId();

            if (utrId == null || utrId.equals("")) {
                System.out.println(player.getName() + " has no UTR, no need to refresh");
                continue;
            }

            System.out.println(player.getName() + " start to query utr and win ratio" );

            loader.searchPlayerResult(utrId, false);

            loader.searchPlayerResult(utrId, true);

            Player utrplayer = loader.getPlayer(utrId);

            if (utrplayer == null) {
                continue;
            }


            player.setsUTR(utrplayer.getsUTR());
            player.setdUTR(utrplayer.getdUTR());
            player.setsUTRStatus(utrplayer.getsUTRStatus());
            player.setdUTRStatus(utrplayer.getdUTRStatus());
            player.setSuccessRate(utrplayer.getSuccessRate());
            player.setWholeSuccessRate(utrplayer.getWholeSuccessRate());
            player.setUtrFetchedTime(new Timestamp(System.currentTimeMillis()));

            playerRepo.save(player);

            System.out.println(player.getName() + " utr is updated" );
        }
    }

    @Test
    void findByUTR() {
        PlayerEntity player = playerRepo.findByUtrId("1316122");
        System.out.println(player.getFirstName() + " " + player.getLastName());

    }


}