package com.utr.match.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {

    Map<String, Team> teams;
    String name;

    public List<Team> getTeams() {
        return new ArrayList<>(teams.values());
    }

    public void addTeam(Team team) {
        teams.put(team.getTeamId(), team);
    }

    public Team getTeam(String teamId) {
        return teams.get(teamId);
    }

    public Team getTeamByName(String teamName) {
        for (Team team: this.teams.values()) {
            if (team.getName().equals(teamName)) {
                return team;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }

    String id;

    public Event(String id) {
        this.id = id;
        this.teams = new HashMap<String, Team>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
