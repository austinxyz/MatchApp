package com.utr.model;

import java.util.ArrayList;
import java.util.List;

public class League {
    String id;
    String name;
    List<Team> teams;

    public League() {
        teams = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "League{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public List<Team> getTeams() {
        return teams;
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


    public Team getTeam(String teamId) {
        for (Team team: this.teams) {
            if (teamId.equals(team.getId())) {
                return team;
            }
        }
        return null;
    }
}
