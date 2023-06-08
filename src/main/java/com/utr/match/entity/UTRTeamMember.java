package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name = "utr_team_member")
public class UTRTeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE })
    @JoinColumn(name = "player_id")
    PlayerEntity player;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "utr_team_id")
    private UTRTeamEntity team;

    @Column(name="match_utr")
    Double matchUTR;

    public UTRTeamEntity getTeam() {
        return team;
    }

    public void setTeam(UTRTeamEntity team) {
        this.team = team;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    @Column(name = "current_rating")
    private String rating;

    public UTRTeamMember() {
    }
    public UTRTeamMember(PlayerEntity player) {
        this.player = player;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public double getDUTR() {
        return player.getDUTR();
    }

    public double getSUTR() {
        return player.getSUTR();
    }

    public String getName() {
        return player.getName();
    }

    public long getId() {
        return this.id;
    }

    @JsonProperty("playerId")
    public long getPlayerId() {
        return player.getId();
    }

    public String getGender() {
        return player.getGender();
    }

    @JsonProperty("ustaRating")
    public String getUSTARating() {
        return player.getUstaRating();
    }

    @JsonProperty("ustaNorcalId")
    public String getUstaNorcalId() {
        return player.getUstaNorcalId();
    }

    public double getDynamicRating() {
        return player.getDynamicRating();
    }

    public String getDUTRStatus() {
        return player.getDUTRStatus() == null? "":player.getDUTRStatus();
    }

    public String getSUTRStatus() {
        return player.getSUTRStatus() == null? "": player.getSUTRStatus();
    }

    public boolean isRefreshedUTR() {
        return player.isRefreshedUTR();
    }

    public float getSuccessRate() {
        return player.getSuccessRate();
    }

    public float getWholeSuccessRate() {
        return player.getWholeSuccessRate();
    }

    public String getSummary() {
        return player.getSummary();
    }

    public float getLevel() {
        String rating = this.player.getUstaRating();
        if (rating != null && rating.length() >=3) {
            return Float.parseFloat(rating.substring(0,3));
        }
        return 0.0f;
    }

    public String getUtrId() {
        return player.getUtrId();
    }

    public String getUstaId() {
        return player.getUstaId();
    }

    public String getNoncalLink() {
        return player.getNoncalLink();
    }

    public String getTennisRecordLink() {
        return player.getTennisRecordLink();
    }

    public boolean isLefty() {
        return player.isLefty();
    }

    public String getTennisLinkURL() {
        return player.getTennisLinkURL();
    }

    public double getMatchUTR() {
        return matchUTR==null? 0.0d: matchUTR.doubleValue();
    }

    public void setMatchUTR(Double matchUTR) {
        this.matchUTR = matchUTR;
    }

    public String getAgeRange() {
        return player.getAgeRange();
    }

    public void setPlayer(PlayerEntity player) {
        this.player = player;
    }

    public String getArea() {
        return player.getArea();
    }

    public String getFirstName() {
        return player.getFirstName();
    }

    public String getLastName() {
        return player.getLastName();
    }

}
