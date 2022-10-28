package com.utr.match.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Team {

    String name;
    List<Player> players;
    @JsonIgnore
    List<Line> lines;

    List<Lineup> preferedLineups;

    public Team(String name) {
        this.name = name;
        this.players = new ArrayList<>();
        this.lines = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Line> getLines() {
        return lines;
    }

    public List<Lineup> getPreferedLineups() {
        return preferedLineups;
    }

    public void setPreferedLineups(List<Lineup> preferedLineups) {
        this.preferedLineups = preferedLineups;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("==================================\n");
        sb.append("com.utr.match.model.Team ").append(this.name).append(":").append(this.players.size()).append("\n");

        for (Line line: this.lines) {
            sb.append(line.toString());
        }

        return sb.toString();
    }

}
