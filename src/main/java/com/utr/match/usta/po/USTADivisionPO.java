package com.utr.match.usta.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.utr.match.entity.USTALeague;

import javax.persistence.*;

public class USTADivisionPO {

    private long id=0l;
    private String name;

    private String link;

    private boolean inDB;

    private String leagueName;


    public USTADivisionPO(String name) {
        this.name = name;
    }

    public USTADivisionPO() {
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInDB() {
        return inDB;
    }

    public void setInDB(boolean inDB) {
        this.inDB = inDB;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "USTADivision{" +
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

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }
}
