package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
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

    @Column(name = "link")
    private String link;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usta_division_id")
    private USTADivision division;
    @JsonIgnore
    @OneToMany(mappedBy = "ustaFlight", fetch = FetchType.LAZY)
    private final Set<USTATeamEntity> teams;


    public USTAFlight(int flightNo, USTADivision division) {
        this.flightNo = flightNo;
        this.division = division;
        this.teams = new HashSet<>();
    }

    public USTAFlight() {
        this.teams = new HashSet<>();
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    public Set<USTATeamEntity> getTeams() {
        return teams;
    }


    public USTADivision getDivision() {
        return division;
    }

    public void setDivision(USTADivision division) {
        this.division = division;
    }

}
