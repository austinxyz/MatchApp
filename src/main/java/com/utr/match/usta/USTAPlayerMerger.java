package com.utr.match.usta;

import com.utr.match.entity.*;
import com.utr.model.Player;
import com.utr.parser.UTRParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Scope("singleton")
public class USTAPlayerMerger {

    private static final Logger logger = LoggerFactory.getLogger(USTAPlayerMerger.class);

    @Autowired
    private PlayerRepository playerRepo;

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private USTACandidateTeamRepository candidateTeamRepository;

    @Autowired
    private USTACandidateRepository candidateRepository;

    @Autowired
    private UTRTeamRepository utrTeamRepository;

    @Autowired
    private UTRTeamCandidateRepository utrTeamCandidateRepository;

    @Autowired
    private UTRTeamMemberRepository utrTeamMemberRepository;
    public USTAPlayerMerger() {
    }

    public boolean mergePlayer(String utr) {

        String utrId = utr;

        List<PlayerEntity> players = playerRepo.findByUtrId(utrId);

        if (players == null || players.size() < 2) {
            return false;
        }

        PlayerEntity primaryPlayer = findPrimaryPlayer(players);

        if (primaryPlayer == null) {
            return false;
        }

        for (PlayerEntity player: players) {

            if (player.getId() == primaryPlayer.getId()) {
                continue;
            }

            merge(primaryPlayer, player);
        }

        return true;

    }

    public boolean mergePlayer(String primaryId, String playerId) {

        Optional<PlayerEntity> playerEntity1 = playerRepo.findById(Long.valueOf(primaryId));

        if (!playerEntity1.isPresent()) {
            return false;
        }

        Optional<PlayerEntity> playerEntity2 = playerRepo.findById(Long.valueOf(playerId));

        if (!playerEntity2.isPresent()) {
            return false;
        }

        PlayerEntity player = playerEntity2.get();
        PlayerEntity primaryPlayer = playerEntity1.get();

        if (primaryPlayer.getUstaId() == null || primaryPlayer.getUstaId().trim().equals("") ) {
            primaryPlayer = player;
            player = playerEntity1.get();
        }

        primaryPlayer = copyFromPlayer(primaryPlayer, player);

        merge(primaryPlayer, player);

        return true;
    }

    private PlayerEntity copyFromPlayer(PlayerEntity primaryPlayer, PlayerEntity player) {
        if (primaryPlayer.getUstaId() == null || primaryPlayer.getUstaId().trim().equals("")) {
            return null;
        }

        if (primaryPlayer.getUtrId() == null) {
            primaryPlayer.setUtrId(player.getUtrId());
        }

        if (!primaryPlayer.getUtrId().equals(player.getUtrId())) {
            primaryPlayer.setUtrId(player.getUtrId());
        }

        return primaryPlayer;
    }

    private void merge(PlayerEntity primaryPlayer, PlayerEntity player) {

        if (primaryPlayer == null) {
            return;
        }

        boolean dryRun = false;

        logger.info("Start to merge player id :" + player.getId() + " to " + primaryPlayer.getId());

        //merge Division
        logger.info("Merge Division...");
        Optional<DivisionEntity> divisionOpt = divisionRepository.findByPlayers_Id(player.getId());
        if (divisionOpt.isPresent()) {
            DivisionEntity division = divisionOpt.get();
            division.removePlayer(player.getId());
            division.getPlayers().add(primaryPlayer);
            if (!dryRun) {
                divisionRepository.save(division);
            } else {
                logger.info("Found Division:" + division.getName() + " remove player id:" + player.getId()
                        + " add player id:" + primaryPlayer.getId());
            }
        } else {
            logger.info("No division to merge");
        }

        //merge Event UTR
        logger.info("Merge Event UTR...");
        Set<EventUTR> utrs = player.getUtrs();
        if (!utrs.isEmpty()) {

            for (EventUTR utr : utrs) {
                utr.setPlayer(primaryPlayer);
            }

            primaryPlayer.setUtrs(utrs);
            primaryPlayer.setUtrId(player.getUtrId());
            player.setUtrs(new HashSet<>());

            if (!dryRun) {
                playerRepo.save(primaryPlayer);
                playerRepo.save(player);
            }
            logger.info("There are event UTR, switch owner from " + player.getId()
                        + " to " + primaryPlayer.getId());
        } else {
            logger.info("No event UTR to merge");
        }

        //merge USTA Candidate Team captain
        logger.info("Merge USTA Candidate Team captain...");
        List<USTACandidateTeam> candidateTeams = candidateTeamRepository.findByCaptain_Id(player.getId());
        if (candidateTeams!=null) {
            //change captain as primary player
            for(USTACandidateTeam candidateTeam:candidateTeams) {
                candidateTeam.setCaptain(primaryPlayer);
                if (!dryRun) {
                    candidateTeamRepository.save(candidateTeam);
                }
                logger.info("Change candidate team: " + candidateTeam.getName() + "'s captain from " + player.getId()
                            + " to " + primaryPlayer.getId());
            }
        } else {
            logger.info("No USTA Candidate Team captain to merge");
        }

        //merge USTA Candidate
        logger.info("Merge USTA Candidate...");
        List<USTACandidate> candidates = candidateRepository.findByPlayer_Id(player.getId());
        if (candidates!=null) {
            for (USTACandidate candidate: candidates) {
                candidate.setPlayer(primaryPlayer);
                if (!dryRun) {
                    candidateRepository.save(candidate);
                }
                logger.info("Change candidate team member: " + candidate.getName() + " from " + player.getId()
                            + " to " + primaryPlayer.getId());
            }
        } else {
            logger.info("No USTA Candidate to merge");
        }

        //merge UTR Candidate Team captain
        logger.info("Merge UTR Candidate...");
        List<UTRTeamCandidate> utrCandidates = utrTeamCandidateRepository.findByPlayer_Id(player.getId());
        if (utrCandidates!=null) {
            //change captain as primary player
            for(UTRTeamCandidate utrCandidate :utrCandidates) {
                utrCandidate.setPlayer(primaryPlayer);
                if (!dryRun) {
                    utrTeamCandidateRepository.save(utrCandidate);
                } logger.info("Change utr team candidate member : " + utrCandidate.getName() + " from " + player.getId()
                            + " to " + primaryPlayer.getId());
            }
        } else {
            logger.info("No UTR Candidate to merge");
        }

        //merge UTR Team captain
        logger.info("Merge UTR Team captain...");
        List<UTRTeamEntity> utrTeams = utrTeamRepository.findByCaptain_Id(player.getId());
        if (utrTeams!=null) {
            //change captain as primary player
            for(UTRTeamEntity utrTeam:utrTeams) {
                utrTeam.setCaptain(primaryPlayer);
                if (!dryRun) {
                    utrTeamRepository.save(utrTeam);
                }
                logger.info("Change utr team: " + utrTeam.getName() + "'s captain from " + player.getId()
                            + " to " + primaryPlayer.getId());
            }
        } else {
            logger.info("No UTR Team captain to merge");
        }

        //merge USTA Candidate
        logger.info("Merge UTR Team member...");
        List<UTRTeamMember> members = utrTeamMemberRepository.findByPlayer_Id(player.getId());
        if (members!=null) {
            for (UTRTeamMember member: members) {
                member.setPlayer(primaryPlayer);
                if (!dryRun) {
                    utrTeamMemberRepository.save(member);
                }
                logger.info("Change UTR team member: " + member.getName() + " from " + player.getId()
                            + " to " + primaryPlayer.getId());
            }
        } else {
            logger.info("No UTR Team member to merge");
        }

        if (!dryRun) {
            playerRepo.delete(player);
            logger.info("player " + player.getName() + " with id " +  player.getId() +" is deleted");
        } else {
            logger.info("player " + player.getName() + " with id " +  player.getId() +" to be deleted");
        }

        System.out.println(player.getName() +"'s merge is completed");
    }

    private PlayerEntity findPrimaryPlayer(List<PlayerEntity> players) {

        if (players == null) {
            return null;
        }

        PlayerEntity primaryPlayer = null;

        for (PlayerEntity player: players) {
            if (player.getUstaId()!=null) {

                if (primaryPlayer!=null) {
                    logger.info("Can not merge: multiple records with same utr has usta info:" + primaryPlayer.getName());
                    return null;
                }

                primaryPlayer = player;
            }
        }

        return primaryPlayer;
    }
}
