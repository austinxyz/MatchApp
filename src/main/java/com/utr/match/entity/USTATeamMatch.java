package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "usta_team_match")
public class USTATeamMatch {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "match_date")
    private Date matchDate;

    @Column(name = "point")
    private int point;

    @Column(name = "opp_point")
    private int opponentPoint;

    @Column(name = "home")
    private Boolean home;

    @ManyToOne
    @JoinColumn(name = "scorecard_id")
    private USTATeamScoreCard scoreCard;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "team_id")
    private USTATeam team;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "opp_team_id")
    private USTATeam opponentTeam;

    @JsonIgnore
    @OneToMany(mappedBy = "match", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<USTATeamMatchLine> lines;

    public USTATeamMatch(USTATeam team) {
        this.team = team;
        this.lines = new HashSet<>();
    }

    public USTATeamMatch() {
        this.lines = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    public Date getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(Date matchDate) {
        this.matchDate = matchDate;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public Boolean getHome() {
        return home;
    }

    public void setHome(Boolean home) {
        this.home = home;
    }

    public USTATeamScoreCard getScoreCard() {
        return scoreCard;
    }

    public void setScoreCard(USTATeamScoreCard scoreCard) {
        this.scoreCard = scoreCard;
    }

    public USTATeam getTeam() {
        return team;
    }

    public Set<USTATeamMatchLine> getLines() {
        return lines;
    }

    public USTATeam getOpponentTeam() {
        return opponentTeam;
    }

    public void setOpponentTeam(USTATeam opponentTeam) {
        this.opponentTeam = opponentTeam;
    }

    @JsonProperty
    public String getTeamName() {
        if (this.team!=null) {
            return this.team.getName();
        }
        return "";
    }

    @JsonProperty
    public Long getTeamId() {
        if (this.team!=null) {
            return this.team.getId();
        }
        return 0L;
    }

    @JsonProperty
    public String getOpponentTeamName() {
        if (this.opponentTeam!=null) {
            return this.opponentTeam.getName();
        }
        return "";
    }

    @JsonProperty
    public Long getOpponentTeamId() {
        if (this.opponentTeam!=null) {
            return this.opponentTeam.getId();
        }
        return 0L;
    }

    public int getOpponentPoint() {
        return opponentPoint;
    }

    public void setOpponentPoint(int opponentPoint) {
        this.opponentPoint = opponentPoint;
    }

    @Override
    public String toString() {
        return "USTATeamMatch{" +
                "id=" + id +
                "matchDate=" + matchDate +
                ", point=" + point +
                ", home=" + home +
                ", lines=" + lines +
                '}';
    }
}
