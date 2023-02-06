package com.utr.model;

import java.util.ArrayList;
import java.util.Comparator;
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
        players.sort((Player o1, Player o2)-> Double.compare(o2.getsUTR(), o1.getsUTR()));
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

    public Player getPlayerByUTRId(String utrId) {
        for (Player player : this.players) {
            if (utrId.equals(player.getId())) {
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
