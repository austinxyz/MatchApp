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
}
