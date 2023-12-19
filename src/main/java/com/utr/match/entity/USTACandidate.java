package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name = "usta_team_candidate")
public class USTACandidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE })
    @JoinColumn(name = "candidate_player_id")
    PlayerEntity player;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "candidate_team_id")
    private USTACandidateTeam team;

    @Column(name = "current_rating")
    private String rating;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(name = "status")
    private String status;

    public USTACandidate() {
    }

    public USTACandidate(PlayerEntity player, USTACandidateTeam team) {
        this.player = player;
        this.team = team;
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

    public boolean isUTRRequriedRefresh() {
        return player.isUTRRequriedRefresh();
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

    public String getAgeRange() {
        return player.getAgeRange();
    }

    public void setPlayer(PlayerEntity player) {
        this.player = player;
    }

    public String getArea() {
        return player.getArea();
    }

    public long getId() {
        return id;
    }

    public String getRating() {
        return rating;
    }


    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getFirstName() {
        return player.getFirstName();
    }

    public String getLastName() {
        return player.getLastName();
    }

    public double getUTR() {
        if (this.player.getDUTRStatus()!=null && this.player.getDUTRStatus().equals("Rated")) {
            return getDUTR();
        }
        return getSUTR();
    }

    public static int compareByGenderAndRating(USTACandidate cand1, USTACandidate cand2) {
        if (cand1.getGender().equals(cand2.getGender())) {
            if (cand1.getUSTARating() == null) {
                return 1;
            }
            return cand1.getUSTARating().compareTo(cand2.getUSTARating());
        } else {
            return cand1.getGender().compareTo(cand2.getGender());
        }
    }

    public int getRequiredMatchNo() {
        if (this.getUSTARating()==null) {
            return 3;
        }
        if (this.getUSTARating().indexOf("C") > 0 ||
        this.getUSTARating().indexOf("M") >0) {
            return 2;
        }

        return 3;
    }
}
