package com.utr.match.usta;

import com.utr.match.entity.*;
import com.utr.model.Player;
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
    private USTATeamMatchRepository teamMatchRepository;

    @Autowired
    private USTATeamMatchLineRepository matchLineRepository;

    @Autowired
    private USTATeamScoreCardRepository scoreCardRepository;

    public USTATeamImportor() {
    }

    public void refreshTeamMatchesScores(USTATeam team, USTADivision division) {
        USTASiteParser util = new USTASiteParser();
        try {
            JSONArray matches = util.parseTeamMatches(team.getTeamEntity());

            for (int i = 0; i < matches.length(); i++) {
                JSONObject scoreCardJSON = (JSONObject) matches.get(i);
                handleScoreCard(division, team.getTeamEntity().getUstaFlight(), scoreCardJSON);
            }

            cleanUpMatches(matches, division, team.getTeamEntity());

            updatePlayerWinInfo(team);

        } catch (IOException | ParseException e) {
            logger.debug("Failed to update team score" + team.getName());
            //throw new RuntimeException(e);
        }

    }

    public void updatePlayerWinInfo(USTATeam team) {
        List<USTATeamMatch> existedMatches = teamMatchRepository.findByTeamOrderByMatchDateAsc(team.getTeamEntity());

        Map<String, Integer> playersWinInfo = new HashMap<>();
        Map<String, Integer> playersLostInfo = new HashMap<>();

        for (USTATeamMatch match : existedMatches) {
            if (match.getScoreCard() == null) {
                continue;
            }
            for (USTATeamLineScore lineScore: match.getScoreCard().getLineScores()) {
                boolean win = lineScore.isWinnerTeam(team.getName());
                USTATeamPair pair = lineScore.getPair(team.getName());
                if (win) {
                    updatePlayerWinInfo(playersWinInfo, pair.getPlayer1());
                    updatePlayerWinInfo(playersWinInfo, pair.getPlayer2());
                } else {
                    updatePlayerWinInfo(playersLostInfo, pair.getPlayer1());
                    updatePlayerWinInfo(playersLostInfo, pair.getPlayer2());
                }
            }
        }

        USTASiteParser util = new USTASiteParser();
        try {
            USTATeamEntity teamEntity =  util.parseUSTATeam(team.getLink());
            for (USTATeamMember member: team.getPlayers()) {
                member.setWinNo(playersWinInfo.getOrDefault(member.getName(), 0));
                member.setLostNo(playersLostInfo.getOrDefault(member.getName(), 0));
                USTATeamMember parsedMember = teamEntity.getPlayer(member.getName());
                if (parsedMember !=null) {
                    member.setQualifiedPo(parsedMember.isQualifiedPo());
                }
                ustaTeamMemberRepository.save(member);
            }
        } catch (IOException e) {
            //throw new RuntimeException(e);
            logger.debug("failed to parse team:" + team.getName());
        }


    }

    private void updatePlayerWinInfo(Map<String, Integer> playersWinInfo, PlayerEntity player) {
        if (player != null) {
            int score = playersWinInfo.getOrDefault(player.getName(), 0);
            score++;
            playersWinInfo.put(player.getName(), score);
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

        for (USTATeamMatch match : existedMatches) {
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
        for (USTATeamMatch match : existedMatches) {
            if (match.getMatchDate().equals(matchDate) && match.getOpponentTeamId().longValue() == opponentTeamId.longValue()) {
                target = match;
                break;
            }
        }

        if (target != null) {
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
                    System.out.println("home line is updated " + existedHomeLine);
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
                    System.out.println("guest line is updated " + existedGuestLine);
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
        String teamNameLabel = isHome ? "homeTeamName" : "guestTeamName";
        String teamLinkLabel = isHome ? "homeTeamLink" : "guestTeamLink";

        String teamName = obj.get(teamNameLabel).toString();
        String teamLink = obj.get(teamLinkLabel).toString();

        USTATeamEntity homeTeam = ustaTeamRepository.findByNameAndDivision_Id(teamName, division.getId());

        if (homeTeam == null) {
            homeTeam = importUSTATeam(teamLink);
        }
        return homeTeam;
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

            for (USTATeamMember player : team.getPlayers()) {

                PlayerEntity existedPlayer = playerRepository.findByUstaNorcalId(player.getUstaNorcalId());

                if (existedPlayer == null) {
                    List<PlayerEntity> players = playerRepository.findByName(player.getName());

                    if (players != null && players.size() >0) {
                        existedPlayer = players.get(0);
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
                } else {
                    setAgeRange(player.getPlayer(), division);
                    existedPlayer = playerRepository.save(player.getPlayer());
                    logger.debug("new player " + player.getName() + " is created");
                }

                if (existTeam.getPlayer(existedPlayer.getName()) == null) {
                    USTATeamMember member = new USTATeamMember(existedPlayer);
                    member.setRating(player.getRating());
                    member = ustaTeamMemberRepository.save(member);
                    existTeam.getPlayers().add(member);
                    logger.debug(" add player " + player.getName() + " into team");
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

            updatePlayerUTRID(member);

        }
    }

    public USTATeamMember updatePlayerUTRID(USTATeamMember member) {

        if (member.getUtrId() == null || member.getUtrId().trim().equals("")) {
            List<Player> utrplayers = parser.searchPlayers(member.getName(), 5);
            String candidateUTRId = findUTRID(utrplayers, member);
            if (candidateUTRId != null) {
                member.getPlayer().setUtrId(candidateUTRId);
                PlayerEntity result = playerRepository.save(member.getPlayer());
                member.setPlayer(result);
                logger.debug("Player:" + member.getName() + " utr: " + member.getUtrId() + " Saved ");
            } else {
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

                PlayerEntity result = playerRepository.save(existPlayer.getPlayer());

                existPlayer.setPlayer(result);

                logger.debug("player " + existPlayer.getId() + " " + existPlayer.getName() + " dr " + player.getDynamicRating() + " updated");
            }

        } catch (IOException e) {
            logger.error(e.toString());
            //throw new RuntimeException(e);
        }

    }

    public void updateTeamUTRInfo(USTATeam existTeam) {

        for (USTATeamMember player : existTeam.getPlayers()) {

            updatePlayerUTRInfo(player, false);

        }


    }

    public USTATeamMember updatePlayerUTRInfo(USTATeamMember member, boolean forceUpdate) {
        if (member.isRefreshedUTR() && !forceUpdate) {
            logger.debug(member.getName() + " has latest UTR, skip");
            return member;
        }

        String utrId = member.getUtrId();

        if (utrId == null || utrId.equals("")) {
            logger.debug(member.getName() + " has no UTR ID, no need to refresh");

            if (member.getDUTR() > 0.0 || member.getSUTR() > 0.0) {
                if (member.getDUTR() > 0.0) {
                    member.getPlayer().setDUTR(0.0);
                    member.getPlayer().setDUTRStatus("");
                }

                if (member.getSUTR() > 0.0) {
                    member.getPlayer().setSUTR(0.0);
                    member.getPlayer().setSUTRStatus("");
                }

                member.getPlayer().setWholeSuccessRate(0.0f);
                member.getPlayer().setSuccessRate(0.0f);
                PlayerEntity player = playerRepository.save(member.getPlayer());
                member.setPlayer(player);
            }

            return member;
        }

        logger.debug(member.getName() + " start to query utr and win ratio");

        float successRate = parser.getWinPercent(utrId, true);
        float wholeSuccessRate = parser.getWinPercent(utrId, false);
        member.getPlayer().setSuccessRate(successRate);
        member.getPlayer().setWholeSuccessRate(wholeSuccessRate);

        Player utrplayer = parser.getPlayer(utrId);

        if (utrplayer == null) {
            logger.debug("failed to get player: " + member.getName() + "'s utr, skipped");
            return member;
        }

        member.getPlayer().setSUTR(utrplayer.getsUTR());
        member.getPlayer().setDUTR(utrplayer.getdUTR());
        member.getPlayer().setSUTRStatus(utrplayer.getsUTRStatus());
        member.getPlayer().setDUTRStatus(utrplayer.getdUTRStatus());
        member.getPlayer().setUtrFetchedTime(new Timestamp(System.currentTimeMillis()));

        PlayerEntity player = playerRepository.save(member.getPlayer());

        member.setPlayer(player);

        logger.debug(member.getName() + " utr is updated");

        return member;
    }

    private String findUTRID(List<Player> players, USTATeamMember player) {
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
                    if (player.getUstaId() == null) {
                        Map<String, String> playerInfo = util.parseUSTANumber(player.getNoncalLink());
                        player.getPlayer().setUstaId(playerInfo.get("USTAID"));
                        player.getPlayer().setUstaRating(playerInfo.get("Rating"));
                        playerRepository.save(player.getPlayer());
                        logger.debug("Player:" + player.getName() + " usta ID: " + player.getUstaId() + " Saved ");
                    } else {
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


}
