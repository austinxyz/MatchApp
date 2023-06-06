package com.utr.model;

import java.util.ArrayList;
import java.util.List;

public class UTRTeam {
    String id;
    String name;
    List<Player> captains;
    List<Player> players;

    public UTRTeam() {
        captains = new ArrayList<>();
        players = new ArrayList<>();
    }

    public List<Player> getCaptains() {
        return captains;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", captains=" + captains +
                '}';
    }
}
