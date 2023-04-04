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
public class USTAMatchImportor {

    private static final Logger logger = LoggerFactory.getLogger(USTAMatchImportor.class);

    @Autowired
    USTASiteParser ustaParser;
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);

    @Autowired
    private NewUSTATeamImportor teamImporter;

    @Autowired
    private USTATeamRepository ustaTeamRepository;
    @Autowired
    private USTATeamMemberRepository ustaTeamMemberRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private USTAMatchRepository matchRepository;

    public USTAMatchImportor() {
    }

    public void refreshMatchesScores(NewUSTATeam team, USTADivision division) {
        USTASiteParser util = new USTASiteParser();
        try {
            JSONArray matches = util.parseTeamMatches(team.getTeamEntity());

            for (int i = 0; i < matches.length(); i++) {
                JSONObject scoreCardJSON = (JSONObject) matches.get(i);
                handleScoreCard(division, scoreCardJSON);
            }

            cleanUpMatches(matches, division, team.getTeamEntity());

            updatePlayerWinInfo(team);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            logger.debug("Failed to update team score" + team.getName());
            //throw new RuntimeException(e);
        }

    }

    public void updatePlayerWinInfo(NewUSTATeam team) {
        List<USTAMatch> existedMatches = matchRepository.findByHomeTeam_IdOrGuestTeam_IdOrderByMatchDateAsc(
                team.getId(), team.getId()
        );

        Map<String, Integer> playersWinInfo = new HashMap<>();
        Map<String, Integer> playersLostInfo = new HashMap<>();

        for (USTAMatch match : existedMatches) {
            if (match.getLines() == null || match.getLines().isEmpty()) {
                continue;
            }
            for (USTAMatchLine lineScore: match.getLines()) {
                boolean win = lineScore.isWinnerTeam(team.getName());
                NewUSTATeamPair pair = lineScore.getPair(team.getName());
                if (pair == null) {
                    continue;
                }
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
        List<USTAMatch> existedMatches = matchRepository.findByHomeTeam_IdOrGuestTeam_IdOrderByMatchDateAsc(
                team.getId(), team.getId()
        );

        for (int i = 0; i < matches.length(); i++) {
            JSONObject scoreCardJSON = (JSONObject) matches.get(i);
            USTAMatch match = createMatch(scoreCardJSON, division);
            if (match == null) {
                continue;
            }
            if (match.getHomeTeamId() == team.getId() || match.getGuestTeamId() == team.getId()) {
                removeMatch(existedMatches, match.getMatchDate(), match.getHomeTeamId(), match.getGuestTeamId());
            }
        }

        for (USTAMatch match : existedMatches) {
            if (match.getLines().size() > 1) {
                continue;
            }
            matchRepository.delete(match);
        }
    }

    private void removeMatch(List<USTAMatch> existedMatches, Date matchDate, Long homeTeamId, Long guestTeamId) {
        USTAMatch target = null;
        for (USTAMatch match : existedMatches) {
            if (match.getMatchDate().equals(matchDate) &&
                    match.getHomeTeamId().longValue() == homeTeamId.longValue() &&
                    match.getGuestTeamId().longValue() == guestTeamId.longValue()) {
                target = match;
                break;
            }
        }

        if (target != null) {
            existedMatches.remove(target);
        }
    }

    public void importScoreCard(String scoreCardURL, USTADivision division) {

        USTASiteParser util = new USTASiteParser();
        try {

                JSONObject obj = util.parseScoreCard(scoreCardURL);

                handleScoreCard(division, obj);


        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

    }

    private void handleScoreCard(USTADivision division, JSONObject obj) throws ParseException {

        USTAMatch match = createMatch(obj, division);

        if (match == null) {
            return;
        }

        createMatchLines(obj, match);

        match = createOrFetchMatch(match);

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

    private USTAMatch createOrFetchMatch(USTAMatch match) {
        USTAMatch existMatch = matchRepository
                .findByMatchDateAndHomeTeam_IdAndGuestTeam_Id(match.getMatchDate(),
                match.getHomeTeamId(), match.getGuestTeamId());
        if (existMatch != null) {
            match = updateMatchInfo(existMatch, match);
        } else {
            match = matchRepository.save(match);
        }
        return match;
    }

    private USTAMatch updateMatchInfo(USTAMatch existMatch, USTAMatch match) {

        if (existMatch.getLines() == null || existMatch.getLines().size() == 0) { //exist match has no result
            existMatch.setHomePoint(match.getHomePoint());
            existMatch.setGuestPoint(match.getGuestPoint());
            existMatch.getLines().addAll(match.getLines());

            for (USTAMatchLine line : match.getLines()) {
                line.setMatch(existMatch);
            }

            return matchRepository.save(existMatch);
        } else {
            existMatch.setHomePoint(match.getHomePoint());
            existMatch.setGuestPoint(match.getGuestPoint());
            for (USTAMatchLine line : existMatch.getLines()) {
                USTAMatchLine newLine = match.getLine(line.getName());
                line.setHomePlayer1(newLine.getHomePlayer1());
                line.setHomePlayer2(newLine.getHomePlayer2());
                line.setGuestPlayer1(newLine.getGuestPlayer1());
                line.setGuestPlayer2(newLine.getGuestPlayer2());
                line.setScore(newLine.getScore());
            }
            return matchRepository.save(existMatch);
        }
    }

    private void createMatchLines(JSONObject obj, USTAMatch match) {

        if (obj.has("singles")) {

            JSONArray singles = (JSONArray) obj.get("singles");

            parseScore(match, singles, true);

        }
        if (obj.has("doubles")) {
            JSONArray doubles = (JSONArray) obj.get("doubles");

            parseScore(match, doubles, false);
        }

    }

    private void parseScore(USTAMatch match,
                            JSONArray linescores, boolean isSingle) {

        for (int i = 0; i < linescores.length(); i++) {
            JSONObject scoreJson = (JSONObject) linescores.get(i);

            String name = (String) scoreJson.get("lineName");
            String type = isSingle? "S":"D";

            USTAMatchLine score = new USTAMatchLine(type, name);

            score.setScore(scoreJson.get("score").toString());
            score.setHomeTeamWin(scoreJson.get("winTeam").toString().equals("Home"));

            updatePlayers(score, scoreJson, isSingle, true);
            updatePlayers(score, scoreJson, isSingle, false);

            match.getLines().add(score);
            score.setMatch(match);

        }
    }

    private void updatePlayers(USTAMatchLine line, JSONObject scoreJson, boolean isSingle, boolean home) {
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

        if (home) {
            line.setHomePlayer1(player1);
            line.setHomePlayer2(player2);
        } else {
            line.setGuestPlayer1(player1);
            line.setGuestPlayer2(player2);
        }

    }

    private USTAMatch createMatch(JSONObject obj, USTADivision division) throws ParseException {
        USTATeamEntity homeTeam = fetchOrCreateTeam(obj, true, division);
        USTATeamEntity guestTeam = fetchOrCreateTeam(obj, false, division);

        USTAMatch match = new USTAMatch(homeTeam, guestTeam);

        Object matchDateStr = obj.get("matchDate");

        if (matchDateStr == null || matchDateStr.toString().trim().equals("") || matchDateStr.toString().trim().equals("to be posted")) {
            return null;
        }

        java.util.Date date = formatter.parse(matchDateStr.toString());

        Date matchDate = new Date(date.getTime());

        match.setMatchDate(matchDate);

        if (obj.has("homePoint")) {
            match.setHomePoint(Integer.parseInt(obj.get("homePoint").toString()));
        }
        if (obj.has("guestPoint")) {
            match.setGuestPoint(Integer.parseInt(obj.get("guestPoint").toString()));
        }

        match.setHomeWin(match.getHomePoint()> match.getGuestPoint());

        return match;
    }

    private USTATeamEntity fetchOrCreateTeam(JSONObject obj, boolean isHome, USTADivision division) {
        String teamNameLabel = isHome ? "homeTeamName" : "guestTeamName";
        String teamLinkLabel = isHome ? "homeTeamLink" : "guestTeamLink";

        String teamName = obj.get(teamNameLabel).toString();
        String teamLink = obj.get(teamLinkLabel).toString();

        USTATeamEntity homeTeam = ustaTeamRepository.findByNameAndDivision_Id(teamName, division.getId());

        if (homeTeam == null) {
            homeTeam = teamImporter.importUSTATeam(teamLink);
        }
        return homeTeam;
    }

}
