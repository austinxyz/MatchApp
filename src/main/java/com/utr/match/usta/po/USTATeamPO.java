package com.utr.match.usta.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.utr.match.entity.PlayerEntity;
import com.utr.match.entity.USTADivision;
import com.utr.match.entity.USTAFlight;
import com.utr.match.entity.USTATeamMember;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class USTATeamPO {


    private String name;


    private String alias;


    private String area;


    private String flight;


    private String link;


    private String captainName;


    private String areaCode;

    private boolean inDB;

    private long id=0l;

    private String flightLink;

    public USTATeamPO(String name) {
        this.name = name;
    }

    public USTATeamPO() {
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

    public String getCaptainName() {
        return captainName;
    }

    public void setCaptainName(String captainName) {
        this.captainName = captainName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    public String getFlightLink() {
        return flightLink;
    }

    public void setFlightLink(String flightLink) {
        this.flightLink = flightLink;
    }

    public String getAreaCode() {

        if (this.areaCode != null && !this.areaCode.equals("")) {
            return areaCode;
        }

        if (area == null || area.trim().equals("")) {
            return area;
        }

        StringBuilder sb = new StringBuilder();
        for (String code: area.split(" ") ) {
            code = code.trim();
            if (code.length() > 0) {
                char c = code.charAt(0);
                if (c!='-') {
                    sb.append(c);
                }
            }
        }
        areaCode = sb.toString();
        return areaCode;
    }

    @Override
    public String toString() {
        return "USTATeamPO{" +
                "name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", link='" + link + '\'' +
                ", captainName='" + captainName + '\'' +
                ", area='" + area + '\'' +
                ", inDB='" + inDB + '\'' +
                '}';
    }
}
