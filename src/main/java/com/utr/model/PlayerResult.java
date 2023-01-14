package com.utr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerResult {

    String playerId;
    int winsNumber;
    int lossesNumber;
    int withdrawsNumber;
    List<PlayerEvent> playerEvents;

    Player player;

    public Player getPlayer() {

        if (player !=null) {
            return player;
        }

        if (playerEvents == null || playerEvents.isEmpty()) {
            return null;
        }

        MatchResult match = getMatchResult(playerEvents);

        if (match == null) {
            return null;
        }

        player = match.getOwner();

        return player;
    }

    private MatchResult getMatchResult(List<PlayerEvent> playerEvents) {

        for (PlayerEvent event: playerEvents) {
            List<MatchResult> matches = event.getResults();
            if (matches != null && !matches.isEmpty()) {
                return matches.get(0);
            }
        }
        return null;
    }

    public PlayerResult(String playerId) {
        this.playerId = playerId;
        this.playerEvents = new ArrayList<>();
    }

    public List<PlayerEvent> getPlayerEvents() {
        return playerEvents;
    }

    public PlayerEvent getEventByName(String name) {
        for (PlayerEvent event: playerEvents) {
            if (event.getName().equals(name)) {
                return event;
            }
        }
        return null;
    }

    public int getWinsNumber() {
        return winsNumber;
    }

    public void setWinsNumber(int winsNumber) {
        this.winsNumber = winsNumber;
    }

    public int getLossesNumber() {
        return lossesNumber;
    }

    public void setLossesNumber(int lossesNumber) {
        this.lossesNumber = lossesNumber;
    }

    public int getWithdrawsNumber() {
        return withdrawsNumber;
    }

    public void setWithdrawsNumber(int withdrawsNumber) {
        this.withdrawsNumber = withdrawsNumber;
    }

    public List<MatchResult> getMatches(String type) {

        List<MatchResult> result = new ArrayList<>();

        for (PlayerEvent event: this.playerEvents) {
            for (MatchResult match: event.getResults()) {
                if (match.getType().equals(type)) {
                    result.add(match);
                }
            }
        }
        return result;

    }

    @JsonIgnore
    public List<Player> getAllPlayers() {
        Map<String, Player> playerMap = new HashMap<>();

        for (PlayerEvent event: this.playerEvents) {
            for (MatchResult result: event.getResults()) {
                addPlayer(playerMap, result.getWinner1());
                addPlayer(playerMap, result.getWinner2());
                addPlayer(playerMap, result.getLoser1());
                addPlayer(playerMap, result.getLoser2());
            }
        }

        return new ArrayList<>(playerMap.values());
    }

    private static void addPlayer(Map<String, Player> playerMap, Player player) {
        if (player != null ) {
            if (!playerMap.containsKey(player.getId())) {
                playerMap.put(player.getId(), player);
            }
        }
    }
}
