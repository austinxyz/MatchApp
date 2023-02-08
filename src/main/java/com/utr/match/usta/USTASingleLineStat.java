package com.utr.match.usta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.USTATeamLineScore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class USTASingleLineStat {

    String lineName;
    @JsonIgnore
    String teamName;

    @JsonIgnore
    Map<String, USTATeamSingle> singlers;

    int winMatchNo = 0;
    int lostMatchNo = 0;

    public USTASingleLineStat(String lineName, String teamName) {
        singlers = new HashMap<>();
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

        PlayerEntity player = pair.getPlayer1();

        if (player == null) {
            return;
        }

        USTATeamSingle single = singlers.getOrDefault(player.getName(), new USTATeamSingle(player));

        single.addScore(score);

        singlers.put(player.getName(), single);

    }

    @JsonProperty
    public List<USTATeamSingle> getSinglers() {
        List<USTATeamSingle> result = new ArrayList<>(singlers.values());
        result.sort(USTATeamSingle::compareByWinNoAndUTR);
        return result;
    }

    @JsonProperty
    public USTATeamSingle getBestSingle() {
        if (singlers.size() > 0) {
            return getSinglers().get(0);
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
}
