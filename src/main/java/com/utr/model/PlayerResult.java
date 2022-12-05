package com.utr.model;

import java.util.ArrayList;
import java.util.List;

public class PlayerResult {

    String playerId;
    int winsNumber;
    int lossesNumber;
    int withdrawsNumber;
    List<PlayerEvent> playerEvents;

    public PlayerResult(String playerId) {
        this.playerId = playerId;
        this.playerEvents = new ArrayList<>();
    }

    public List<PlayerEvent> getPlayerEvents() {
        return playerEvents;
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
}
