package com.utr.model;

public class Round {
    int winnerScore;
    int loserScore;
    int tierBreakScore;
    int winnerTierBreakScore;

    public Round(int winnerScore, int loserScore, int tierBreakScore, int winnerTierBreakScore) {
        this.winnerScore = winnerScore;
        this.loserScore = loserScore;
        this.tierBreakScore = tierBreakScore;
        this.winnerTierBreakScore = winnerTierBreakScore;
    }

    @Override
    public String toString() {

        if (tierBreakScore != -1) {
            if (winnerScore == 1) {
                return winnerScore + ":" + loserScore + "(" + tierBreakScore + ")";
            } else {
                return winnerScore + "(" + tierBreakScore + ")" + ":" + loserScore ;
            }
        }
        return winnerScore + ":" + loserScore;
    }
}
