package com.utr.match.usta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.utr.match.entity.USTATeamLineScore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class USTADoubleLineStat {

    String lineName;
    @JsonIgnore
    String teamName;

    @JsonIgnore
    Map<String, USTATeamPair> pairs;

    int winMatchNo = 0;
    int lostMatchNo = 0;

    int surprisedWin = 0;

    int surprisedLost = 0;
    int normalNo = 0;

    public USTADoubleLineStat(String lineName, String teamName) {
        pairs = new HashMap<>();
        this.lineName = lineName;
        this.teamName = teamName;
    }

    public void addLineScore(USTATeamLineScore score) {

        if (!score.getHomeLine().getName().equals(lineName)) {
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

        if (pair.getPlayer1() == null) {
            return;
        }

        String pairName = pair.getPlayerNames();

        pair = pairs.getOrDefault(pairName, pair);

        pair.addScore(score);

        pairs.put(pairName, pair);
    }

    @JsonProperty
    public List<USTATeamPair> getPairs() {
        List<USTATeamPair> result = new ArrayList<>(pairs.values());
        result.sort(USTATeamPair::compareByWinNoAndUTR);
        return result;
    }

    @JsonProperty
    public USTATeamPair bestPair() {
        if (pairs.size() > 0) {
            return getPairs().get(0);
        }
        return null;
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
    public double averageUTRs() {
        if (pairs.size() == 0) {
            return 0.0d;
        }

        double sum = 0.0d;

        for (USTATeamPair pair: pairs.values()) {
            sum += pair.getTotalUTR();
        }

        return sum/pairs.size();

    }

    public int getSurprisedLost() {
        return surprisedLost;
    }

    public int getSurprisedWin() {
        return surprisedWin;
    }

    public int getNormalNo() {
        return normalNo;
    }
}
