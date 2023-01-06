package com.utr.match.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "usta_league")
public class USTALeague {
    @Id
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "league_year")
    private String year;

    public USTALeague(String name, String year) {
        this.name = name;
        this.year = year;
    }

    public USTALeague() {
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
