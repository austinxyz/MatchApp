package com.utr.match.usta;

import com.utr.match.TeamLoader;
import com.utr.match.entity.*;
import com.utr.model.Player;
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
    private PlayerRepository playerRepository;
    @Autowired
    private TeamLoader loader;
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

    public void refreshTeamMatchesScores(USTATeamEntity team, USTADivision division) {
        USTASiteParser util = new USTASiteParser();
        try {
            JSONArray matches = util.parseTeamMatches(team);

            for (int i = 0; i < matches.length(); i++) {
                JSONObject scoreCardJSON = (JSONObject) matches.get(i);
                handleScoreCard(division, team.getUstaFlight(), scoreCardJSON);
            }

            cleanUpMatches(matches, division, team);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

    }

    private void cleanUpMatches(JSONArray matches, USTADivision division, USTATeamEntity team) throws ParseException {
        List<USTATeamMatch> existedMatches = teamMatchRepository.findByTeamOrderByMatchDateAsc(team);

        for (int i = 0; i < matches.length(); i++) {
            JSONObject scoreCardJSON = (JSONObject) matches.get(i);
            USTATeamMatch homeMatch = createHomeTeamMatch(scoreCardJSON, division);
            USTATeamMatch guestMatch = createGuestTeamMatch(scoreCardJSON, division);
            homeMatch.setOpponentTeam(guestMatch.getTeam());
            guestMatch.setOpponentTeam(homeMatch.getTeam());
            if (homeMatch.getTeam().getId() == team.getId()) {
                removeMatch(existedMatches, homeMatch.getMatchDate(), homeMatch.getOpponentTeam().getId());
            }
            if (guestMatch.getTeam().getId() == team.getId()) {
                removeMatch(existedMatches, guestMatch.getMatchDate(), guestMatch.getOpponentTeam().getId());
            }
        }

        for (USTATeamMatch match: existedMatches) {
            if (match.getLines().size() > 1) {
                continue;
            }
            USTATeamMatch oppMatch = teamMatchRepository.findByMatchDateAndTeam_IdAndOpponentTeam_Id(match.getMatchDate(), match.getOpponentTeamId(), match.getTeamId());

            teamMatchRepository.delete(match);
            teamMatchRepository.delete(oppMatch);
        }
    }

    private void removeMatch(List<USTATeamMatch> existedMatches, Date matchDate, Long opponentTeamId) {
        USTATeamMatch target = null;
        for (USTATeamMatch match: existedMatches) {
            if (match.getMatchDate().equals(matchDate) && match.getOpponentTeamId().longValue() == opponentTeamId.longValue()) {
                target = match;
                break;
            }
        }

        if (target!=null) {
            existedMatches.remove(target);
        }
    }

    public void importScoreCard(String scoreCardURL, Long flightId, USTADivision division) {

        USTASiteParser util = new USTASiteParser();
        try {
            Optional<USTAFlight> flightOptional = ustaFlightRepository.findById(flightId);

            if (flightOptional.isPresent()) {
                USTAFlight flight = flightOptional.get();

                JSONObject obj = util.parseScoreCard(scoreCardURL);

                handleScoreCard(division, flight, obj);
            }

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

    }

    private void handleScoreCard(USTADivision division, USTAFlight flight, JSONObject obj) throws ParseException {
        USTATeamMatch homeMatch = createHomeTeamMatch(obj, division);

        USTATeamMatch guestMatch = createGuestTeamMatch(obj, division);

        if (homeMatch == null || guestMatch == null) {
            return;
        }

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
                scoreCard.setHomeTeamName(scoreCard.getHomeTeam().getName());
                scoreCard.setGuestTeamName(scoreCard.getGuestTeam().getName());

                scoreCard = scoreCardRepository.save(scoreCard);

                homeMatch.setScoreCard(scoreCard);

                teamMatchRepository.save(homeMatch);

                guestMatch.setScoreCard(scoreCard);

                teamMatchRepository.save(guestMatch);
            }
        } else {

            USTATeamScoreCard existedScoreCard = homeMatch.getScoreCard();

            for (USTATeamLineScore lineScore : scoreCard.getLineScores()) {
                USTATeamMatchLine homeLine = lineScore.getHomeLine();

                USTATeamLineScore existedLineScore = existedScoreCard.getLineScore(homeLine.getName());

                USTATeamMatchLine existedHomeLine = existedLineScore.getHomeLine();

                boolean changed = false;

                if (!samePlayer(homeLine.getPlayer1(), existedHomeLine.getPlayer1())) {
                    existedHomeLine.setPlayer1(homeLine.getPlayer1());
                    changed = true;
                }

                if (!samePlayer(homeLine.getPlayer2(), existedHomeLine.getPlayer2())) {
                    existedHomeLine.setPlayer2(homeLine.getPlayer2());
                    changed = true;
                }

                if (changed) {
                    matchLineRepository.save(existedHomeLine);
                    System.out.println("home line is updated" + existedHomeLine.toString());
                    changed = false;
                }

                USTATeamMatchLine guestLine = lineScore.getGuestLine();

                USTATeamMatchLine existedGuestLine = existedLineScore.getGuestLine();

                if (!samePlayer(guestLine.getPlayer1(), existedGuestLine.getPlayer1())) {
                    existedGuestLine.setPlayer1(guestLine.getPlayer1());
                    changed = true;
                }

                if (!samePlayer(guestLine.getPlayer2(), existedGuestLine.getPlayer2())) {
                    existedGuestLine.setPlayer2(guestLine.getPlayer2());
                    changed = true;
                }

                if (changed) {
                    matchLineRepository.save(existedGuestLine);
                    System.out.println("guest line is updated" + existedGuestLine.toString());
                    changed = false;
                }
            }

            if (existedScoreCard.getHomeTeamName() == null || existedScoreCard.getHomeTeamName().trim().equals("")) {
                existedScoreCard.setHomeTeamName(scoreCard.getHomeTeam().getName());
                existedScoreCard.setGuestTeamName(scoreCard.getGuestTeam().getName());
                scoreCardRepository.save(existedScoreCard);
            }

        }

    }

    private boolean samePlayer(PlayerEntity player1, PlayerEntity player2) {
        if (player1 == null && player2 == null) {
            return true;
        }

        if (player1 == null || player2 == null) {
            return false;
        }

        return player1.getId() == player2.getId();

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
            for (USTATeamMatchLine line : existMatch.getLines()) {
                USTATeamMatchLine newLine = match.getLine(line.getName());
                line.setPlayer1(newLine.getPlayer1());
                line.setPlayer2(newLine.getPlayer2());
            }
            return teamMatchRepository.save(existMatch);
        }
    }

    private USTATeamMatch createGuestTeamMatch(JSONObject obj, USTADivision division) throws ParseException {
        USTATeamEntity guestTeam = fetchOrCreateTeam(obj, false, division);

        USTATeamMatch match = new USTATeamMatch(guestTeam);

        Object matchDateStr = obj.get("matchDate");

        if (matchDateStr == null || matchDateStr.toString().trim().equals("")) {
            return null;
        }

        java.util.Date date = formatter.parse(matchDateStr.toString());

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

            JSONArray singles = (JSONArray) obj.get("singles");

            parseScore(scoreCard, homeMatch, guestMatch, singles, true);

        }
        if (obj.has("doubles")) {
            JSONArray doubles = (JSONArray) obj.get("doubles");

            parseScore(scoreCard, homeMatch, guestMatch, doubles, false);
        }

    }

    private void parseScore(USTATeamScoreCard scoreCard, USTATeamMatch homeMatch, USTATeamMatch guestMatch, JSONArray linescores, boolean isSingle) {

        for (int i = 0; i < linescores.length(); i++) {
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
        String playersName = home ? "homePlayers" : "guestPlayers";

        JSONArray players = (JSONArray) scoreJson.get(playersName);

        PlayerEntity player1 = null;

        PlayerEntity player2 = null;

        if (players != null && players.length() > 0) {

            JSONObject playerJson = (JSONObject) players.get(0);

            String norcalId = (String) playerJson.get("norcalId");

            player1 = playerRepository.findByUstaNorcalId(norcalId);

            if (!isSingle) {
                playerJson = (JSONObject) players.get(1);

                norcalId = (String) playerJson.get("norcalId");

                player2 = playerRepository.findByUstaNorcalId(norcalId);
            }

        }

        String name = (String) scoreJson.get("lineName");

        return new USTATeamMatchLine(player1, player2, isSingle ? "S" : "D", name);

    }

    private USTATeamMatch createHomeTeamMatch(JSONObject obj, USTADivision division) throws ParseException {
        USTATeamEntity homeTeam = fetchOrCreateTeam(obj, true, division);

        USTATeamMatch match = new USTATeamMatch(homeTeam);

        Object matchDateStr = obj.get("matchDate");

        if (matchDateStr == null || matchDateStr.toString().trim().equals("")) {
            return null;
        }

        java.util.Date date = formatter.parse(matchDateStr.toString());

        Date matchDate = new Date(date.getTime());

        match.setMatchDate(matchDate);

        if (obj.has("homePoint")) {
            match.setPoint(Integer.parseInt(obj.get("homePoint").toString()));
        }

        match.setHome(true);

        return match;
    }

    private USTATeamEntity fetchOrCreateTeam(JSONObject obj, boolean isHome, USTADivision division) {
        String teamNameLabel = isHome? "homeTeamName":"guestTeamName";
        String teamLinkLabel = isHome? "homeTeamLink":"guestTeamLink";

        String teamName = obj.get(teamNameLabel).toString();
        String teamLink = obj.get(teamLinkLabel).toString();

        USTATeamEntity homeTeam = ustaTeamRepository.findByNameAndDivision_Id(teamName, division.getId());

        if (homeTeam == null) {
            homeTeam = importUSTATeam(teamLink);
        }
        return homeTeam;
    }


    public USTATeamEntity importUSTATeam(String teamURL) {
        USTATeamEntity team = createTeamAndAddPlayers(teamURL);
        updatePlayerUSTANumber(teamURL);
        return team;
    }

    public List<USTATeamEntity> importUSTAFlight(String flightURL) {

        USTASiteParser util = new USTASiteParser();
        List<USTATeamEntity> teams = new ArrayList<>();

        try {
            for (String teamURL : util.parseUSTAFlight(flightURL)) {
                teams.add(importUSTATeam(teamURL));
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
                        flight = ustaFlightRepository.save(flight);
                    }

                    newTeam.setUstaFlight(flight);
                }


                existTeam = ustaTeamRepository.save(newTeam);
                logger.debug("new team " + existTeam.getName() + " with id: " + existTeam.getId() + " is created");
            }

            for (PlayerEntity player : team.getPlayers()) {

                PlayerEntity existedPlayer = existTeam.getPlayer(player.getName());

                if (existedPlayer != null) {
                    existedPlayer.setNoncalLink(player.getNoncalLink());
                    existedPlayer.setUstaNorcalId(player.getUstaNorcalId());
                    existedPlayer.setArea(player.getArea());
                    //existedPlayer.setUstaRating(player.getUstaRating());
                    setAgeRange(existedPlayer, division);
                    existedPlayer = playerRepository.save(existedPlayer);

                    logger.debug(player.getName() + " is existed, update USTA info");
                } else {
                    setAgeRange(player, division);
                    existedPlayer = playerRepository.save(player);
                    logger.debug("new player " + player.getName() + " is created");
                }

                if (existTeam.getPlayer(existedPlayer.getName()) == null) {
                    existTeam.getPlayers().add(existedPlayer);
                    logger.debug(" add player " + player.getName() + " into team");
                }
            }

            List<PlayerEntity> toRemovePlayers = new ArrayList<>();

            for (PlayerEntity player: existTeam.getPlayers()) {
                if (team.getPlayer(player.getName()) == null) {
                    toRemovePlayers.add(player);
                }
            }

            for (PlayerEntity player: toRemovePlayers) {
                existTeam.getPlayers().remove(player);
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

        updateTeamPlayersUTRID(existTeam);


    }

    public void updateTeamPlayersUTRID(USTATeamEntity existTeam) {
        for (PlayerEntity player : existTeam.getPlayers()) {

            updatePlayerUTRID(player);

        }
    }

    public PlayerEntity updatePlayerUTRID(PlayerEntity player) {

        PlayerEntity result = player;

        List<Player> utrplayers = loader.queryPlayer(player.getName(), 5);

        if (player.getUtrId() == null) {
            String candidateUTRId = findUTRID(utrplayers, player);
            if (candidateUTRId != null) {
                player.setUtrId(candidateUTRId);
                result = playerRepository.save(player);
                logger.debug("Player:" + player.getName() + " utr: " + player.getUtrId() + " Saved ");
            } else {
                logger.debug("Player:" + player.getName() + " has no UTRId");
            }
        }

        return result;
    }

    public void updateTeamPlayersDR(USTATeamEntity team) {

        USTASiteParser util = new USTASiteParser();

        try {
            List<PlayerEntity> players = util.getTeamDynamicRating(team.getTennisRecordLink());

            for (PlayerEntity player : players) {
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

    public void updateTeamUTRInfo(USTATeamEntity existTeam) {

        for (PlayerEntity player : existTeam.getPlayers()) {

            updatePlayerUTRInfo(player, false);

        }


    }

    public PlayerEntity updatePlayerUTRInfo(PlayerEntity player, boolean forceUpdate) {
        if (player.isRefreshedUTR() && !forceUpdate) {
            logger.debug(player.getName() + " has latest UTR, skip");
            return player;
        }

        String utrId = player.getUtrId();

        if (utrId == null || utrId.equals("")) {
            logger.debug(player.getName() + " has no UTR, no need to refresh");

            if (player.getDUTR() > 0.0 || player.getSUTR() > 0.0) {
                if (player.getDUTR() > 0.0) {
                    player.setDUTR(0.0);
                    player.setDUTRStatus("");
                }

                if (player.getSUTR() > 0.0) {
                    player.setSUTR(0.0);
                    player.setSUTRStatus("");
                }

                player.setWholeSuccessRate(0.0f);
                player.setSuccessRate(0.0f);
                player = playerRepository.save(player);
            }

            return player;
        }

        logger.debug(player.getName() + " start to query utr and win ratio");

        loader.searchPlayerResult(utrId, false);

        loader.searchPlayerResult(utrId, true);

        Player utrplayer = loader.getPlayer(utrId);

        if (utrplayer == null) {
            return player;
        }


        player.setSUTR(utrplayer.getsUTR());
        player.setDUTR(utrplayer.getdUTR());
        player.setSUTRStatus(utrplayer.getsUTRStatus());
        player.setDUTRStatus(utrplayer.getdUTRStatus());
        player.setSuccessRate(utrplayer.getSuccessRate());
        player.setWholeSuccessRate(utrplayer.getWholeSuccessRate());
        player.setUtrFetchedTime(new Timestamp(System.currentTimeMillis()));

        player = playerRepository.save(player);

        logger.debug(player.getName() + " utr is updated");

        return player;
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

            if (utrPlayer.getLocation() == null || !utrPlayer.getLocation().contains("CA")) {
                continue;
            }

            candidateUTRIds.add(utrPlayer.getId());
        }

        return candidateUTRIds.size() > 0 ? candidateUTRIds.get(0) : "";
    }

    public void updatePlayerUSTANumber(String teamURL) {

        USTASiteParser util = new USTASiteParser();
        try {
            USTATeamEntity team = util.parseUSTATeam(teamURL);

            USTATeamEntity existTeam = ustaTeamRepository.findByNameAndDivision_Name(team.getName(), team.getDivisionName());

            for (PlayerEntity player : existTeam.getPlayers()) {

                if (player.getNoncalLink() == null) {
                    PlayerEntity newPlayer = team.getPlayer(player.getName());

                    if (newPlayer != null) {
                        //player.setUstaRating(newPlayer.getUstaRating());
                        player.setArea(newPlayer.getArea());
                        player.setNoncalLink(newPlayer.getNoncalLink());
                    }
                }

                if (player.getNoncalLink() != null) {
                    Map<String, String> playerInfo = util.parseUSTANumber(player.getNoncalLink());
                    player.setUstaId(playerInfo.get("USTAID"));
                    player.setUstaRating(playerInfo.get("Rating"));
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
