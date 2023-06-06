package com.utr.model;

import java.util.ArrayList;
import java.util.List;

public class League {
    String id;
    String name;
    String clubId;
    List<Conference> conferences;

    public League() {
        conferences = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "League{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
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

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public List<Conference> getConferences() {
        return conferences;
    }

    public UTRTeam getTeam(String teamId) {
        for (Conference conf: this.conferences) {
            UTRTeam team = conf.getTeam(teamId);
            if (team != null) {
                return team;
            }
        }
        return null;
    }

    public Session getSession(String sessionName) {
        for (Conference conf: this.conferences) {
            for (Session session: conf.getSessions()) {
                if (session.getName().trim().equals(sessionName.trim())) {
                    return session;
                }
            }
        }
        return null;
    }
}
