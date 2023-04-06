package com.utr.match.entity;

import com.utr.match.TeamLoader;
import com.utr.match.usta.USTAMatchImportor;
import com.utr.model.Player;
import com.utr.parser.UTRParser;
import com.utr.match.usta.USTASiteParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private USTAFlightRepository flightRepository;

    @Autowired
    private TeamLoader loader;

    @Autowired
    private USTAMatchRepository matchRepository;

    @Test
    void createTeam() {
        Optional<USTADivision> division = divisionRepository.findById(1L);

        if (division.isPresent()) {

            USTATeamEntity team = new USTATeamEntity("VALLEY CHURCH 40AM3.5A", division.get());

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

        USTATeamEntity team = ustaTeamRepository.findById(1L).get();

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
            USTATeamMember member = new USTATeamMember();
            member.setPlayer(player);
            team.getPlayers().add(member);
            ustaTeamRepository.save(team);
            System.out.println(player);
        }
    }

    @Test
    void getTeam() {
        USTATeamEntity team = ustaTeamRepository.findById(1L).get();

        System.out.println(team.getAreaCode());

        for (USTATeamMember player : team.getPlayers()) {

            System.out.println(player.getName() + ": update player set usta_noncal_link='https://www.ustanorcal.com/playermatches.asp?id=', usta_tennisrecord_link='https://www.tennisrecord.com/adult/profile.aspx?playername=' where id=" + player.getId());

        }

    }

    @Test
    @Transactional
    void getTeams() {
        PlayerEntity player = playerRepository.findByNameLike("%Lucy%").get(0);
        long start = System.currentTimeMillis();
        System.out.println(player.getName());
        for (USTATeamMember member :player.getTeamMembers()) {
            System.out.println(member.getTeam().getName());
        }
        System.out.println(System.currentTimeMillis() - start);

    }

    @Test
    void getTeamScore() {
        USTATeamEntity team = ustaTeamRepository.findById(1L).get();

        long start = System.currentTimeMillis();

        List<USTAMatch> matches = matchRepository.findByHomeTeam_IdOrGuestTeam_IdOrderByMatchDateAsc(team.getId(), team.getId());
        for (USTAMatch match :matches) {

            if (!match.getLines().isEmpty()) {
                System.out.println(match.getGuestPoint());
                System.out.println(match.getHomePoint());
            }
        }
        System.out.println(System.currentTimeMillis() - start);

    }

    @Test
    void createTeamAndAddPlayers() {

        USTASiteParser util = new USTASiteParser();
        try {
            String teamURL = "https://www.ustanorcal.com/teaminfo.asp?id=96701";
            USTATeamEntity team = util.parseUSTATeam(teamURL);

            USTADivision division = divisionRepository.findByName(team.getDivisionName());

            if (division == null) {
                System.out.println("Failed to import team, no division info");
                return;
            }

            USTATeamEntity existTeam = ustaTeamRepository.findByNameAndDivision_Id(team.getName(), division.getId());

            if (existTeam != null) {
                System.out.println("team " + team.getName() + " is existed");
            } else {
                USTATeamEntity newTeam = new USTATeamEntity();
                newTeam.setName(team.getName());
                newTeam.setAlias(team.getAlias());
                newTeam.setLink(teamURL);
                if (division!=null) {
                    newTeam.setDivision(division);
                }
                existTeam = ustaTeamRepository.save(newTeam);
                System.out.println("new team " + team.getName() + " is created");
            }

            for (USTATeamMember player : team.getPlayers()) {
                PlayerEntity existedPlayer = playerRepository.findByUstaNorcalId(player.getUstaNorcalId());

                if (existedPlayer != null) {
                    existedPlayer.setNoncalLink(player.getNoncalLink());
                    existedPlayer.setArea(player.getArea());
                    //existedPlayer.setUstaRating(player.getUstaRating());
                    playerRepository.save(existedPlayer);
                    System.out.println(player.getName() + " is existed, update USTA info");
                } else {
                    playerRepository.save(player.getPlayer());
                    existedPlayer = playerRepository.findByNameLike(player.getName()).get(0);
                    System.out.println("new player" + player.getName() + " is created");
                }

                if (existTeam.getPlayer(existedPlayer.getName()) == null) {
                    USTATeamMember member = new USTATeamMember();
                    member.setPlayer(existedPlayer);
                    existTeam.getPlayers().add(member);
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

        USTATeamEntity existTeam = ustaTeamRepository.findById(3L).get();

        if (existTeam == null) {
            System.out.println("team " + existTeam.getName() + " is not existed");
            return;
        }

        for (USTATeamMember player : existTeam.getPlayers()) {

            List<Player> utrplayers = parser.searchPlayers(player.getName(), 5);

            if (player.getUtrId() == null) {
                String candidateUTRId = findUTRID(utrplayers, player.getPlayer());
                if (candidateUTRId != null) {
                    player.getPlayer().setUtrId(candidateUTRId);
                    playerRepository.save(player.getPlayer());
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
            USTATeamEntity team = util.parseUSTATeam(teamURL);

            USTATeamEntity existTeam = ustaTeamRepository.findByNameAndDivision_Name(team.getName(), team.getDivisionName());

            for (USTATeamMember player : existTeam.getPlayers()) {

                if (player.getNoncalLink() == null) {
                    USTATeamMember newPlayer = team.getPlayer(player.getName());

                    if (newPlayer != null) {
                        //player.setUstaRating(newPlayer.getUstaRating());
                        player.getPlayer().setArea(newPlayer.getArea());
                        player.getPlayer().setNoncalLink(newPlayer.getNoncalLink());
                    }
                }

                if (player.getNoncalLink() != null) {

                    Map<String, String> playerInfo = util.parseUSTANumber(player.getNoncalLink());
                    player.getPlayer().setUstaId(playerInfo.get("USTAID"));
                    player.getPlayer().setUstaRating(playerInfo.get("Rating"));
                    //player.setTennisRecordLink(getTennisRecordLink(player));
                }

                playerRepository.save(player.getPlayer());
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


            List<USTATeamEntity> existTeams = ustaTeamRepository.findAll();

            for (USTATeamEntity existTeam : existTeams) {
               if (existTeam.getCaptain() == null ) {
                   USTATeamEntity team = util.parseUSTATeam(existTeam.getLink());
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

    @Test
    void createFlights() {

        for (USTATeamEntity team : ustaTeamRepository.findAll()) {
            String flightStr = team.getFlight();
            int flightNo = Integer.parseInt(flightStr);

            long divisionId = team.getDivision().getId();

            USTAFlight flight = flightRepository.findByDivision_IdAndFlightNoAndArea(divisionId, flightNo, team.getArea());

            if (flight == null) {
                flight = new USTAFlight(flightNo, team.getDivision());
                flight.setArea(team.getArea());
                flight = flightRepository.save(flight);
                System.out.println(" new flight created with division " + team.getDivisionName() + " no. " + flightNo );
            } else {
                flight.setArea(team.getArea());
                flight = flightRepository.save(flight);
            }

            team.setUstaFlight(flight);

            ustaTeamRepository.save(team);
        }


    }

    @Test
    void queryMatchScore() {

        USTATeamEntity team = ustaTeamRepository.findById(77L).get();
        for (USTAMatch match: matchRepository.findByHomeTeam_IdOrGuestTeam_IdOrderByMatchDateAsc(team.getId(), team.getId())) {
            if (!match.getLines().isEmpty()) {

                for (USTAMatchLine score: match.getLines()) {
                    if (score.getName().equals("D3")) {
                        System.out.println(score.getScore());
                    }
                }

            }
        }


    }


    @Test
    void updateAgeRange() {

        for (USTATeamEntity team : ustaTeamRepository.findByDivision_IdOrderByUstaFlightAsc(4L)){
            for (USTATeamMember player: team.getPlayers()) {

                if (player.getAgeRange() == null || !player.getAgeRange().equals("40+")) {
                    player.getPlayer().setAgeRange("40+");
                    playerRepository.save(player.getPlayer());
                    System.out.println(player.getName() + " is 40+");
                }
            }
        }
    }


}