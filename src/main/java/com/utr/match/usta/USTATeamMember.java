package com.utr.match.usta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.utr.match.entity.PlayerEntity;

public class USTATeamMember {

    PlayerEntity player;

    int winNo;
    int lostNo;

    public USTATeamMember(PlayerEntity player) {
        this.player = player;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public double getDUTR() {
        return player.getDUTR();
    }

    public double getSUTR() {
        return player.getSUTR();
    }

    public String getName() {
        return player.getName();
    }

    public long getId() {
        return player.getId();
    }

    public String getGender() {
        return player.getGender();
    }

    public String getUSTARating() {
        return player.getUstaRating();
    }

    public Double getDynamicRating() {
        return player.getDynamicRating();
    }

    public String getDUTRStatus() {
        return player.getDUTRStatus();
    }

    public String getSUTRStatus() {
        return player.getSUTRStatus();
    }

    public boolean isRefreshedUTR() {
        return player.isRefreshedUTR();
    }

    public float getSuccessRate() {
        return player.getSuccessRate();
    }

    public float getWholeSuccessRate() {
        return player.getWholeSuccessRate();
    }

    public String getSummary() {
        return player.getSummary();
    }

    public int getWinNo() {
        return winNo;
    }

    public void setWinNo(int winNo) {
        this.winNo = winNo;
    }

    public int getLostNo() {
        return lostNo;
    }

    public void setLostNo(int lostNo) {
        this.lostNo = lostNo;
    }
}
