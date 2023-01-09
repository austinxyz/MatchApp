package com.utr.entity;

import com.utr.match.TeamLoader;
import com.utr.match.entity.*;
import com.utr.model.Player;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class USTATeamRepositoryTest {


    @Autowired
    private USTADivisionRepository divisionRepository;

    @Autowired
    private USTATeamRepository ustaTeamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamLoader loader;

    @Test
    void createTeam() {
        Optional<USTADivision> division = divisionRepository.findById(1L);

        if(division.isPresent()) {

            USTATeam team = new USTATeam("VALLEY CHURCH 40AM3.5A", division.get());

            team.setAlias("Federoar");
            team.setArea("South Bay");
            team.setFlight("2");

            ustaTeamRepository.save(team);

        }

    }

    @Test
    void addPlayer() {

        String playerName = "3695913";

        List<PlayerEntity> players = playerRepository.findByNameLike(playerName);

        USTATeam team = ustaTeamRepository.findById(1L).get();

        PlayerEntity player = null;
        if (!players.isEmpty()) {
            player = players.get(0);
        } else {
            List<Player> utrPlayers = loader.queryPlayer(playerName, 5);

            if (!utrPlayers.isEmpty()) {
               Player utrPlayer = utrPlayers.get(0);
               player = new PlayerEntity();
               player.setFirstName(utrPlayer.getFirstName());
               player.setLastName(utrPlayer.getLastName());
               player.setName(utrPlayer.getName());
               player.setGender(utrPlayer.getGender());
               player.setUtrId(utrPlayer.getId());
            }

            player = playerRepository.save(player);
        }

        if (player != null) {
            //Hibernate.initialize(team.getPlayers());
            team.getPlayers().add(player);
            ustaTeamRepository.save(team);
            System.out.println(player);
        }
    }

    @Test
    void getTeam() {
        USTATeam team = ustaTeamRepository.findById(1L).get();

        for (PlayerEntity player: team.getPlayers()) {

            System.out.println(player.getName() + ": update player set usta_noncal_link='https://www.ustanorcal.com/playermatches.asp?id=', usta_tennisrecord_link='https://www.tennisrecord.com/adult/profile.aspx?playername=' where id=" + player.getId());

        }

    }

}