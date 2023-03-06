package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name = "usta_division")
public class USTADivision {
    @Column(name = "age_range")
    String ageRange;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "level")
    private String level;
    @Column(name = "link")
    private String link;
    @Column(name = "type")
    private String type;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "usta_league_id")
    private USTALeague league;

    public USTADivision(String name, String level, USTALeague league) {
        this.name = name;
        this.level = level;
        this.league = league;
    }

    public USTADivision() {
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    @Override
    public String toString() {
        return "USTADivision{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                '}';
    }

    @JsonProperty
    public String getUSTALeagueId() {
        if (this.link == null || this.link.length() == 0) {
            return "";
        }
        int idStart = link.indexOf("leagueid=") + 9;
        return link.substring(idStart, link.length());

    }
}
