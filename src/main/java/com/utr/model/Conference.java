package com.utr.model;

import java.util.ArrayList;
import java.util.List;

public class Conference {
    String id;
    String name;
    List<Session> sessions;

    List<Player> organizers;

    public Conference() {
        sessions = new ArrayList<>();
        organizers = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Conference{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public List<Player> getOrganizers() {
        return organizers;
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
        for (Session session: this.sessions) {
            UTRTeam team = session.getTeam(teamId);
            if (team!=null) {
                return team;
            }
        }
        return null;
    }
}
