package com.utr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MatchResult {
    String name; //draw.name + round.name
    @JsonIgnore
    LocalDateTime matchTime;


    Player winner1;
    Player winner2;
    Player loser1;
    Player loser2;
    @JsonIgnore
    MatchScore score;

    String ownerId;

    public MatchResult(String name, LocalDateTime matchTime, String ownerId) {
        this.name = name;
        this.matchTime = matchTime;
        this.ownerId = ownerId;
    }

    @JsonProperty
    public boolean isWinner() {
        return (winner1 != null && winner1.getId().equals(ownerId)) || (winner2 != null && winner2.getId().equals(ownerId));
    }

    @JsonProperty
    public String getLoserInfo() {
        StringBuilder builder = new StringBuilder();

        if (this.getType().equals("single")) {
            builder.append(loser1.getName()).append("-")
                    .append(loser1.getsUTR())
                    .append("(").append(loser1.getsUTRStatus().charAt(0)).append(")");
        } else {
            builder.append(loser1.getName()).append("-")
                    .append(loser1.getdUTR())
                    .append("(").append(loser1.getdUTRStatus().charAt(0)).append(")");
            builder.append("+");
            builder.append(loser2.getName()).append("-")
                    .append(loser2.getdUTR())
                    .append("(").append(loser2.getdUTRStatus().charAt(0)).append(")");
        }
        return builder.toString();
    }

    @JsonProperty
    public String getWinnerInfo() {
        StringBuilder builder = new StringBuilder();

        if (this.getType().equals("single")) {
            builder.append(winner1.getName()).append("-")
                    .append(winner1.getsUTR())
                    .append("(").append(winner1.getsUTRStatus().charAt(0)).append(")");
        } else {
            builder.append(winner1.getName()).append("-")
                    .append(winner1.getdUTR())
                    .append("(").append(winner1.getdUTRStatus().charAt(0)).append(")");
            builder.append("+");
            builder.append(winner2.getName()).append("-")
                    .append(winner2.getdUTR())
                    .append("(").append(winner2.getdUTRStatus().charAt(0)).append(")");
        }
        return builder.toString();
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
        return this.score== null? "default": score.toString();
    }
}
