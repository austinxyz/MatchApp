package com.utr.match.usta.po;

import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.USTAMatchLine;
import com.utr.match.entity.USTATeamLineScore;

import java.sql.Date;


public class USTAMatchLinePO {

    USTAMatchLine lineScore;

    public USTAMatchLinePO(USTAMatchLine lineScore) {
        this.lineScore = lineScore;
    }

    public String getLineName() {
        return lineScore.getName();
    }
    public Date getMatchDate() {
        return lineScore.getMatch().getMatchDate();
    }

    public String getHomeTeamName() {
        return lineScore.getMatch().getHomeTeamName();
    }

    public String getGuestTeamName() {
        return lineScore.getMatch().getGuestTeamName();
    }

    public PlayerEntity getHomePlayer1() {
        return lineScore.getHomePlayer1();
    }

    public PlayerEntity getHomePlayer2() {
        return lineScore.getHomePlayer2();
    }

    public PlayerEntity getGuestPlayer1() {
        return lineScore.getGuestPlayer1();
    }

    public PlayerEntity getGuestPlayer2() {
        return lineScore.getGuestPlayer2();
    }

    public boolean isHomeTeamWin() {
        return lineScore.isHomeTeamWin();
    }

    public String getMatchType() {
        return lineScore.getType();
    }

    public String getScore() {
        return lineScore.getScore();
    }

    public String getComment() {
        return lineScore.getComment();
    }

    public String getVideoLink() {
        return lineScore.getVideoLink();
    }
}
