package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.utr.match.entity.PlayerEntity;

import javax.persistence.*;

@Entity
@Table(name = "usta_team_member")
public class USTATeamMember {

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
    @JoinColumn(name = "usta_team_id")
    private USTATeamEntity team;

    public USTATeamEntity getTeam() {
        return team;
    }

    public void setTeam(USTATeamEntity team) {
        this.team = team;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public boolean isQualifiedPo() {
        return qualifiedPo == null? false: qualifiedPo.booleanValue();
    }

    public void setQualifiedPo(boolean qualifiedPo) {
        this.qualifiedPo = qualifiedPo;
    }

    @Column(name = "current_rating")
    private String rating;

    @Column(name = "win_no")
    Integer winNo;

    @Column(name = "lost_no")
    Integer lostNo;

    @Column(name = "qualified_po")
    Boolean qualifiedPo;

    public USTATeamMember() {

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

    public int getWinNo() {
        return winNo==null? 0:winNo.intValue();
    }

    public void setWinNo(int winNo) {
        this.winNo = winNo;
    }

    public int getLostNo() {
        return lostNo==null?0:lostNo.intValue();
    }

    public void setLostNo(int lostNo) {
        this.lostNo = lostNo;
    }

    public float getLevel() {
        String rating = this.getRating()==null? this.player.getUstaRating(): this.getRating();
        if (rating != null && rating.length() >=3) {
            try {
                return Float.parseFloat(rating.substring(0, 3));
            } catch (NumberFormatException ex){
                System.out.println(" wrong rating " + rating);
            }
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

    public boolean isRegisteredBayArea() {
        return player.isRegisteredBayArea();
    }

    public float getWinPercent() {
        if (this.getWinNo() + this.getLostNo() == 0) {
            return 0.0f;
        }
        return (float) (this.getWinNo()) / (float) (this.getWinNo() + this.getLostNo());
    }
}
