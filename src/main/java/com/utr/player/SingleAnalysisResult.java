package com.utr.player;

import com.utr.model.MatchResult;
import com.utr.model.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleAnalysisResult {

    Player player1;
    Player player2;
    List<MatchResult> pastMatches;
    Map<String, List<MatchResult>> matchesWithSamePlayer;

    public SingleAnalysisResult(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.pastMatches = new ArrayList<>();
        this.matchesWithSamePlayer = new HashMap<>();
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    @Override
    public String toString() {
        return "SingleAnalysisResult{" +
                "player1=" + player1 +
                ", player2=" + player2 +
                ", pastMatches=" + pastMatches +
                ", matchesWithSamePlayer=" + matchesWithSamePlayer +
                '}';
    }

    public List<MatchResult> getPastMatches() {
        return pastMatches;
    }

    public Map<String, List<MatchResult>> getMatchesWithSamePlayer() {
        return matchesWithSamePlayer;
    }

}
