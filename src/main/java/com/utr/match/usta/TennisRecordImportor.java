package com.utr.match.usta;

import com.utr.match.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("singleton")
public class TennisRecordImportor {

    private static final Logger logger = LoggerFactory.getLogger(TennisRecordImportor.class);
    @Autowired
    USTASiteParser ustaParser;

    @Autowired
    private USTATeamRepository ustaTeamRepository;

    @Autowired
    private USTATeamMemberRepository ustaTeamMemberRepository;
    @Autowired
    private PlayerRepository playerRepository;

    public USTATeamEntity importUSTATeam(String tennisRecordLink, String teamName) {
        USTASiteParser util = new USTASiteParser();
        try {
            USTATeamEntity existTeam = fetchOrCreateTeam(tennisRecordLink, teamName);

            List<PlayerEntity> players = util.getTeamDynamicRating(tennisRecordLink);

            existTeam = addPlayers(existTeam, players);

            return existTeam;
        } catch (IOException e) {
            logger.error(e.toString());

            return null;
            //throw new RuntimeException(e);
        }
    }

    public USTATeamEntity importUSTATeamFromText(String playerInfo, String teamName) {
        USTASiteParser util = new USTASiteParser();
            USTATeamEntity existTeam = fetchOrCreateTeam(playerInfo, teamName);

            List<PlayerEntity> players = util.parseTeamMembers(playerInfo);

            existTeam = addPlayers(existTeam, players);
            return existTeam;

    }

    private USTATeamEntity createTeamAndAddPlayers(String tennisRecordLink, String teamName) {

        USTASiteParser util = new USTASiteParser();
        try {
            USTATeamEntity existTeam = fetchOrCreateTeam(tennisRecordLink, teamName);

            List<PlayerEntity> players = util.getTeamDynamicRating(tennisRecordLink);

            existTeam = addPlayers(existTeam, players);
            return existTeam;
        } catch (IOException e) {
            logger.error(e.toString());

            return null;
            //throw new RuntimeException(e);
        }

    }

    private USTATeamEntity fetchOrCreateTeam(String tennisRecordLink, String teamName) {
        List<USTATeamEntity> existTeams = ustaTeamRepository.findByName(teamName);
        USTATeamEntity existTeam = null;

        if (existTeams.size() > 0) {
            existTeam = existTeams.get(0);
        } else {
            USTATeamEntity newTeam = new USTATeamEntity();
            newTeam.setName(teamName);
            newTeam.setTennisRecordLink(tennisRecordLink);
            existTeam = ustaTeamRepository.save(newTeam);
            logger.debug("new team " + existTeam.getName() + " with id: " + existTeam.getId() + " is created");
        }
        return existTeam;
    }

    private USTATeamEntity addPlayers(USTATeamEntity existTeam, List<PlayerEntity> players) {
        for (PlayerEntity player : players) {

            PlayerEntity existedPlayer = null;

            List<PlayerEntity> existedPlayers = playerRepository.findByName(player.getName());

            if (existedPlayers != null && existedPlayers.size() >0) {
                existedPlayer = existedPlayers.get(0);
            }

            if (existedPlayer != null) {
                existedPlayer.setDynamicRating(player.getDynamicRating());
                existedPlayer.setUstaRating(player.getUstaRating());
                existedPlayer = playerRepository.save(existedPlayer);
            } else {
                existedPlayer = playerRepository.save(player);
                logger.debug("new player " + player.getName() + " is created");
            }

            if (existTeam.getPlayer(existedPlayer.getName()) == null) {
                USTATeamMember member = new USTATeamMember();
                member.setPlayer(existedPlayer);
                member.setTeam(existTeam);
                //member = ustaTeamMemberRepository.save(member);
                existTeam.addPlayer(member);
                logger.debug(" add player " + player.getName() + " into team");
            }
        }

        existTeam = ustaTeamRepository.save(existTeam);
        return existTeam;
    }
}
