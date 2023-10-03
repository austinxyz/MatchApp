package com.utr.match.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.utr.match.entity.USTATeamMember;
import com.utr.model.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Team {

    String name;
    List<Player> players;
    String displayName;
    Map<String, Line> lines;
    @JsonIgnore
    String teamId;
    List<Lineup> preferedLineups;

    float teamUTR;

    public float getTeamUTR() {
        return teamUTR;
    }

    public Team(String name) {
        this.name = name;
        this.players = new ArrayList<>();
        this.lines = new HashMap<>();
    }

    public String getDisplayName() {
        return displayName==null? name:displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        players.sort((Player o1, Player o2) -> Double.compare(o2.getUTR(), o1.getUTR()));
        return players;
    }

    public void caculateTeamUTR() {

        int maleCount = 0;
        int femaleCount = 0;

        for (Line line: lines.values()) {
            femaleCount += line.getFemaleCount();
            maleCount += (2-line.getFemaleCount());
        }

        float sumUTR = 0.0f;
        int maleIndex = 0;
        int femaleIndex = 0;
        int i = 0;

        List<Player> sortedPlayers = this.getPlayers();

        while (maleIndex < maleCount || femaleIndex < femaleCount) {
            Player player = sortedPlayers.get(i);

            if (player.getGender().equals("M") && maleIndex < maleCount) {
                sumUTR += player.getUTR();
                maleIndex++;
            }

            if (player.getGender().equals("F") && femaleIndex < femaleCount) {
                sumUTR += player.getUTR();
                femaleIndex++;
            }

            i++;
        }

        this.teamUTR = sumUTR / (float)(femaleCount + maleCount);
    }

    public Map<String, Line> getLines() {
        return lines;
    }

    public Line getD1() {
        return lines.get("D1");
    }

    public Line getD2() {
        return lines.get("D2");
    }

    public Line getD3() {
        return lines.get("D3");
    }

    public Line getWD() {
        return lines.get("WD");
    }

    public Line getMD() {
        return lines.get("MD");
    }

    public List<Lineup> getPreferedLineups() {
        return preferedLineups;
    }

    public void setPreferedLineups(List<Lineup> preferedLineups) {
        this.preferedLineups = preferedLineups;
    }

    public Player getPlayer(String playerName) {
        for (Player player : this.players) {
            if (playerName.equals(player.getName())) {
                return player;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                ", players=" + players +
                ", displayName='" + displayName + '\'' +
                ", teamId='" + teamId + '\'' +
                '}';
    }
}
