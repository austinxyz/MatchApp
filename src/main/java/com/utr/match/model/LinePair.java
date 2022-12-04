package com.utr.match.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.utr.model.Player;

public class LinePair {
    @JsonIgnore
    Line line;
    PlayerPair pair;

    public LinePair(Line line, PlayerPair pair) {
        this.line = line;
        this.pair = pair;
    }

    @JsonIgnore
    public Player getPlayer1() {
        return pair.getPlayer1();
    }

    @JsonIgnore
    public Player getPlayer2() {
        return pair.getPlayer2();
    }

    public PlayerPair getPair() {
        return pair;
    }

    @JsonIgnore
    public String getPairName() {
        return getPlayer1().toString() + "," + getPlayer2().toString();
    }
    @JsonIgnore
    public Line getLine() {
        return line;
    }

    @JsonIgnore
    public float getGAP() {
        return line.getUtrLimit() - pair.getTotalUTR();
    }

    @Override
    public String toString() {
        return getPairName() + ":" + String.format("%.02f", getPair().getTotalUTR());
    }
}
