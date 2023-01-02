package com.utr.match.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="player")
public class PlayerEntity {
    @Id
    private long id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="birth_year")
    private int birthYear;

    @Column(name="birth_month")
    private int birthMonth;

    @Column(name="utr_id")
    private String utrId;

    @Column(name="usta_id")
    private String ustaId;

    @Column(name="usta_rating")
    private String ustaRating;

    @Column(name="registered_area")
    private String area;

    @Column(name="gender")
    private String gender;

    @Column(name="lefty")
    private boolean lefty;

    @Column(name="summary")
    private String summary;

    @Column(name="memo")
    private String memo;

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public int getBirthMonth() {
        return birthMonth;
    }

    public String getUtrId() {
        return utrId;
    }

    public String getUstaId() {
        return ustaId;
    }

    public String getUstaRating() {
        return ustaRating;
    }

    public String getArea() {
        return area;
    }

    public String getGender() {
        return gender;
    }

    public boolean isLefty() {
        return lefty;
    }

    public String getSummary() {
        return summary;
    }

    public String getMemo() {
        return memo;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public void setBirthMonth(int birthMonth) {
        this.birthMonth = birthMonth;
    }

    public void setUtrId(String utrId) {
        this.utrId = utrId;
    }

    public void setUstaId(String ustaId) {
        this.ustaId = ustaId;
    }

    public void setUstaRating(String ustaRating) {
        this.ustaRating = ustaRating;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setLefty(boolean lefty) {
        this.lefty = lefty;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
