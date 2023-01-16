package com.utr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;
import java.util.Objects;

public class Player {

    String name;
    String gender;
    float UTR;
    String id;
    double dUTR;

    double sUTR;
    String dUTRStatus;
    String sUTRStatus;

    String firstName;
    String lastName;

    String location;

    String dynamicRating;

    float successRate;

    float wholeSuccessRate;

    Timestamp utrFetchedTime;

    public Player(String firstName, String lastName, String gender, String UTR) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = lastName + " " + firstName;
        this.gender = gender;
        if (UTR.indexOf("x") >0) {
            this.UTR = Float.parseFloat(UTR.substring(0, UTR.indexOf('.')));
        } else {
            this.UTR = Float.parseFloat(UTR);
        }
    }

    public Player() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public float getUTR() {
        return UTR;
    }

    public void setUTR(String UTR) {
        this.UTR = Float.parseFloat(UTR);
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDynamicRating() {
        return dynamicRating;
    }

    public void setDynamicRating(String dynamicRating) {
        this.dynamicRating = dynamicRating;
    }

    public float getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(float successRate) {
        this.successRate = successRate;
    }

    public float getWholeSuccessRate() {
        return wholeSuccessRate;
    }

    public void setWholeSuccessRate(float wholeSuccessRate) {
        this.wholeSuccessRate = wholeSuccessRate;
    }

    public Timestamp getUtrFetchedTime() {
        return utrFetchedTime;
    }

    public void setUtrFetchedTime(Timestamp utrFetchedTime) {
        this.utrFetchedTime = utrFetchedTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + " " + gender + "(" +
                UTR + ")";
    }
}
