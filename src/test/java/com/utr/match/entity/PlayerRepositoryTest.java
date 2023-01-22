package com.utr.match.entity;

import com.utr.match.TeamLoader;
import com.utr.model.Player;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.transaction.Transactional;
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
    void updateUTRByNotFetechedPlayers() {
        List<PlayerEntity> players = playerRepo.findByUtrFetchedTimeNull();

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


            player.setDUTR(utrplayer.getsUTR());
            player.setDUTR(utrplayer.getdUTR());
            player.setDUTRStatus(utrplayer.getsUTRStatus());
            player.setDUTRStatus(utrplayer.getdUTRStatus());
            player.setSuccessRate(utrplayer.getSuccessRate());
            player.setWholeSuccessRate(utrplayer.getWholeSuccessRate());
            player.setUtrFetchedTime(new Timestamp(System.currentTimeMillis()));

            playerRepo.save(player);

            System.out.println(player.getName() + " utr is updated" );
        }
    }

    @Test
    void updateNorCalUSTAId() {
        List<PlayerEntity> players = playerRepo.findByNoncalLinkNotNullAndUstaNorcalIdNull();

        for (PlayerEntity player : players) {

            String norcalLink = player.getNoncalLink();

            int start = norcalLink.indexOf("=");

            if (start > 0) {
                String ustaId = norcalLink.substring(start+1, norcalLink.length());

                player.setUstaNorcalId(ustaId);

                playerRepo.save(player);

                System.out.println(player.getName() + " norcal id " + player.getUstaNorcalId() + " is saved" );
            }


        }
    }

    @Test
    @Transactional
    void findByUTR() {
        PlayerEntity player = playerRepo.findByUtrId("1316122");
        System.out.println(player.getFirstName() + " " + player.getLastName());

        for (USTATeam team: player.getTeams()) {
            System.out.println(team.getName());
        }

    }

    @Test
    void findHighUTRPlayerswithUSTARating() {
        Pageable firstPage = PageRequest.of(0,10);
        PlayerSpecification ustaRating = new PlayerSpecification(new SearchCriteria("ustaRating", ":", "3.5C"));
        PlayerSpecification dUTR = new PlayerSpecification(new SearchCriteria("dUTR", ">", Double.valueOf("5.5")), new OrderByCriteria("dUTR", false));
        PlayerSpecification gender = new PlayerSpecification(new SearchCriteria("gender", ":", "M"));
        Page<PlayerEntity> players = playerRepo.findAll(Specification.where(ustaRating).and(dUTR).and(gender), firstPage);

        for (PlayerEntity player: players) {
            System.out.println(player.getName() + " " + player.getDUTR());
        }

    }
}