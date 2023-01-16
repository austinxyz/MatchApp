package com.utr.match.usta;

import com.utr.match.TeamLoader;
import com.utr.match.entity.*;
import com.utr.model.Division;
import com.utr.model.Event;
import com.utr.model.Player;
import com.utr.model.PlayerEvent;
import com.utr.parser.UTRParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("singleton")
public class USTATeamImportor {

    private static final Logger logger = LoggerFactory.getLogger(USTATeamImportor.class);

    @Autowired
    EventRepository eventRepo;

    @Autowired
    USTASiteParser ustaParser;

    @Autowired
    private USTADivisionRepository divisionRepository;

    @Autowired
    private USTATeamRepository ustaTeamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamLoader loader;

    public USTATeamImportor() {
    }

    public USTATeam importUSTATeam(String teamURL) {
        USTATeam team = createTeamAndAddPlayers(teamURL);
        updatePlayerUSTANumber(teamURL);
        return team;
    }

    public List<USTATeam> importUSTAFlight(String flightURL) {

        USTASiteParser util = new USTASiteParser();
        List<USTATeam> teams = new ArrayList<>();

        try {
            for (String teamURL: util.parseUSTAFlight(flightURL)) {
                teams.add(importUSTATeam(teamURL));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return teams;
    }

    private USTATeam createTeamAndAddPlayers(String teamURL) {

        USTASiteParser util = new USTASiteParser();
        try {
            USTATeam team = util.parseUSTATeam(teamURL);

            USTATeam existTeam = ustaTeamRepository.findByName(team.getName());

            if (existTeam != null) {
                logger.debug("team " + team.getName() + " is existed");
            } else {
                USTATeam newTeam = new USTATeam();
                newTeam.setName(team.getName());
                newTeam.setAlias(team.getAlias());
                newTeam.setLink(teamURL);
                newTeam.setArea(team.getArea());
                newTeam.setFlight(team.getFlight());
                USTADivision division = divisionRepository.findByName(team.getDivisionName());
                if (division!=null) {
                    newTeam.setDivision(division);
                }

                ustaTeamRepository.save(newTeam);
                existTeam = ustaTeamRepository.findByName(newTeam.getName());
                logger.debug("new team " + team.getName() + " is created");
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
                    logger.debug(player.getName() + " is existed, update USTA info");
                } else {
                    playerRepository.save(player);
                    existedPlayer = playerRepository.findByNameLike(player.getName()).get(0);
                    logger.debug("new player " + player.getName() + " is created");
                }

                if (existTeam.getPlayer(existedPlayer.getName()) == null) {
                    existTeam.getPlayers().add(existedPlayer);
                    logger.debug(" add player " + player.getName() + " into team");
                }
            }

            if (existTeam.getCaptain() == null) {
                List<PlayerEntity> captains = playerRepository.findByNameLike(team.getCaptainName());
                if (captains.size() > 0) {
                    existTeam.setCaptain(captains.get(0));
                }
            }
            ustaTeamRepository.save(existTeam);
            return existTeam;
        } catch (IOException e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }

    }

    public void updateTeamPlayerUTRID(String teamName) {

        USTATeam existTeam = ustaTeamRepository.findByName(teamName);

        if (existTeam == null) {
            logger.debug("team " + existTeam.getName() + " is not existed");
            return;
        }

        updateTeamPlayersUTRID(existTeam);


    }

    public void updateTeamPlayersUTRID(USTATeam existTeam) {
        for (PlayerEntity player : existTeam.getPlayers()) {

            List<Player> utrplayers = loader.queryPlayer(player.getName(), 5);

            if (player.getUtrId() == null) {
                String candidateUTRId = findUTRID(utrplayers, player);
                if (candidateUTRId != null) {
                    player.setUtrId(candidateUTRId);
                    playerRepository.save(player);
                    logger.debug("Player:" + player.getName() + " utr: " + player.getUtrId() + " Saved ");
                } else {
                    logger.debug("Player:" + player.getName() + " has no UTRId");
                }

            }

        }
    }

    public void updateTeamUTRInfo(String teamName) {

        USTATeam existTeam = ustaTeamRepository.findByName(teamName);

        if (existTeam == null) {
            logger.debug("team " + existTeam.getName() + " is not existed");
            return;
        }

        for (PlayerEntity player : existTeam.getPlayers()) {

            if (player.isRefreshedUTR()) {
                logger.debug(player.getName() + " has latest UTR, skip");
                continue;
            }

            String utrId = player.getUtrId();

            if (utrId == null || utrId.equals("")) {
                logger.debug(player.getName() + " has no UTR, no need to refresh");
                continue;
            }

            logger.debug(player.getName() + " start to query utr and win ratio" );

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

            playerRepository.save(player);

            logger.debug(player.getName() + " utr is updated" );


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

    private void updatePlayerUSTANumber(String teamURL) {

        USTASiteParser util = new USTASiteParser();
        try {
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
                    player.getTennisRecordLink();
                }

                playerRepository.save(player);
                logger.debug("Player:" + player.getName() + " usta ID: " + player.getUstaId() + " Saved ");
            }

        } catch (IOException e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }

    }


}
