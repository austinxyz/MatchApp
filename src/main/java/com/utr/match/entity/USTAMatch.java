package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usta_match")
public class USTAMatch {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "match_date")
    private Date matchDate;

    @Column(name = "home_point")
    private int homePoint;

    @Column(name = "guest_point")
    private int guestPoint;

    @Column(name = "home_win")
    private Boolean homeWin;

    @Column(name = "comment")
    private String comment;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "home_team_id")
    private USTATeamEntity homeTeam;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "guest_team_id")
    private USTATeamEntity guestTeam;

    @OneToMany(mappedBy = "match", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final Set<USTAMatchLine> lines;

    public USTAMatch(USTATeamEntity homeTeam, USTATeamEntity guestTeam) {
        this.homeTeam = homeTeam;
        this.guestTeam = guestTeam;
        this.lines = new HashSet<>();
    }

    public USTAMatch() {
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

    public Set<USTAMatchLine> getLines() {
        return lines;
    }


    @JsonProperty
    public String getHomeTeamName() {
        return getTeamName(this.homeTeam);
    }

    @JsonProperty
    public String getGuestTeamName() {
        return getTeamName(this.guestTeam);
    }

    private String getTeamName(USTATeamEntity team) {
        if (team != null) {
            if (team.getAlias() != null && !team.getAlias().trim().equals("")) {
                return team.getName() + "-" + team.getAlias();
            } else {
                return team.getName();
            }
        }
        return "";
    }

    @JsonProperty
    public Long getHomeTeamId() {

        return this.homeTeam != null ? this.homeTeam.getId() : 0L;

    }

    public String getOppTeamName(String teamName) {
        if (this.getHomeTeam().getName().equals(teamName)) {
            return this.getGuestTeam().getName();
        } else {
            return this.getHomeTeam().getName();
        }
    }

    public int getScore(String teamName) {
        if (this.getHomeTeam().getName().equals(teamName)) {
            return this.getHomePoint();
        } else {
            return this.getGuestPoint();
        }
    }

    public boolean isWinner(String teamName) {
        if (this.homeWin) {
            return this.getHomeTeam().getName().equals(teamName);
        } else {
            return this.getGuestTeam().getName().equals(teamName);
        }
    }
    @JsonProperty
    public Long getGuestTeamId() {
        return this.guestTeam != null ? this.guestTeam.getId() : 0L;
    }


    public USTAMatchLine getLine(String lineName) {
        for (USTAMatchLine line : this.lines) {
            if (line.getName().equals(lineName)) {
                return line;
            }
        }
        return null;
    }

    public int getHomePoint() {
        return homePoint;
    }

    public void setHomePoint(int homePoint) {
        this.homePoint = homePoint;
    }

    public int getGuestPoint() {
        return guestPoint;
    }

    public void setGuestPoint(int guestPoint) {
        this.guestPoint = guestPoint;
    }

    public Boolean getHomeWin() {
        return homeWin;
    }

    public void setHomeWin(Boolean homeWin) {
        this.homeWin = homeWin;
    }

    public USTATeamEntity getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(USTATeamEntity homeTeam) {
        this.homeTeam = homeTeam;
    }

    public USTATeamEntity getGuestTeam() {
        return guestTeam;
    }

    public void setGuestTeam(USTATeamEntity guestTeam) {
        this.guestTeam = guestTeam;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "USTATeamMatch{" +
                "id=" + id +
                "matchDate=" + matchDate +
                ", home team=" + this.getHomeTeamName() +
                ", guest team=" + this.getGuestTeamName() +
                ", lines=" + lines +
                '}';
    }
}