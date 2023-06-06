package com.utr.model;

import java.util.ArrayList;
import java.util.List;

public class Session {
    String id;
    String name;
    List<UTRTeam> teams;

    public Session() {
        teams = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Session{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public List<UTRTeam> getTeams() {
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


    public UTRTeam getTeam(String teamId) {
        for (UTRTeam team: this.teams) {
            if (teamId.equals(team.getId())) {
                return team;
            }
        }
        return null;
    }

    public UTRTeam getTeamByName(String teamName) {
        for (UTRTeam team: this.teams) {
            if (teamName.trim().equals(team.getName().trim())) {
                return team;
            }
        }
        return null;
    }
}
