package com.utr.match.entity;

import javax.persistence.*;

@Entity
@Table(name = "usta_division")
public class USTADivision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "level")
    private String level;

    @ManyToOne
    @JoinColumn(name = "usta_league_id")
    private USTALeague league;

    @Column(name="age_range")
    String ageRange;

    public USTADivision(String name, String level, USTALeague league) {
        this.name = name;
        this.level = level;
        this.league = league;
    }

    public USTADivision() {
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public USTALeague getLeague() {
        return league;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }
}
