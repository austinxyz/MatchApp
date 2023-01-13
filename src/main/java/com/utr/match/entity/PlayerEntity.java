package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="player")
public class PlayerEntity {
    @Id
    private long id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="full_name")
    private String name;

    @Column(name="birth_year")
    private Integer birthYear;

    @Column(name="birth_month")
    private Integer birthMonth;

    @Column(name="utr_id")
    private String utrId;

    @Column(name="usta_id")
    private String ustaId;

    @Column(name="usta_rating")
    private String ustaRating;

    @Column(name="usta_noncal_link")
    private String noncalLink;

    @Column(name="usta_tennisrecord_link")
    private String tennisRecordLink;

    @Column(name="registered_area")
    private String area;

    @Column(name="gender")
    private String gender;

    @Column(name="lefty")
    private Boolean lefty;

    @Column(name="summary")
    private String summary;

    @Column(name="memo")
    private String memo;

    @JsonIgnore
    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<EventUTR> utrs;

    @Transient
    double dUTR;
    @Transient
    double sUTR;
    @Transient
    String dUTRStatus;
    @Transient
    String sUTRStatus;

    @Transient
    String dynamicRating;

    @Transient
    float successRate;

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

    public String getDynamicRating() {
        return dynamicRating;
    }

    public void setDynamicRating(String dynamicRating) {
        this.dynamicRating = dynamicRating;
    }

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
        return birthYear==null? 0:birthYear.intValue();
    }

    public int getBirthMonth() {
        return birthMonth==null? 0:birthMonth.intValue();
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
        return lefty==null? false:lefty.booleanValue();
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

    public PlayerEntity() {
        this.utrs = new ArrayList<>();
    }

    public List<EventUTR> getUtrs() {
        return utrs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public String getTennisLinkURL() {
        return "https://tennislink.usta.com/Leagues/Main/StatsAndStandings.aspx?t=R-17&search=" + this.utrId;
    }

    public String getNoncalLink() {
        return noncalLink;
    }

    public void setNoncalLink(String noncalLink) {
        this.noncalLink = noncalLink;
    }

    public String getTennisRecordLink() {
        if (tennisRecordLink == null) {
            tennisRecordLink = "https://www.tennisrecord.com/adult/profile.aspx?playername="
                    + this.firstName
                    + "%20" + this.lastName;
        }
        return tennisRecordLink;
    }

    public void setTennisRecordLink(String tennisRecordLink) {
        this.tennisRecordLink = tennisRecordLink;
    }

    public float getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(float successRate) {
        this.successRate = successRate;
    }

    @Override
    public String toString() {
        return "PlayerEntity{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", name='" + name + '\'' +
                ", utrId='" + utrId + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }

}
