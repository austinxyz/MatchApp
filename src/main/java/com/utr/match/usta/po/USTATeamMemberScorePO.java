package com.utr.match.usta.po;

import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.USTATeamLineScore;

import java.sql.Date;


public class USTATeamMemberScorePO {

    USTATeamLineScore lineScore;

    public USTATeamMemberScorePO(USTATeamLineScore lineScore) {
        this.lineScore = lineScore;
    }

    public String getLineName() {
        return lineScore.getHomeLine().getName();
    }
    public Date getMatchDate() {
        return lineScore.getHomeLine().getMatch().getMatchDate();
    }

    public String getHomeTeamName() {
        return lineScore.getHomeLine().getMatch().getTeamName();
    }

    public String getGuestTeamName() {
        return lineScore.getGuestLine().getMatch().getTeamName();
    }

    public PlayerEntity getHomePlayer1() {
        return lineScore.getHomeLine().getPlayer1();
    }

    public PlayerEntity getHomePlayer2() {
        return lineScore.getHomeLine().getPlayer2();
    }

    public PlayerEntity getGuestPlayer1() {
        return lineScore.getGuestLine().getPlayer1();
    }

    public PlayerEntity getGuestPlayer2() {
        return lineScore.getGuestLine().getPlayer2();
    }

    public boolean isHomeTeamWin() {
        return lineScore.isHomeTeamWin();
    }

    public String getMatchType() {
        return lineScore.getHomeLine().getType();
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
