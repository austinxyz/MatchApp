package com.utr.match.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public class Player {

    String name;
    String gender;
    float UTR;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    String id;

    double dUTR;

    @JsonIgnore
    double sUTR;

    public double getdUTR() {
        return dUTR;
    }

    public void setdUTR(double dUTR) {
        this.dUTR = dUTR;
    }

    public double getsUTR() {
        return sUTR;
    }

    public void setsUTR(double sUTR) {
        this.sUTR = sUTR;
    }

    public String getdUTRStatus() {
        return dUTRStatus;
    }

    public void setdUTRStatus(String dUTRStatus) {
        this.dUTRStatus = dUTRStatus;
    }

    public String getsUTRStatus() {
        return sUTRStatus;
    }

    public void setsUTRStatus(String sUTRStatus) {
        this.sUTRStatus = sUTRStatus;
    }

    String dUTRStatus;

    @JsonIgnore
    String sUTRStatus;

    public Player(String name, String gender, String UTR) {
        this.name = name;
        this.gender = gender;
        this.UTR = Float.parseFloat(UTR);
    }

    public void setUTR(String UTR) {
        this.UTR = Float.parseFloat(UTR);
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public float getUTR() {
        return UTR;
    }

    @JsonIgnore
    public int getWCount() {
        return this.gender.equals("F") ? 1 : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Float.compare(player.UTR, UTR) == 0 && Objects.equals(name, player.name) && Objects.equals(gender, player.gender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, gender, UTR);
    }

    @Override
    public String toString() {
        return name + " " + gender + "(" +
                UTR + ")";
    }
}
