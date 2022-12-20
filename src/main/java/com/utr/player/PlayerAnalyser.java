package com.utr.player;

import com.utr.model.MatchResult;
import com.utr.model.Player;
import com.utr.model.PlayerResult;
import com.utr.parser.UTRParser;

import java.util.*;

public class PlayerAnalyser {

    private final UTRParser parser;
    private Map<String, PlayerResult> players;

    public PlayerAnalyser() {
        parser = new UTRParser();
        players = new HashMap<>();
    }

    private static class SingletonHolder {
        private static final PlayerAnalyser INSTANCE = new PlayerAnalyser();
    }

    public static PlayerAnalyser getInstance() {
        return PlayerAnalyser.SingletonHolder.INSTANCE;
    }

    public SingleAnalysisResult compareSingle(String playerId1, String playerId2) {

        PlayerResult player1 = getPlayer(playerId1);
        PlayerResult player2 = getPlayer(playerId2);

        if (player1 == null || player2 == null) {
            return null;
        }

        SingleAnalysisResult ar = new SingleAnalysisResult(player1.getPlayer(), player2.getPlayer());

        List<MatchResult> singleMatches = player1.getMatches(MatchResult.SINGLE);
        Map<String, List<MatchResult>> player1Opponents = new HashMap<>();
        for (MatchResult match:singleMatches) {
            String opponentId = match.getOpponents().get(0).getId();
            if (opponentId.equals(playerId2)) {
                ar.getPastMatches().add(match);
            }
            if (player1Opponents.containsKey(opponentId)) {
                player1Opponents.get(opponentId).add(match);
            } else{
                List<MatchResult> matches = new ArrayList<>();
                matches.add(match);
                player1Opponents.put(opponentId, matches);
            }

        }

        List<MatchResult> player2SingleMatches = player2.getMatches(MatchResult.SINGLE);
        for (MatchResult match:player2SingleMatches) {
            String opponentId = match.getOpponents().get(0).getId();
            if (ar.getMatchesWithSamePlayer().containsKey(opponentId)) {
                List<MatchResult> matches = ar.getMatchesWithSamePlayer().get(opponentId);
                matches.add(match);
            } else if (player1Opponents.containsKey(opponentId)) {
                List<MatchResult> matches = player1Opponents.get(opponentId);
                matches.add(match);
                ar.getMatchesWithSamePlayer().put(opponentId, matches);
            }
        }

        return ar;
    }

    private PlayerResult getPlayer(String playerId) {
        PlayerResult player;
        if (players.containsKey(playerId)) {
            player = players.get(playerId);
        } else {
            player = parser.parsePlayerResult(playerId);
            players.put(playerId, player);
        }

        return player;
    }
}
