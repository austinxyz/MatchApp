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
public class USTATeamImportor {

    private static final Logger logger = LoggerFactory.getLogger(USTATeamImportor.class);

    @Autowired
    EventRepository eventRepo;

    @Autowired
    USTASiteParser ustaParser;
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
    @Autowired
    private USTADivisionRepository divisionRepository;
    @Autowired
    private USTATeamRepository ustaTeamRepository;

    @Autowired
    private USTATeamMemberRepository ustaTeamMemberRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private UTRParser parser;
    //private TeamLoader loader;
    @Autowired
    private USTAFlightRepository ustaFlightRepository;

    @Autowired
    private USTAMatchRepository matchRepository;


    public USTATeamImportor() {
    }


    public USTATeamEntity importUSTATeam(String teamLink) {
        USTATeamEntity teamEntity = createTeamAndAddPlayers(teamLink);
        return updatePlayerUSTANumber(teamEntity);
    }

    public List<USTATeamEntity> importUSTAFlight(String flightURL) {

        USTASiteParser util = new USTASiteParser();
        List<USTATeamEntity> teams = new ArrayList<>();

        try {
            for (String teamURL : util.parseUSTAFlight(flightURL)) {
                USTATeamEntity team = importUSTATeam(teamURL);

                if (team !=null) {
                    teams.add(team);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return teams;
    }

    private USTATeamEntity createTeamAndAddPlayers(String teamURL) {

        USTASiteParser util = new USTASiteParser();
        try {
            USTATeamEntity team = util.parseUSTATeam(teamURL);
            USTADivision division = divisionRepository.findByName(team.getDivisionName());
            USTATeamEntity existTeam = ustaTeamRepository.findByNameAndDivision_Id(team.getName(), division.getId());

            if (existTeam != null) {
                logger.debug("team " + team.getName() + " is existed");
                if (existTeam.getUstaFlight().getLink() == null || existTeam.getUstaFlight().getLink().trim().equals("") ) {
                    logger.debug("flight link is null");
                    existTeam.getUstaFlight().setLink(team.getUstaFlight().getLink());
                    ustaFlightRepository.save(existTeam.getUstaFlight());
                    logger.debug("flight" + existTeam.getFlight() + " 's link is updated");
                }
            } else {
                USTATeamEntity newTeam = new USTATeamEntity();
                newTeam.setName(team.getName());
                newTeam.setAlias(team.getAlias());
                newTeam.setLink(teamURL);
                newTeam.setArea(team.getArea());
                newTeam.setFlight(team.getFlight());
                newTeam.setTennisRecordLink("https://www.tennisrecord.com/adult/teamprofile.aspx?teamname=" + team.getName() + "&year=" + division.getLeague().getYear());
                if (division != null) {
                    newTeam.setDivision(division);
                    int flightNo = Integer.parseInt(newTeam.getFlight());
                    USTAFlight flight = ustaFlightRepository.findByDivision_IdAndFlightNoAndArea(division.getId(), flightNo, newTeam.getArea());

                    if (flight == null) {
                        flight = new USTAFlight(flightNo, division);
                        flight.setArea(newTeam.getArea());
                        flight.setLink(team.getUstaFlight().getLink());
                        flight = ustaFlightRepository.save(flight);
                    }

                    newTeam.setUstaFlight(flight);
                }


                existTeam = ustaTeamRepository.save(newTeam);
                logger.debug("new team " + existTeam.getName() + " with id: " + existTeam.getId() + " is created");
            }

            boolean inBayArea = team.inBayArea();

            for (USTATeamMember player : team.getPlayers()) {

                PlayerEntity existedPlayer = playerRepository.findByUstaNorcalId(player.getUstaNorcalId());

                if (existedPlayer == null) {
                    List<PlayerEntity> players = playerRepository.findByName(player.getName());

                    if (players != null && players.size() >0) {
                        for (PlayerEntity candidate: players) {
                            if (candidate.getUstaRating()!=null && candidate.getUstaRating().equals(player.getUSTARating())) {
                                existedPlayer = candidate;
                            }
                        }
                    }
                }

                if (existedPlayer != null) {
                    if (existedPlayer.getUstaNorcalId() == null) {
                        existedPlayer.setNoncalLink(player.getNoncalLink());
                        existedPlayer.setUstaNorcalId(player.getUstaNorcalId());
                        existedPlayer.setArea(player.getArea());
                        setAgeRange(existedPlayer, division);
                        existedPlayer = playerRepository.save(existedPlayer);
                        logger.debug(player.getName() + " is existed, update USTA info");
                    }
                    if (inBayArea) {
                        if (!existedPlayer.isRegisteredBayArea()) {
                            existedPlayer.setRegisteredBayArea(true);
                            existedPlayer = playerRepository.save(existedPlayer);
                            logger.debug(player.getName() + " now registered in bay area team");
                        }
                    }
                } else {
                    setAgeRange(player.getPlayer(), division);
                    if (inBayArea) {
                        player.getPlayer().setRegisteredBayArea(true);
                    }
                    existedPlayer = playerRepository.save(player.getPlayer());
                    logger.debug("new player " + player.getName() + " is created");
                }

                if (existTeam.getPlayer(existedPlayer.getName()) == null) {
                    USTATeamMember member = new USTATeamMember();
                    member.setPlayer(existedPlayer);
                    member.setRating(player.getRating());
                    member.setTeam(existTeam);
                    //member = ustaTeamMemberRepository.save(member);
                    existTeam.addPlayer(member);
                    logger.debug(" add player " + player.getName() + " into team");
                } else {
                    USTATeamMember member = existTeam.getPlayer(existedPlayer.getName()) ;
                    if (member.getRating()== null || !member.getRating().equals(player.getRating())) {
                        member.setRating(player.getRating());
                        ustaTeamMemberRepository.save(member);
                        logger.debug(" update player " + player.getName() + "'s rating to " + member.getRating());
                    }
                }
            }

            List<USTATeamMember> toRemovePlayers = new ArrayList<>();

            for (USTATeamMember player : existTeam.getPlayers()) {
                if (team.getPlayer(player.getName()) == null) {
                    toRemovePlayers.add(player);
                }
            }

            for (USTATeamMember player : toRemovePlayers) {
                existTeam.getPlayers().remove(player);
            }

            if (existTeam.getCaptain() == null) {
                List<PlayerEntity> captains = playerRepository.findByNameLike(team.getCaptainName());
                if (captains.size() > 0) {
                    existTeam.setCaptain(captains.get(0));
                }
            }

            existTeam = ustaTeamRepository.save(existTeam);
            return existTeam;
        } catch (IOException e) {
            logger.error(e.toString());

            return null;
            //throw new RuntimeException(e);
        }

    }

    private void setAgeRange(PlayerEntity existedPlayer, USTADivision division) {
        if (division == null) {
            return;
        }
        String ageRange = division.getAgeRange();
        if (existedPlayer.getAgeRange() == null || existedPlayer.getAgeRange().trim().equals("")) {
            existedPlayer.setAgeRange(ageRange);
            return;
        }

        if (existedPlayer.getAgeRange().compareTo(ageRange) < 0) {
            existedPlayer.setAgeRange(ageRange);
        }

    }

    public void updateTeamPlayerUTRID(String teamName, String divisionName) {

        USTATeamEntity existTeam = ustaTeamRepository.findByNameAndDivision_Name(teamName, divisionName);

        if (existTeam == null) {
            logger.debug("team " + teamName + " is not existed");
            return;
        }


        updateTeamPlayersUTRID(new USTATeam(existTeam));


    }

    public void updateTeamPlayersUTRID(USTATeam existTeam) {
        for (USTATeamMember member : existTeam.getPlayers()) {

            updatePlayerUTRID(member.getPlayer());

        }
    }

    public PlayerEntity updatePlayerUTRID(PlayerEntity member) {

        if (member.getUtrId() == null || member.getUtrId().trim().equals("")) {
            List<Player> utrplayers = parser.searchPlayers(member.getName(), 10, true);
            String candidateUTRId = findUTRID(utrplayers, member);
            if (candidateUTRId != null && !candidateUTRId.equals("")) {
                member.setUtrId(candidateUTRId);
                member = playerRepository.save(member);
                logger.debug("Player:" + member.getName() + " utr: " + member.getUtrId() + " Saved ");
            } else {
                member.setMemo("No UTR ID");
                member = playerRepository.save(member);
                logger.debug("Player:" + member.getName() + " has no UTRId");
            }
        }

        return member;
    }

    public void updateTeamPlayersDR(USTATeam team) {

        USTASiteParser util = new USTASiteParser();

        try {
            List<PlayerEntity> players = util.getTeamDynamicRating(team.getTennisRecordLink());

            for (PlayerEntity player : players) {
                USTATeamMember existPlayer = team.getTeamMemberByName(player.getName());

                if (existPlayer == null) {
                    continue;
                }

                existPlayer.getPlayer().setDynamicRating(player.getDynamicRating());
                existPlayer.getPlayer().setDrFetchedTime(new Timestamp(System.currentTimeMillis()));
                existPlayer.getPlayer().setTennisRecordLink(player.getTennisRecordLink());

                if (existPlayer.getPlayer().getUstaRating() == null || existPlayer.getPlayer().getUstaRating().trim().equals("")) {
                    logger.debug("player " + player.getName() + " has rating " + player.getUstaRating());
                    existPlayer.getPlayer().setUstaRating(player.getUstaRating());
                }

                PlayerEntity result = playerRepository.save(existPlayer.getPlayer());

                existPlayer.setPlayer(result);

                logger.debug("player " + existPlayer.getId() + " " + existPlayer.getName() + " dr " + player.getDynamicRating() + " updated");
            }

        } catch (IOException e) {
            logger.error(e.toString());
            //throw new RuntimeException(e);
        }

    }

    public PlayerEntity updatePlayerDR(PlayerEntity player) {

        USTASiteParser util = new USTASiteParser();

        try {
            String dynamicRating = util.getDynamicRating(player.getTennisRecordLink());
            if (dynamicRating!=null && !dynamicRating.startsWith("-")) {
                player.setDynamicRating(Double.parseDouble(dynamicRating));
                player = playerRepository.save(player);
                logger.debug("player " + player.getId() + " " + player.getName() + " dr " + player.getDynamicRating() + " updated");
            } else {
                logger.debug("player " + player.getId() + " " + player.getName() + " no dr rating");
            }
        } catch (IOException e) {
            logger.error(e.toString());
            //throw new RuntimeException(e);
        }

        return player;
    }

    public void updateTeamUTRInfo(USTATeam existTeam) {

        for (USTATeamMember player : existTeam.getPlayers()) {

            updatePlayerUTRInfo(player.getPlayer(), false, true);

        }

    }

    public void updateCandidateListUTRInfo(List<UTRTeamCandidate> players, boolean forceUpdate, boolean includeWinPercent) {

        for (UTRTeamCandidate player : players) {

            updatePlayerUTRInfo(player.getPlayer(), forceUpdate, includeWinPercent);

        }

    }
    public void updateUSTACandidateListUTRInfo(List<USTACandidate> players, boolean forceUpdate, boolean includeWinPercent) {

        for (USTACandidate player : players) {

            updatePlayerUTRInfo(player.getPlayer(), forceUpdate, includeWinPercent);

        }

    }

    public void updatePlayerListUTRInfo(List<PlayerEntity> players, boolean forceUpdate, boolean includeWinPercent) {

        for (PlayerEntity player : players) {

            updatePlayerUTRInfo(player, forceUpdate, includeWinPercent);

        }

    }


    public void updateTeamUTRInfo(USTATeam existTeam, boolean forceUpdate, boolean includeWinPercent) {

        for (USTATeamMember player : existTeam.getPlayers()) {

            updatePlayerUTRInfo(player.getPlayer(), forceUpdate, includeWinPercent);

        }


    }

    public PlayerEntity updatePlayerUTRInfo(PlayerEntity member, boolean forceUpdate, boolean inlcudeWinPercent) {
        if (member.isRefreshedUTR() && !forceUpdate) {
            logger.debug(member.getName() + " has latest UTR, skip");
            return member;
        }

        String utrId = member.getUtrId();

        if (utrId == null || utrId.equals("")) {
            logger.debug(member.getName() + " has no UTR ID, no need to refresh");

            if (member.getDUTR() > 0.0 || member.getSUTR() > 0.0) {
                if (member.getDUTR() > 0.0) {
                    member.setDUTR(0.0);
                    member.setDUTRStatus("");
                }

                if (member.getSUTR() > 0.0) {
                    member.setSUTR(0.0);
                    member.setSUTRStatus("");
                }

                member.setWholeSuccessRate(0.0f);
                member.setSuccessRate(0.0f);
                member = playerRepository.save(member);
            }

            return member;
        }

        logger.debug(member.getName() + " start to query utr and win ratio");

        if (forceUpdate || (inlcudeWinPercent && member.isUTRRequriedRefresh())) {
            float successRate = parser.getWinPercent(utrId, true);
            float wholeSuccessRate = parser.getWinPercent(utrId, false);
            member.setSuccessRate(successRate);
            member.setWholeSuccessRate(wholeSuccessRate);
        }

        Player utrplayer = parser.getPlayer(utrId, true);

        if (utrplayer == null) {
            logger.debug("failed to get player: " + member.getName() + "'s utr, skipped");
            return member;
        }

        member.setSUTR(utrplayer.getsUTR());
        member.setDUTR(utrplayer.getdUTR());
        member.setSUTRStatus(utrplayer.getsUTRStatus());
        member.setDUTRStatus(utrplayer.getdUTRStatus());
        member.setUtrFetchedTime(new Timestamp(System.currentTimeMillis()));

        member = playerRepository.save(member);

        logger.debug(member.getName() + " utr is updated");

        return member;
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

            if (utrPlayer.getLocation() == null || !utrPlayer.getLocation().startsWith(player.getArea())) {
                continue;
            }

            candidateUTRIds.add(utrPlayer.getId());
        }

        return candidateUTRIds.size() > 0 ? candidateUTRIds.get(0) : "";
    }

    public USTATeamEntity updatePlayerUSTANumber(USTATeamEntity teamEntity) {

        if (teamEntity == null) {
            return null;
        }

        USTASiteParser util = new USTASiteParser();
        try {
            USTATeamEntity team = util.parseUSTATeam(teamEntity.getLink());

            USTATeamEntity existTeam = ustaTeamRepository.findByNameAndDivision_Name(team.getName(), team.getDivisionName());

            for (USTATeamMember player : existTeam.getPlayers()) {

                if (player.getNoncalLink() == null) {
                    USTATeamMember newPlayer = team.getPlayer(player.getName());

                    if (newPlayer != null) {
                        //player.setUstaRating(newPlayer.getUstaRating());
                        player.getPlayer().setArea(newPlayer.getArea());
                        player.getPlayer().setNoncalLink(newPlayer.getNoncalLink());
                        player.getPlayer().setUstaNorcalId(newPlayer.getUstaNorcalId());
                    }
                    playerRepository.save(player.getPlayer());
                    logger.debug("Player:" + player.getName() + " non CAL ID: " + player.getUstaNorcalId() + " Saved ");
                }

                if (player.getNoncalLink() != null) {
                    Map<String, String> playerInfo = util.parseUSTANumber(player.getNoncalLink());
                    if (player.getUstaId() == null) {
                        player.getPlayer().setUstaId(playerInfo.get("USTAID"));
                        player.getPlayer().setUstaRating(playerInfo.get("Rating"));
                        playerRepository.save(player.getPlayer());
                        logger.debug("Player:" + player.getName() + " usta ID: " + player.getUstaId() + " Saved ");
                    } else {
                        player.getPlayer().setUstaRating(playerInfo.get("Rating"));
                        playerRepository.save(player.getPlayer());
                        logger.debug("Player:" + player.getName() + " usta Rating: " + player.getUstaId() + " Saved ");
//                        logger.debug("Player:" + player.getName() + " nothing to update ");
                    }
                }
            }

            return existTeam;

        } catch (IOException e) {
            logger.error(e.toString());
            return null;
            //throw new RuntimeException(e);
        }

    }


    public boolean isTokenExpired(String playerId) {
        return parser.isTokenExpired(playerId);
    }
}
