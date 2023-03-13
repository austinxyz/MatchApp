package com.utr.match.usta.po;

import com.utr.match.entity.USTADivision;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class USTALeaguePO {

    private String name;

    private String year;

    private List<USTADivisionPO> divisions;

    boolean inDB = false;

    public USTALeaguePO(String name, String year) {
        this.name = name;
        this.year = year;
        this.divisions = new ArrayList<>();
    }

    public USTALeaguePO() {
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

    public List<USTADivisionPO> getDivisions() {
        return divisions;
    }

    public boolean isInDB() {
        return inDB;
    }

    public void setInDB(boolean inDB) {
        this.inDB = inDB;
    }

    @Override
    public String toString() {
        return "USTALeaguePO{" +
                "name='" + name + '\'' +
                ", year='" + year + '\'' +
                ", divisions=" + divisions +
                ", inDB=" + inDB +
                '}';
    }
}
