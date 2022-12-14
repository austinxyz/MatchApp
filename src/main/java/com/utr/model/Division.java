package com.utr.model;

import java.util.ArrayList;
import java.util.List;

public class Division {

    String name;
    List<Player> players;
    String id;

    String displayName;

    public Division(String id) {
        this.id = id;
        this.players = new ArrayList<>();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public String getId() {
        return id;
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
        return "Division{" +
                "name='" + name + '\'' +
                ", players=" + players +
                ", displayName='" + displayName + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
