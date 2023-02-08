package com.utr.match.usta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.USTATeamEntity;
import com.utr.match.entity.USTATeamLineScore;
import com.utr.match.entity.USTATeamScoreCard;

import java.util.*;

public class USTATeam {
    @JsonProperty("team")
    USTATeamEntity teamEntity;

    @JsonIgnore
    Map<String, List<USTATeamScoreCard>> scores;
    List<USTATeamMember> players;

    @JsonIgnore
    List<USTATeamMember> singleMembers;

    @JsonIgnore
    List<USTATeamMember> drMembers;

    Map<String, USTADoubleLineStat> doubleLineStats;

    Map<String, USTASingleLineStat> singleLineStats;

    public List<USTATeamMember> getPlayers() {
        return players;
    }

    public boolean isMixed() {
        return mixed;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getCurrentWinMatchNo() {
        return currentWinMatchNo;
    }

    int currentScore=0;
    int currentWinMatchNo=0;

    public int getTotalMatchNo() {
        return totalMatchNo;
    }

    public Map<String, USTADoubleLineStat> getDoubleLineStats() {
        return doubleLineStats;
    }

    public Map<String, USTASingleLineStat> getSingleLineStats() {
        return singleLineStats;
    }

    int totalMatchNo=0;

    boolean mixed = false;

    float level = 7.0f;

    public USTATeam(USTATeamEntity teamEntity) {
        this.teamEntity = teamEntity;
        this.players = new ArrayList<>();

        String gender = "";

        for (PlayerEntity player: teamEntity.getPlayers()) {
            USTATeamMember member = new USTATeamMember(player);
            this.players.add(member);
            if (gender.equals("")) {
                gender = player.getGender();
            }

            if (!gender.equals(player.getGender())) {
                mixed = true;
                
            }
        }
        updateTeamLevel(mixed, teamEntity.getName());
        
        this.singleMembers = new ArrayList<>(this.players);
        this.drMembers = new ArrayList<>(this.players);
        this.scores = new HashMap<>();
        this.doubleLineStats = new HashMap<>();
        this.singleLineStats = new HashMap<>();

    }

    private void updateTeamLevel(boolean mixed, String name) {

        String leagueAbbr = mixed? "MX":"AM";

        int start = name.indexOf(leagueAbbr) + leagueAbbr.length();

        int end = start+3;

        String levelStr = name.substring(start, end);

        this.level = Float.parseFloat(levelStr);
    }

    public void addScore(USTATeamScoreCard scoreCard) {

        String oppTeamName = scoreCard.getOpponentTeam(this.teamEntity.getName());
        List<USTATeamScoreCard> oppTeamScores = this.scores.getOrDefault(oppTeamName, new ArrayList<>());
        oppTeamScores.add(scoreCard);
        this.scores.put(oppTeamName, oppTeamScores);

        String teamName = teamEntity.getName();
        this.currentScore += scoreCard.getScore(teamName);
        if (scoreCard.isWinner(teamName)) {
            currentWinMatchNo++;
        }
        for (USTATeamLineScore lineScore: scoreCard.getLineScores()) {
            String lineName = lineScore.getHomeLine().getName();
            if (lineName.startsWith("D")) {
                USTADoubleLineStat doubleLineStat = this.doubleLineStats.getOrDefault(lineName,
                        new USTADoubleLineStat(lineName, this.teamEntity.getName()));
                doubleLineStat.addLineScore(lineScore);
                this.doubleLineStats.put(lineName, doubleLineStat);
                updateTeamMemberWLRatio(lineScore);
            }
            if (lineName.startsWith("S")) {
                USTASingleLineStat singleLineStat = this.singleLineStats.getOrDefault(lineName,
                        new USTASingleLineStat(lineName, this.teamEntity.getName()));
                singleLineStat.addLineScore(lineScore);
                this.singleLineStats.put(lineName, singleLineStat);
                updateTeamMemberWLRatio(lineScore);
            }
        }
        this.totalMatchNo++;
    }

    private void updateTeamMemberWLRatio(USTATeamLineScore lineScore) {

        USTATeamPair pair = lineScore.getPair(this.getTeamName());
        USTATeamMember m1 = getTeamMember(pair.getPlayer1());
        USTATeamMember m2 = getTeamMember(pair.getPlayer2());

        boolean win = lineScore.isWinnerTeam(this.getTeamName());
        if (win) {
            if (m1 != null) {
                m1.setWinNo(m1.getWinNo() + 1);
            }
            if (m2 != null) {
                m2.setWinNo(m2.getWinNo() + 1);
            }
        } else {
            if (m1 != null) {
                m1.setLostNo(m1.getLostNo() + 1);
            }
            if (m2 != null) {
                m2.setLostNo(m1.getLostNo() + 1);
            }
        }

    }

    private USTATeamMember getTeamMember(PlayerEntity player) {
        if (player == null) {
            return null;
        }
        for (USTATeamMember member : this.players) {
            if (member.getId() == player.getId()) {
                return member;
            }
        }
        return null;
    }

    public List<USTATeamScoreCard> getScores(String teamName) {
        return this.scores.get(teamName);
    }

    @JsonIgnore
    public List<USTATeamScoreCard> getAllScores() {
        List<USTATeamScoreCard> result = new ArrayList<>();
        for (List<USTATeamScoreCard> teamScores: this.scores.values()) {
            result.addAll(teamScores);
        }
        return result;
    }

    @JsonProperty
    public String getTeamName() {
        return this.teamEntity.getName();
    }

    @JsonProperty
    public Long getId() {
        return this.teamEntity.getId();
    }

    public USTATeamPair getBestUTRDouble() {
        if (!mixed) {
            players.sort((USTATeamMember o1, USTATeamMember o2) -> Double.compare(o2.getDUTR(), o1.getDUTR()));
            if (players.size() >= 2) {
                return new USTATeamPair(players.get(0).getPlayer(), players.get(1).getPlayer());
            }
        } else {
            USTATeamMember malePlayer = null;
            USTATeamMember femalePlayer = null;
            List<USTATeamPair> pairs = new ArrayList<>();

            List<USTATeamMember> males = new ArrayList<>();
            List<USTATeamMember> females = new ArrayList<>();

            for (USTATeamMember player: this.players) {
                if (player.getGender().equals("M")) {
                    males.add(player);
                }

                if (player.getGender().equals("F")) {
                    females.add(player);
                }
            }

            for (USTATeamMember male: males) {
                for (USTATeamMember female: females) {
                   if ((male.getLevel() + female.getLevel())<= this.level + 0.1) {
                       pairs.add(new USTATeamPair(male.getPlayer(), female.getPlayer()));
                   }
                }
            }

            pairs.sort((USTATeamPair p1, USTATeamPair p2) -> Double.compare(p2.getTotalUTR(), p1.getTotalUTR()));

            return pairs.iterator().next();
        }
        return null;
    }

    public USTATeamPair getBestDRDouble() {
        if (!mixed) {
            players.sort((USTATeamMember o1, USTATeamMember o2) -> Double.compare(o2.getDynamicRating(), o1.getDynamicRating()));
            if (players.size() >= 2) {
                return new USTATeamPair(players.get(0).getPlayer(), players.get(1).getPlayer());
            }
        } else {
            USTATeamMember malePlayer = null;
            USTATeamMember femalePlayer = null;
            List<USTATeamPair> pairs = new ArrayList<>();

            List<USTATeamMember> males = new ArrayList<>();
            List<USTATeamMember> females = new ArrayList<>();

            for (USTATeamMember player: this.players) {
                if (player.getGender().equals("M")) {
                    males.add(player);
                }

                if (player.getGender().equals("F")) {
                    females.add(player);
                }
            }

            for (USTATeamMember male: males) {
                for (USTATeamMember female: females) {
                    if ((male.getLevel() + female.getLevel())<= this.level + 0.1) {
                        pairs.add(new USTATeamPair(male.getPlayer(), female.getPlayer()));
                    }
                }
            }

            pairs.sort((USTATeamPair p1, USTATeamPair p2) -> Double.compare(p2.getTotalDR(), p1.getTotalDR()));

            return pairs.iterator().next();
        }
        return null;
    }

    public USTATeamMember getBestUTRSingle() {
        if (!mixed) {
            singleMembers.sort((USTATeamMember o1, USTATeamMember o2) -> Double.compare(o2.getSUTR(), o1.getSUTR()));
            return singleMembers.iterator().next();
        }
        return null;
    }

    public USTATeamMember getBestDRSingle() {
        if (!mixed) {
            singleMembers.sort((USTATeamMember o1, USTATeamMember o2) -> Double.compare(o2.getDynamicRating(), o1.getDynamicRating()));
            return singleMembers.iterator().next();
        }
        return null;
    }

    @JsonIgnore
    public Set<String> getOpponentTeams() {
        return this.scores.keySet();
    }

    public float getLevel() {
        return level;
    }

    public USTATeamEntity getTeamEntity() {
        return teamEntity;
    }
}
