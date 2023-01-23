package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "usta_flight")
public class USTAFlight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "flight_no")
    private int flightNo;

    @Column(name = "area")
    private String area;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "usta_division_id")
    private USTADivision division;

    @JsonIgnore
    @OneToMany(mappedBy = "ustaFlight", fetch = FetchType.LAZY)
    private Set<USTATeam> teams;

    @JsonIgnore
    @OneToMany(mappedBy = "flight", fetch = FetchType.LAZY)
    private Set<USTATeamScoreCard> teamScoreCards;

    public USTAFlight(int flightNo, USTADivision division) {
        this.flightNo = flightNo;
        this.division = division;
        this.teams = new HashSet<>();
        this.teamScoreCards = new HashSet<>();
    }

    public USTAFlight() {
        this.teams = new HashSet<>();
        this.teamScoreCards = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    public int getFlightNo() {
        return flightNo;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Set<USTATeam> getTeams() {
        return teams;
    }


    public USTADivision getDivision() {
        return division;
    }

    public void setDivision(USTADivision division) {
        this.division = division;
    }

}
