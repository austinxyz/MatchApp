package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.utr.match.model.Line;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "usta_team_scorecard")
public class USTATeamScoreCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "comment")
    private String comment;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "flight_id")
    private USTAFlight flight;

    @JsonIgnore
    @OneToMany(mappedBy = "scoreCard", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<USTATeamLineScore> lineScores;

    public USTATeamScoreCard(USTAFlight flight) {
        this.flight = flight;
        this.lineScores = new HashSet<>();
    }

    public USTATeamScoreCard() {
        this.lineScores = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    public USTAFlight getFlight() {
        return flight;
    }

    public void setFlight(USTAFlight flight) {
        this.flight = flight;
    }

    public Set<USTATeamLineScore> getLineScores() {
        return lineScores;
    }


    @JsonProperty
    public List<USTATeamLineScore> getScores() {

        List<USTATeamLineScore> res = new ArrayList<>(this.lineScores);

        res.sort(Comparator.comparing(o -> o.getHomeLine().getName()));

        return res;
    }
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonIgnore
    public USTATeam getHomeTeam() {
        if (this.lineScores.size() > 0) {
            USTATeamLineScore lineScore = this.lineScores.iterator().next();
            return lineScore.getHomeLine().getMatch().getTeam();
        }
        return null;
    }

    @JsonIgnore
    public USTATeam getGuestTeam() {
        if (this.lineScores.size() > 0) {
            USTATeamLineScore lineScore = this.lineScores.iterator().next();
            return lineScore.getGuestLine().getMatch().getTeam();
        }
        return null;
    }

    @JsonProperty
    public String getHomeTeamName() {
        USTATeam team = getHomeTeam();

        if (team != null) {
            return team.getName();
        }
        return "";
    }

    @JsonProperty
    public String getGuestTeamName() {
        USTATeam team = getGuestTeam();

        if (team != null) {
            return team.getName();
        }
        return "";
    }

    @Override
    public String toString() {
        return "USTATeamScoreCard{" +
                "id=" + id +
                ", lineScores=" + lineScores +
                '}';
    }
}
