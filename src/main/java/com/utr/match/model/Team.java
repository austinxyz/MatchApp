package com.utr.match.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Team {

    String name;
    List<Player> players;

    @JsonIgnore
    Map<String, Line> lines;

    List<Lineup> preferedLineups;

    public Team(String name) {
        this.name = name;
        this.players = new ArrayList<>();
        this.lines = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
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

}
