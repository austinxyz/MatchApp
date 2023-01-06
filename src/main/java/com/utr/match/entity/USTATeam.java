package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "usta_team")
public class USTATeam {
    @Id
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "alias")
    private String alias;

    @Column(name = "area")
    private String area;

    @Column(name = "flight")
    private String flight;

    @Column(name = "link")
    private String link;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "usta_division_id")
    private USTADivision division;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usta_team_player",
            joinColumns = {@JoinColumn(name = "usta_team_id")},
            inverseJoinColumns = {@JoinColumn(name = "player_id")})
    private List<PlayerEntity> players;

    @ManyToOne
    @JoinColumn(name = "captain_id")
    private PlayerEntity captain;

    public USTATeam(String name, USTADivision division) {
        this.name = name;
        this.division = division;
    }

    public USTATeam() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFlight() {
        return flight;
    }

    public void setFlight(String flight) {
        this.flight = flight;
    }

    public USTADivision getDivision() {
        return division;
    }

    public List<PlayerEntity> getPlayers() {
        return players;
    }

    public PlayerEntity getCaptain() {
        return captain;
    }

    public void setCaptain(PlayerEntity captain) {
        this.captain = captain;
    }

    @JsonProperty
    public String getDivisionName() {
        if (this.division != null) {
            return division.getName() + " " + division.getLevel();
        }
        return "";
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
