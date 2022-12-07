package com.utr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MatchResult {
    String name; //draw.name + round.name
    @JsonIgnore
    LocalDateTime matchTime;

    String type; // single or double
    Player winner1;
    Player winner2;
    Player loser1;
    Player loser2;
    @JsonIgnore
    MatchScore score;

    public MatchResult(String name, LocalDateTime matchTime) {
        this.name = name;
        this.matchTime = matchTime;
    }

    public String getName() {
        return name;
    }

    @JsonProperty
    public String getMatchDate() {
        return matchTime.format(DateTimeFormatter.ISO_DATE);
    }

    public LocalDateTime getMatchTime() {
        return matchTime;
    }

    public String getType() {
        return winner2!=null? "double":"single";
    }

    public Player getWinner1() {
        return winner1;
    }

    public void setWinner1(Player winner1) {
        this.winner1 = winner1;
    }

    public Player getWinner2() {
        return winner2;
    }

    public void setWinner2(Player winner2) {
        this.winner2 = winner2;
    }

    public Player getLoser1() {
        return loser1;
    }

    public void setLoser1(Player loser1) {
        this.loser1 = loser1;
    }

    public Player getLoser2() {
        return loser2;
    }

    public void setLoser2(Player loser2) {
        this.loser2 = loser2;
    }

    public MatchScore getScore() {
        return score;
    }

    public void setScore(MatchScore score) {
        this.score = score;
    }

    @JsonProperty
    public String getMatchScore() {
        return this.score== null? "": score.toString();
    }
}
