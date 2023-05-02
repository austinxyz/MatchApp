package com.utr.match.usta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.USTAMatchLine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class USTASingleLineStat {


    String lineName;
    @JsonIgnore
    String teamName;

    @JsonIgnore
    Map<String, USTATeamSingle> newSinglers;

    int winMatchNo = 0;
    int lostMatchNo = 0;

    int surprisedWin = 0;

    int surprisedLost = 0;
    int normalNo = 0;

    public USTASingleLineStat(String lineName, String teamName) {
        newSinglers = new HashMap<>();
        this.lineName = lineName;
        this.teamName = teamName;
    }


    public void addMatchLine(USTAMatchLine score) {

        if (!score.getName().equals(lineName)) {
            return;
        }

        if (score.isWinnerTeam(teamName)) {
            winMatchNo++;
        } else {
            lostMatchNo++;
        }

        switch(score.isSurprisedResult(teamName)) {
            case -1: // surprised lost
                this.surprisedLost++;
                break;
            case 1: // surprised win
                this.surprisedWin++;
                break;
            default:
                this.normalNo++;
        }

        USTATeamPair pair = score.getPair(teamName);

        if (pair == null) {
            return;
        }

        PlayerEntity player = pair.getPlayer1();

        if (player == null) {
            return;
        }

        USTATeamSingle single = newSinglers.getOrDefault(player.getName(), new USTATeamSingle(player));

        single.addScore(score);

        newSinglers.put(player.getName(), single);
    }
    @JsonProperty
    public List<USTATeamSingle> getSinglers() {
        List<USTATeamSingle> result = new ArrayList<>(newSinglers.values());
        result.sort(USTATeamSingle::compareByWinNoAndUTR);
        return result;
    }

    @JsonProperty
    public USTATeamSingle getBestSingle() {
        if (newSinglers.size() > 0) {
            return getSinglers().get(0);
        }
        return new USTATeamSingle(null);
    }

    public int getWinMatchNo() {
        return winMatchNo;
    }

    public int getLostMatchNo() {
        return lostMatchNo;
    }

    public String getLineName() {
        return lineName;
    }

    public float getWinPrecent() {
        if (this.lostMatchNo + this.winMatchNo == 0) {
            return 0.0f;
        }

        return (float)this.winMatchNo/(float)(this.lostMatchNo+this.winMatchNo);
    }

    @JsonProperty
    public double averageUTR() {
        if (newSinglers.size() == 0) {
            return 0.0d;
        }

        double sum = 0.0d;

        for (USTATeamSingle single: newSinglers.values()) {
            sum += single.getSingleUTR();
        }

        return sum/newSinglers.size();

    }

    public int getSurprisedWin() {
        return surprisedWin;
    }

    public int getNormalNo() {
        return normalNo;
    }

    public int getSurprisedLost() {
        return surprisedLost;
    }
}
