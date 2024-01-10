package com.utr.match.entity;

import javax.persistence.*;

@Entity
@Table(name = "usta_league")
public class USTALeague {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "league_year")
    private String year;

    @Column(name = "status")
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
