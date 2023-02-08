package com.utr.match.usta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.utr.match.entity.PlayerEntity;

public class USTATeamMember {

    @JsonIgnore
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

    @JsonProperty("ustaRating")
    public String getUSTARating() {
        return player.getUstaRating();
    }

    @JsonProperty("ustaNorcalId")
    public String getUstaNorcalId() {
        return player.getUstaNorcalId();
    }

    public double getDynamicRating() {
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

    public float getLevel() {
        String rating = this.player.getUstaRating();
        if (rating != null && rating.length() >=3) {
            return Float.parseFloat(rating.substring(0,3));
        }
        return 0.0f;
    }

    public String getUtrId() {
        return player.getUtrId();
    }

    public String getUstaId() {
        return player.getUstaId();
    }

    public String getNoncalLink() {
        return player.getNoncalLink();
    }

    public String getTennisRecordLink() {
        return player.getTennisRecordLink();
    }

    public boolean isLefty() {
        return player.isLefty();
    }

    public String getTennisLinkURL() {
        return player.getTennisLinkURL();
    }

    public String getAgeRange() {
        return player.getAgeRange();
    }


}
