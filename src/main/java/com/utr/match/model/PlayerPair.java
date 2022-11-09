package com.utr.match.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public class PlayerPair {
    @JsonIgnore
    Player player1;
    @JsonIgnore
    Player player2;

    float totalUTR;

    public PlayerPair(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.totalUTR = player1.getUTR() + player2.getUTR();
    }

    public float getTotalUTR() {
        return totalUTR;
    }

    @JsonIgnore
    public int getWCount() {
        return player1.getWCount() + player2.getWCount();
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerPair that = (PlayerPair) o;
        return Objects.equals(player1, that.player1) && Objects.equals(player2, that.player2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player1, player2);
    }

    @Override
    public String toString() {
        return  player1 +
                "+" +
                player2 +
                "=" + String.format("%.02f", totalUTR);
    }

    @JsonIgnore
    public boolean has7Member() {
        return player1.getUTR() > 7 || player2.getUTR()>7;
    }

    @JsonIgnore
    public boolean has55Member() {
        return (player1.getGender().equals("F") && player1.getUTR() > 5.5) ||
               (player2.getGender().equals("F") && player2.getUTR() > 5.5);
    }

    public String getPairName() {
        return getPlayer1().getName() + "," + getPlayer2().getName();
    }

    @JsonIgnore
    public String getSecondPairName() {
        return getPlayer2().getName() + "," + getPlayer1().getName();
    }

    public String getPairInfo() {
        return toString();
    }
}
