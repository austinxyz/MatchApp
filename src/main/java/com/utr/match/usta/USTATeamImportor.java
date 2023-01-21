package com.utr.match.usta;

import com.utr.match.TeamLoader;
import com.utr.match.entity.*;
import com.utr.model.Division;
import com.utr.model.Event;
import com.utr.model.Player;
import com.utr.model.PlayerEvent;
import com.utr.parser.UTRParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

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

    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
    @Autowired
    private USTAFlightRepository ustaFlightRepository;

    @Autowired
    private USTATeamMatchRepository teamMatchRepository;

    @Autowired
    private USTATeamMatchLineRepository matchLineRepository;

    @Autowired
    private USTATeamScoreCardRepository scoreCardRepository;

    public USTATeamImportor() {
    }

    public void refreshTeamMatcheScores(USTATeam team) {
        USTASiteParser util = new USTASiteParser();
        try {
            JSONArray matches = util.parseTeamMatches(team);
            USTAFlight flight = team.getUstaFlight();

            for (int i=0; i<matches.length(); i++) {
                JSONObject scoreCardJSON = (JSONObject)matches.get(i);
                handleScoreCard(flight, scoreCardJSON);
            }

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public USTATeamScoreCard importScoreCard(String scoreCardURL, Long flightId) {

        USTASiteParser util = new USTASiteParser();
        try {
            USTAFlight flight = ustaFlightRepository.findById(flightId).get();

            JSONObject obj = util.parseScoreCard(scoreCardURL);

            handleScoreCard(flight, obj);

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }


        return null;
    }

    private void handleScoreCard(USTAFlight flight, JSONObject obj) throws ParseException {
        USTATeamMatch homeMatch = createHomeTeamMatch(obj);

        USTATeamMatch guestMatch = createGuestTeamMatch(obj);

        homeMatch.setOpponentTeam(guestMatch.getTeam());
        homeMatch.setOpponentPoint(guestMatch.getPoint());

        guestMatch.setOpponentTeam(homeMatch.getTeam());
        guestMatch.setOpponentPoint(homeMatch.getPoint());

        USTATeamScoreCard scoreCard = new USTATeamScoreCard(flight);

        createMatchLines(obj, scoreCard, homeMatch, guestMatch);

        homeMatch = createOrFetchMatch(homeMatch);

        guestMatch = createOrFetchMatch(guestMatch);

        if (homeMatch.getScoreCard() == null) {

            for (USTATeamLineScore lineScore : scoreCard.getLineScores()) {
                USTATeamMatchLine homeLine = lineScore.getHomeLine();
                homeLine = matchLineRepository.findByMatch_IdAndName(homeMatch.getId(), homeLine.getName());

                if (homeLine != null) {
                    lineScore.setHomeLine(homeLine);
                }

                USTATeamMatchLine guestLine = lineScore.getGuestLine();
                guestLine = matchLineRepository.findByMatch_IdAndName(guestMatch.getId(), guestLine.getName());

                if (guestLine != null) {
                    lineScore.setGuestLine(guestLine);
                }
            }

            if (homeMatch.getLines() != null && !homeMatch.getLines().isEmpty()) {

                scoreCard = scoreCardRepository.save(scoreCard);

                homeMatch.setScoreCard(scoreCard);

                teamMatchRepository.save(homeMatch);

                guestMatch.setScoreCard(scoreCard);

                teamMatchRepository.save(guestMatch);
            }
        }

    }

    private USTATeamMatch createOrFetchMatch(USTATeamMatch match) {
        USTATeamMatch existMatch = teamMatchRepository.findByMatchDateAndTeam_IdAndOpponentTeam_Id(match.getMatchDate(),
                match.getTeam().getId(), match.getOpponentTeam().getId());
        if (existMatch != null) {
            match = updateMatchInfo(existMatch, match);
        } else {
            match = teamMatchRepository.save(match);
        }
        return match;
    }

    private USTATeamMatch updateMatchInfo(USTATeamMatch existMatch, USTATeamMatch match) {

        if (existMatch.getLines() == null || existMatch.getLines().size() == 0) { //exist match has no result
            existMatch.setPoint(match.getPoint());
            existMatch.setOpponentPoint(match.getOpponentPoint());
            existMatch.getLines().addAll(match.getLines());

            for (USTATeamMatchLine line : match.getLines()) {
                line.setMatch(existMatch);
            }

            return teamMatchRepository.save(existMatch);
        } else {
            for (USTATeamMatchLine line: existMatch.getLines()) {
                USTATeamMatchLine newLine = match.getLine(line.getName());
                line.setPlayer1(newLine.getPlayer1());
                line.setPlayer2(newLine.getPlayer2());
            }
            return teamMatchRepository.save(existMatch);
        }
    }

    private USTATeamMatch createGuestTeamMatch(JSONObject obj) throws ParseException {
        String guestTeamName = obj.get("guestTeamName").toString();

        USTATeam guestTeam = ustaTeamRepository.findByName(guestTeamName);

        USTATeamMatch match = new USTATeamMatch(guestTeam);

        java.util.Date date = formatter.parse(obj.get("matchDate").toString());

        Date matchDate = new Date(date.getTime());

        match.setMatchDate(matchDate);

        if (obj.has("guestPoint")) {
            match.setPoint(Integer.parseInt(obj.get("guestPoint").toString()));
        }

        match.setHome(false);

        return match;
    }

    private void createMatchLines(JSONObject obj, USTATeamScoreCard scoreCard, USTATeamMatch homeMatch, USTATeamMatch guestMatch) {

        if (obj.has("singles")) {

            JSONArray singles = (JSONArray)obj.get("singles");

            parseScore(scoreCard, homeMatch, guestMatch, singles, true);

        }
        if (obj.has("doubles")) {
            JSONArray doubles = (JSONArray) obj.get("doubles");

            parseScore(scoreCard, homeMatch, guestMatch, doubles, false);
        }

    }

    private void parseScore(USTATeamScoreCard scoreCard, USTATeamMatch homeMatch, USTATeamMatch guestMatch, JSONArray linescores, boolean isSingle) {

        for (int i = 0; i< linescores.length(); i++) {
            JSONObject scoreJson = (JSONObject) linescores.get(i);

            USTATeamLineScore score = new USTATeamLineScore();

            score.setScore(scoreJson.get("score").toString());
            score.setHomeTeamWin(scoreJson.get("winTeam").toString().equals("Home"));

            USTATeamMatchLine homeLine = createMatchLine(scoreJson, isSingle, true);

            homeMatch.getLines().add(homeLine);

            homeLine.setMatch(homeMatch);

            score.setHomeLine(homeLine);

            USTATeamMatchLine guestLine = createMatchLine(scoreJson, isSingle, false);

            guestMatch.getLines().add(guestLine);

            guestLine.setMatch(guestMatch);

            score.setGuestLine(guestLine);

            scoreCard.getLineScores().add(score);

            score.setScoreCard(scoreCard);
        }
    }

    private USTATeamMatchLine createMatchLine(JSONObject scoreJson, boolean isSingle, boolean home) {
        String playersName = home? "homePlayers": "guestPlayers";

        JSONArray players = (JSONArray) scoreJson.get(playersName);

        PlayerEntity player1 = null;

        PlayerEntity player2 = null;

        if (players != null && players.length() > 0) {

            JSONObject playerJson = (JSONObject)players.get(0);

            String norcalId = (String)playerJson.get("norcalId");

            player1 = playerRepository.findByUstaNorcalId(norcalId);

            if (!isSingle) {
                playerJson = (JSONObject)players.get(1);

                norcalId = (String)playerJson.get("norcalId");

                player2 = playerRepository.findByUstaNorcalId(norcalId);
            }

        }

        String name = (String) scoreJson.get("lineName");

        USTATeamMatchLine matchLine = new USTATeamMatchLine(player1, player2, isSingle? "S":"D", name);

        return matchLine;
    }

    private USTATeamMatch createHomeTeamMatch(JSONObject obj) throws ParseException {
        String homeTeamName = obj.get("homeTeamName").toString();

        USTATeam homeTeam = ustaTeamRepository.findByName(homeTeamName);

        USTATeamMatch match = new USTATeamMatch(homeTeam);

        java.util.Date date = formatter.parse(obj.get("matchDate").toString());

        Date matchDate = new Date(date.getTime());

        match.setMatchDate(matchDate);

        if (obj.has("homePoint")) {
            match.setPoint(Integer.parseInt(obj.get("homePoint").toString()));
        }

        match.setHome(true);

        return match;
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
                PlayerEntity existedPlayer = playerRepository.findByUstaNorcalId(player.getUstaNorcalId());

                if (existedPlayer != null) {
                    existedPlayer.setNoncalLink(player.getNoncalLink());
                    existedPlayer.setArea(player.getArea());
                    existedPlayer.setUstaRating(player.getUstaRating());
                    playerRepository.save(existedPlayer);
                    logger.debug(player.getName() + " is existed, update USTA info");
                } else {
                    existedPlayer = playerRepository.save(player);
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

    public void updateTeamPlayersDR(USTATeam team) {

        USTASiteParser util = new USTASiteParser();

        try {
            List<PlayerEntity> players = util.getTeamDynamicRating(team.getTennisRecordLink());

            for (PlayerEntity player: players) {
                PlayerEntity existPlayer = team.getPlayer(player.getName());

                if (existPlayer == null) {
                    continue;
                }

                existPlayer.setDynamicRating(player.getDynamicRating());
                existPlayer.setDrFetchedTime(new Timestamp(System.currentTimeMillis()));

                playerRepository.save(existPlayer);

                logger.debug("player " + existPlayer.getId() + " " + existPlayer.getName() + " dr " + player.getDynamicRating().toString() + " updated");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void updateTeamUTRInfo(USTATeam existTeam) {

        for (PlayerEntity player : existTeam.getPlayers()) {

            updatePlayerUTRInfo(player, false);

        }


    }

    public void updatePlayerUTRInfo(PlayerEntity player, boolean forceUpdate) {
        if (player.isRefreshedUTR() && !forceUpdate) {
            logger.debug(player.getName() + " has latest UTR, skip");
            return;
        }

        String utrId = player.getUtrId();

        if (utrId == null || utrId.equals("")) {
            logger.debug(player.getName() + " has no UTR, no need to refresh");
            return;
        }

        logger.debug(player.getName() + " start to query utr and win ratio" );

        loader.searchPlayerResult(utrId, false);

        loader.searchPlayerResult(utrId, true);

        Player utrplayer = loader.getPlayer(utrId);

        if (utrplayer == null) {
            return;
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
