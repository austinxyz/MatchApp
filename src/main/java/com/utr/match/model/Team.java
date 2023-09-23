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
