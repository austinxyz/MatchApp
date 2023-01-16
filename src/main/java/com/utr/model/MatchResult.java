package com.utr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchResult {
    public static final String DOUBLE = "double";
    public static final String SINGLE = "single";
    String name; //draw.name + round.name
    @JsonIgnore
    LocalDateTime matchTime;

    @JsonIgnore
    Player winner1;
    @JsonIgnore
    Player winner2;
    @JsonIgnore
    Player loser1;
    @JsonIgnore
    Player loser2;
    @JsonIgnore
    MatchScore score;

    String ownerId;

    public MatchResult(String name, LocalDateTime matchTime, String ownerId) {
        this.name = name;
        this.matchTime = matchTime;
        this.ownerId = ownerId;
    }

    public Player getOwner() {
        if (winner1.getId().equals(ownerId)) {
            return winner1;
        }
        if (winner2!=null && winner2.getId().equals(ownerId)) {
            return winner2;
        }
        if (loser1.getId().equals(ownerId)) {
            return loser1;
        }
        if (loser2!=null && loser2.getId().equals(ownerId)) {
            return loser2;
        }
        return null;
    }

    @JsonProperty
    public boolean isWinner() {
        return (winner1 != null && winner1.getId().equals(ownerId)) || (winner2 != null && winner2.getId().equals(ownerId));
    }

    @JsonIgnore
    public List<Player> getOpponents() {
        List<Player> result = new ArrayList<>();

        if (isWinner()) {
            result.add(loser1);

            if (loser2 != null) {
                result.add(loser2);
            }
        } else {
            result.add(winner1);
            if (winner2 !=null) {
                result.add(winner2);
            }
        }
        return result;
    }

    @JsonProperty
    public List<Map<String, String>> getLoserInfo() {
        List<Map<String, String>> result=new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        if (this.getType().equals("single")) {
            builder.append(loser1.getName()).append("-")
                    .append(loser1.getsUTR())
                    .append("(").append(loser1.getsUTRStatus().charAt(0)).append(")");
            Map<String, String> player = getPlayerInfoMap(builder.toString(), loser1);
            result.add(player);
        } else {
            builder.append(loser1.getName()).append("-")
                    .append(loser1.getdUTR())
                    .append("(").append(loser1.getdUTRStatus().charAt(0)).append(")");
            Map<String, String> player = getPlayerInfoMap(builder.toString(), loser1);
            result.add(player);

            builder = new StringBuilder();
            builder.append(loser2.getName()).append("-")
                    .append(loser2.getdUTR())
                    .append("(").append(loser2.getdUTRStatus().charAt(0)).append(")");
            Map<String, String> player2 = getPlayerInfoMap(builder.toString(), loser2);
            result.add(player2);
        }
        return result;
    }

    @JsonProperty
    public List<Map<String, String>> getWinnerInfo() {
        List<Map<String, String>> result=new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        if (this.getType().equals("single")) {
            builder.append(winner1.getName()).append("-")
                    .append(winner1.getsUTR())
                    .append("(").append(winner1.getsUTRStatus().charAt(0)).append(")");
            Map<String, String> player = getPlayerInfoMap(builder.toString(), winner1);
            result.add(player);
        } else {
            builder.append(winner1.getName()).append("-")
                    .append(winner1.getdUTR())
                    .append("(").append(winner1.getdUTRStatus().charAt(0)).append(")");
            Map<String, String> player = getPlayerInfoMap(builder.toString(), winner1);
            result.add(player);

            builder = new StringBuilder();
            builder.append(winner2.getName()).append("-")
                    .append(winner2.getdUTR())
                    .append("(").append(winner2.getdUTRStatus().charAt(0)).append(")");
            Map<String, String> player2 = getPlayerInfoMap(builder.toString(), winner2);
            result.add(player2);
        }
        return result;
    }

    private Map<String, String> getPlayerInfoMap(String info, Player player) {
        Map<String, String> playerInfo = new HashMap<>();
        playerInfo.put("info", info);
        playerInfo.put("utrId", player.getId());
        playerInfo.put("name", player.getName());
        return playerInfo;
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
        return winner2!=null? DOUBLE : SINGLE;
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
