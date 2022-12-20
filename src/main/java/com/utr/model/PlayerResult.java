package com.utr.model;

import java.util.ArrayList;
import java.util.List;

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
}
