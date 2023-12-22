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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SpringBootTest
class PlayerRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(PlayerRepositoryTest.class);

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

            loader.searchPlayerResult(utrId, false, true);

            loader.searchPlayerResult(utrId, true, true);

            Player utrplayer = loader.getPlayer(utrId, true);

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
        PlayerEntity player = playerRepo.findByUtrId("1316122").get(0);
        System.out.println(player.getFirstName() + " " + player.getLastName());

        for (USTATeamMember member: player.getTeamMembers()) {
            System.out.println(member.getTeam().getName());
        }

    }

    @Test
    void findHighUTRPlayerswithUSTARating() {
        Pageable firstPage = PageRequest.of(0,10);
        PlayerSpecification ustaRatingSpec = new PlayerSpecification(new SearchCriteria("ustaRating", ":", "4.0"));
        PlayerSpecification UTRSpec = new PlayerSpecification(new SearchCriteria("dUTR", ">", Double.valueOf("0.5")), new OrderByCriteria("dUTR", false));
        PlayerSpecification genderSpec = new PlayerSpecification(new SearchCriteria("gender", ":", "M"));
        PlayerSpecification ageRangeSpec = new PlayerSpecification(new SearchCriteria("ageRange", ">", "40+"));

        PlayerSpecification    ratedOnlySpec = new PlayerSpecification(new SearchCriteria("dUTRStatus", ":", "Rated"));

        PlayerSpecification    ignoreZeroUTRSpec = new PlayerSpecification(new SearchCriteria("dUTR", ">", 0.1D));

        Specification spec = Specification.where(ustaRatingSpec).and(UTRSpec).and(genderSpec).and(ageRangeSpec);

        spec.and(ratedOnlySpec).and(ignoreZeroUTRSpec);



        Page<PlayerEntity> players = playerRepo.findAll(spec, firstPage);

        System.out.println(players.getTotalPages());

        System.out.println(players.getTotalElements());

        Pageable midPage = PageRequest.of(players.getTotalPages()/2, 10);

        players = playerRepo.findAll(spec, midPage);


        for (PlayerEntity player: players) {
            System.out.println(player.getName() + " " + player.getDUTR());
            System.out.println(player.getName() + " " + player.getDUTRStatus());
        }

    }

    @Test
    void fixFullNameIssues() {

//        for (PlayerEntity player: playerRepo.findAll()) {
//            if (player.getId() > 2L) {
//                return;
//            }
//            String name = player.getLastName().trim() + " " + player.getFirstName().trim();
//            if (!player.getName().equals(name)) {
//                player.setName(name);
//                playerRepo.save(player);
//                System.out.println(player.getName() + " full name is updated");
//            }
//        }

        //String fullnameList = "Lee Tzong-Han";
        String ids=
        //"Vasseghi Jing,"+
        "908248,886674,3542931,2718355,737470,1947802,2991444,3114695,3024263,1906109";

        for (String id: ids.split(",")) {

            List<PlayerEntity> players = playerRepo.findByUtrId(id);

            PlayerEntity primaryPlayer = null;

            for(PlayerEntity player: players) {
                if (player.getUstaId() != null && !player.getUstaId().trim().equals("")) {
                    primaryPlayer = player;
                    break;
                }
            }

            if (primaryPlayer != null) {
                for(PlayerEntity player: players) {
                    if (player.getId() == primaryPlayer.getId()) {
                        continue;
                    }

                    DivisionEntity division = divisionRepository.findByPlayers_Id(player.getId()).get();

                    if (division == null) {
                        continue;
                    }
                    division.removePlayer(player.getId());
                    division.getPlayers().add(primaryPlayer);
                    divisionRepository.save(division);

                    Set<EventUTR> utrs = player.getUtrs();

                    for (EventUTR utr: utrs) {
                        utr.setPlayer(primaryPlayer);
                    }

                    primaryPlayer.setUtrs(utrs);
                    primaryPlayer.setUtrId(player.getUtrId());
                    player.setUtrs(new HashSet<>());

                    playerRepo.save(primaryPlayer);
                    playerRepo.save(player);

                    System.out.println(player.getName() +"'s update is completed");
                }
            }

        }

    }

    @Test
    void verifyFullNameIssues() {

        String fullnameList=
                "Vasseghi Jing,"+
                "Yang Chuo-Yun";

        for (String playerName: fullnameList.split(",")) {
            playerName = playerName.trim();

            List<PlayerEntity> players = playerRepo.findByName(playerName);

            PlayerEntity primaryPlayer = null;

            for(PlayerEntity player: players) {
                if (player.getUstaId() != null && !player.getUstaId().trim().equals("")) {
                    primaryPlayer = player;
                    break;
                }
            }

            if (primaryPlayer != null) {
                for(PlayerEntity player: players) {
                    if (player.getId() == primaryPlayer.getId()) {
                        DivisionEntity division = divisionRepository.findByPlayers_Id(player.getId()).get();
                        System.out.println(division.getName());
                        System.out.println(player.getUtrs());
                        System.out.println(playerName +"'s update is completed");
                    }

                }
            }

        }

    }

    @Test
    void cleanFullNameIssues() {

        String ids=
                //"Vasseghi Jing,"+
                "908248,886674,3542931,2718355,737470,1947802,2991444,3114695,3024263,1906109";

        for (String id: ids.split(",")) {


            List<PlayerEntity> players = playerRepo.findByUtrId(id);

            PlayerEntity primaryPlayer = null;

            for(PlayerEntity player: players) {
                if (player.getUstaId() != null && !player.getUstaId().trim().equals("")) {
                    primaryPlayer = player;
                    break;
                }
            }

            if (primaryPlayer != null) {
                for(PlayerEntity player: players) {
                    if (player.getId() == primaryPlayer.getId()) {
                        DivisionEntity division = divisionRepository.findByPlayers_Id(player.getId()).get();
                        continue;
                    } else {
                        playerRepo.delete(player);
                        System.out.println("player " + player.getName() + " with id " +  player.getId() +"'is deleted");
                    }
                }
                continue;
            }

        }

    }

    @Test
    void mergePlayer() {
        String utrId = "4119258";
        boolean dryRun = false;

        List<PlayerEntity> players = playerRepo.findByUtrId(utrId);

        PlayerEntity primaryPlayer = findPrimaryPlayer(players);

        for (PlayerEntity player: players) {

            if (player.getId() == primaryPlayer.getId()) {
                continue;
            }

            merge(primaryPlayer, player, dryRun);
        }

    }

    private void merge(PlayerEntity primaryPlayer, PlayerEntity player, boolean dryRun) {

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
/*        Set<EventUTR> utrs = player.getUtrs();
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
            } else {
                logger.info("There are event UTR, switch owner from " + player.getId()
                        + " to " + primaryPlayer.getId());
            }
        } else {
            logger.info("No event UTR to merge");
        }*/

        //merge USTA Candidate Team captain
        logger.info("Merge USTA Candidate Team captain...");
        List<USTACandidateTeam> candidateTeams = candidateTeamRepository.findByCaptain_Id(player.getId());
        if (candidateTeams!=null) {
            //change captain as primary player
            for(USTACandidateTeam candidateTeam:candidateTeams) {
                candidateTeam.setCaptain(primaryPlayer);
                if (!dryRun) {
                    candidateTeamRepository.save(candidateTeam);
                } else {
                    logger.info("Change candidate team: " + candidateTeam.getName() + "'s captain from " + player.getId()
                            + " to " + primaryPlayer.getId());
                }
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
                } else {
                    logger.info("Change candidate team member: " + candidate.getName() + " from " + player.getId()
                            + " to " + primaryPlayer.getId());
                }
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
                } else {
                    logger.info("Change utr team candidate member : " + utrCandidate.getName() + " from " + player.getId()
                            + " to " + primaryPlayer.getId());
                }
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
                } else {
                    logger.info("Change utr team: " + utrTeam.getName() + "'s captain from " + player.getId()
                            + " to " + primaryPlayer.getId());
                }
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
                } else {
                    logger.info("Change UTR team member: " + member.getName() + " from " + player.getId()
                            + " to " + primaryPlayer.getId());
                }
            }
        } else {
            logger.info("No UTR Team member to merge");
        }

        if (!dryRun) {
            playerRepo.delete(player);
            logger.info("player " + player.getName() + " with id " +  player.getId() +"'is deleted");
        } else {
            logger.info("player " + player.getName() + " with id " +  player.getId() +"'to be deleted");
        }

        System.out.println(player.getName() +"'s merge is completed");
    }

    private PlayerEntity findPrimaryPlayer(List<PlayerEntity> players) {

        PlayerEntity primaryPlayer = null;

        for (PlayerEntity player: players) {
            if (player.getUstaId()!=null) {

                if (primaryPlayer!=null) {
                    logger.info("Can not merge: already has record with usta info:" + primaryPlayer.getName());
                    return null;
                }

                primaryPlayer = player;
            }
        }

        return primaryPlayer;
    }
}