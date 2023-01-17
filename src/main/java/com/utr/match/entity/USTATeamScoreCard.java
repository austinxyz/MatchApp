package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "USTATeamScoreCard{" +
                "id=" + id +
                ", lineScores=" + lineScores +
                '}';
    }
}
