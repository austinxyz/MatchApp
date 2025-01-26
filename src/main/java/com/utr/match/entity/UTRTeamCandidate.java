package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.utr.match.model.PlayerPair;
import com.utr.model.Player;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "team_candidates")
public class UTRTeamCandidate {

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
    @JoinColumn(name = "division_id")
    private DivisionEntity division;

    @Column(name = "self_rating")
    private String rating;

    @Column(name = "level_range")
    private String range; // Below, Avg, Good, Strong, Top

    @Transient
    Map<String, List<Player>> candidatePartners = new HashMap<>();

    public UTRTeamCandidate() {
    }

    public UTRTeamCandidate(PlayerEntity player, DivisionEntity division) {
        this.player = player;
        this.division = division;
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

    public String getRange() {
        return range;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setRange(String range) {
        this.range = range;
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
        double selfRatingUTR = SelfRatingHelper.getSelfRatingUTR(this.getRating(), this.getRange(), this.getGender());
        if (selfRatingUTR <0.1) {
            return getDUTR();
        } else {
            return selfRatingUTR;
        }
    }

    public String getSelfRating() {
        if (this.player.getDUTRStatus()!=null && this.player.getDUTRStatus().equals("Rated")) {
            if (this.getUSTARating()!=null && !this.getUSTARating().trim().equals("")) {
                return this.getUSTARating() + " Player";
            }
            return "Rated UTR Player";
        }

        return this.getRange() + " " + this.getRating() + "s Player";
    }

    @JsonProperty
    public Map<String, String> getLinePartners() {
        Map<String, String> results = new HashMap<>();

        for (String key: this.candidatePartners.keySet()) {
            List<Player> partners = this.candidatePartners.get(key);
            partners.sort((Player o1, Player o2) -> Float.compare(o2.getUTR(), o1.getUTR()));
            StringBuilder builder = new StringBuilder();
            int index = 0;
            for (Player partner: partners) {
                builder.append(partner.getName()).append("(").append(partner.getGender()).append(")-").append(partner.getUTR()).append(",");
                index++;
                if (index >4) {
                    break;
                }
            }
            results.put(key, builder.toString());

        }
        return results;
    }

    public void addPartner(String lineName, PlayerPair pair) {
        Player partner = null;
        if (pair.getPlayer1().getId().equals(this.player.getUtrId())) {
            partner = pair.getPlayer2();
        } else if (pair.getPlayer2().getId().equals(this.player.getUtrId())) {
            partner = pair.getPlayer1();
        } else {
            return;
        }

        List<Player> partners = this.candidatePartners.getOrDefault(lineName, new ArrayList<>());
        partners.add(partner);
        this.candidatePartners.put(lineName, partners);
    }
}
