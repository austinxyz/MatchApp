package com.utr.model;

import java.util.ArrayList;
import java.util.List;

public class MatchScore {

    List<Round> rounds;

    public MatchScore() {
        rounds = new ArrayList<>();
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void addRound(int winNumber, int loserNumber, int tierBreakNumber, int winnerTierBreakNumber) {
        this.rounds.add(new Round(winNumber, loserNumber, tierBreakNumber, winnerTierBreakNumber));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Round round: this.rounds) {
            builder.append(round.toString()).append(" ");
        }

        return builder.toString();
    }
}
