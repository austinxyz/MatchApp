package com.utr.match.usta;

import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.USTATeamLineScore;

import java.util.ArrayList;
import java.util.List;

public class USTATeamSingle {
    PlayerEntity player;

    List<USTATeamLineScore> winScores;
    List<USTATeamLineScore> lostScores;

    int winMatchNo = 0;

    int lostMatchNo = 0;

    public USTATeamSingle(PlayerEntity player1) {
        this.player = player1;
        this.winScores = new ArrayList<>();
        this.lostScores = new ArrayList<>();
    }

    public static int compareByWinNoAndUTR(USTATeamSingle s1, USTATeamSingle s2) {
        if (s1.getWinMatchNo() == s2.getWinMatchNo()) {
            return Double.compare(s2.getPlayer().getSUTR(), s1.getPlayer().getSUTR());
        } else {
            return Integer.compare(s2.getWinMatchNo(), s1.getWinMatchNo());
        }
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public void addScore(USTATeamLineScore score) {
        if (score.isWinner(player)) {
            this.winMatchNo++;
            this.winScores.add(score);
        } else {
            this.lostMatchNo++;
            this.lostScores.add(score);
        }
    }

    public int getWinMatchNo() {
        return winMatchNo;
    }

    public int getLostMatchNo() {
        return lostMatchNo;
    }

    public double getSingleUTR() {
        return player.getSUTR();
    }

    public float getSuccessRate() {
        return (float) winMatchNo / (winMatchNo + lostMatchNo);
    }

    public String getPlayerName() {
        return player.getName();
    }

    public List<USTATeamLineScore> getWinScores() {
        return winScores;
    }

    public List<USTATeamLineScore> getLostScores() {
        return lostScores;
    }
}