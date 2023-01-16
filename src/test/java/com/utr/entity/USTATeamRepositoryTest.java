package com.utr.entity;

import com.utr.match.TeamLoader;
import com.utr.match.entity.*;
import com.utr.model.Player;
import com.utr.parser.UTRParser;
import com.utr.match.usta.USTASiteParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
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

        if (division.isPresent()) {

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

        System.out.println(team.getAreaCode());

        for (PlayerEntity player : team.getPlayers()) {

            System.out.println(player.getName() + ": update player set usta_noncal_link='https://www.ustanorcal.com/playermatches.asp?id=', usta_tennisrecord_link='https://www.tennisrecord.com/adult/profile.aspx?playername=' where id=" + player.getId());

        }

    }

    @Test
    void createTeamAndAddPlayers() {

        USTASiteParser util = new USTASiteParser();
        try {
            String teamURL = "https://www.ustanorcal.com/teaminfo.asp?id=96701";
            USTATeam team = util.parseUSTATeam(teamURL);

            USTATeam existTeam = ustaTeamRepository.findByName(team.getName());

            if (existTeam != null) {
                System.out.println("team " + team.getName() + " is existed");
            } else {
                USTATeam newTeam = new USTATeam();
                newTeam.setName(team.getName());
                newTeam.setAlias(team.getAlias());
                newTeam.setLink(teamURL);
                USTADivision division = divisionRepository.findByName(team.getDivisionName());
                if (division!=null) {
                    newTeam.setDivision(division);
                }
                ustaTeamRepository.save(newTeam);
                existTeam = ustaTeamRepository.findByName(newTeam.getName());
                System.out.println("new team " + team.getName() + " is created");
            }

            for (PlayerEntity player : team.getPlayers()) {
                List<PlayerEntity> existedPlayers = playerRepository.findByNameLike(player.getName());
                PlayerEntity existedPlayer = null;

                if (existedPlayers.size() > 0) {
                    existedPlayer = existedPlayers.get(0);
                    existedPlayer.setNoncalLink(player.getNoncalLink());
                    existedPlayer.setArea(player.getArea());
                    existedPlayer.setUstaRating(player.getUstaRating());
                    playerRepository.save(existedPlayer);
                    System.out.println(player.getName() + " is existed, update USTA info");
                } else {
                    playerRepository.save(player);
                    existedPlayer = playerRepository.findByNameLike(player.getName()).get(0);
                    System.out.println("new player" + player.getName() + " is created");
                }

                if (existTeam.getPlayer(existedPlayer.getName()) == null) {
                    existTeam.getPlayers().add(existedPlayer);
                    System.out.println(" add player " + player.getName() + " into team");
                }
            }
            ustaTeamRepository.save(existTeam);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void updateTeamPlayerUTRID() {

        UTRParser parser = new UTRParser();

        USTATeam existTeam = ustaTeamRepository.findById(3L).get();

        if (existTeam == null) {
            System.out.println("team " + existTeam.getName() + " is not existed");
            return;
        }

        for (PlayerEntity player : existTeam.getPlayers()) {

            List<Player> utrplayers = parser.searchPlayers(player.getName(), 5);

            if (player.getUtrId() == null) {
                String candidateUTRId = findUTRID(utrplayers, player);
                if (candidateUTRId != null) {
                    player.setUtrId(candidateUTRId);
                    playerRepository.save(player);
                    System.out.println("Player:" + player.getName() + " utr: " + player.getUtrId() + " Saved ");
                } else {
                    System.out.println("Player:" + player.getName() + " has no UTRId");
                }

            }

        }


    }

    private String findUTRID(List<Player> players, PlayerEntity player) {
        if (players.size() == 1) {
            return players.get(0).getId();
        }

        List<String> candidateUTRIds = new ArrayList<>();
        for (Player utrPlayer : players) {
            if (!utrPlayer.getGender().equals(player.getGender())) {
                continue;
            }

            if (utrPlayer.getUTR() < 0.1) {
                continue;
            }

            if (utrPlayer.getLocation() == null || utrPlayer.getLocation().indexOf("CA") < 0) {
                continue;
            }

            candidateUTRIds.add(utrPlayer.getId());
        }

        return candidateUTRIds.size() > 0 ? candidateUTRIds.get(0) : "";
    }

    @Test
    void updatePlayerUSTANumber() {

        USTASiteParser util = new USTASiteParser();
        try {
            String teamURL = "https://www.ustanorcal.com/teaminfo.asp?id=96701";
            USTATeam team = util.parseUSTATeam(teamURL);

            USTATeam existTeam = ustaTeamRepository.findByName(team.getName());

            for (PlayerEntity player : existTeam.getPlayers()) {

                if (player.getNoncalLink() == null) {
                    PlayerEntity newPlayer = team.getPlayer(player.getName());

                    if (newPlayer != null) {
                        player.setUstaRating(newPlayer.getUstaRating());
                        player.setArea(newPlayer.getArea());
                        player.setNoncalLink(newPlayer.getNoncalLink());
                    }
                }

                if (player.getNoncalLink() != null) {
                    player.setUstaId(util.parseUSTANumber(player.getNoncalLink()));
                    player.setTennisRecordLink(getTennisRecordLink(player));
                }

                playerRepository.save(player);
                System.out.println("Player:" + player.getName() + " usta ID: " + player.getUstaId() + " Saved ");

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private String getTennisRecordLink(PlayerEntity player) {
        return "https://www.tennisrecord.com/adult/profile.aspx?playername=" + player.getFirstName()
                + "%20" + player.getLastName();
    }

    @Test
    void updateCaptainArea() {

        USTASiteParser util = new USTASiteParser();
        try {


            List<USTATeam> existTeams = ustaTeamRepository.findAll();

            for (USTATeam existTeam : existTeams) {
               if (existTeam.getCaptain() == null ) {
                   USTATeam team = util.parseUSTATeam(existTeam.getLink());
                   existTeam.setArea(team.getArea());
                   existTeam.setFlight(team.getFlight());
                   List<PlayerEntity> captains = playerRepository.findByNameLike(team.getCaptainName());
                   if (captains.size() > 0) {
                       existTeam.setCaptain(captains.get(0));
                   }
                   ustaTeamRepository.save(existTeam);
               }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}