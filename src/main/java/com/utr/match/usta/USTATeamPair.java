package com.utr.match.usta;

import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.USTATeamLineScore;

import java.util.ArrayList;
import java.util.List;

public class USTATeamPair {
    PlayerEntity player1;
    PlayerEntity player2;

    List<USTATeamLineScore> winScores;
    List<USTATeamLineScore> lostScores;

    int winMatchNo = 0;

    int lostMatchNo = 0;

    public USTATeamPair(PlayerEntity player1, PlayerEntity player2) {
        this.winScores = new ArrayList<>();
        this.lostScores =new ArrayList<>();

        if (player2 == null) {
            this.player1 = player1;
            return;
        }

        if (player1.getName().compareTo(player2.getName()) < 0) {
            this.player1 = player1;
            this.player2 = player2;
        } else {
            this.player1 = player2;
            this.player2 = player1;
        }

    }

    public PlayerEntity getPlayer1() {
        return player1;
    }

    public PlayerEntity getPlayer2() {
        return player2;
    }

    public void addScore(USTATeamLineScore score) {
        if (score.isWinner(player1, player2)) {
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

    public double getTotalUTR() {
        return player1.getDUTR() + player2.getDUTR();
    }

    public double getTotalDR() {
        return player1.getDynamicRating() + player2.getDynamicRating();
    }

    public float getSuccessRate() {
        if (winMatchNo + lostMatchNo == 0) {
            return 0;
        }
        return (float)winMatchNo / (float)(winMatchNo + lostMatchNo);
    }

    public String getPlayerNames() {
        return player1.getName() + ":" + player2.getName();
    }

    public List<USTATeamLineScore> getWinScores() {
        return winScores;
    }

    public List<USTATeamLineScore> getLostScores() {
        return lostScores;
    }

    public static int compareByWinNoAndUTR(USTATeamPair pair1, USTATeamPair pair2) {
        if (pair1.getWinMatchNo() == pair2.getWinMatchNo()) {
            return Double.compare(pair2.getTotalUTR(), pair1.getTotalUTR());
        } else {
            return Integer.compare(pair2.getWinMatchNo(), pair1.getWinMatchNo());
        }
    }
}
