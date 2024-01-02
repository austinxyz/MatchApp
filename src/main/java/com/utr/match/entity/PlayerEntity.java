package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.utr.model.Player;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Entity
@Table(name="player")
public class PlayerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name="registered_bay_area")
    private Boolean registeredBayArea;

    @Column(name="gender")
    private String gender;

    @Column(name="lefty")
    private Boolean lefty;

    @Column(name="summary")
    private String summary;

    @Column(name="memo")
    private String memo;

    @Column(name="usta_norcal_id")
    private String ustaNorcalId;

    @JsonIgnore
    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<EventUTR> utrs;

    @JsonIgnore
    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY)
    private Set<USTATeamMember> members;

    @Column(name="dutr")
    Double dUTR;
    @Column(name="sutr")
    Double sUTR;
    @Column(name="dutr_status")
    String dUTRStatus;
    @Column(name="sutr_status")
    String sUTRStatus;

    @Column(name="dynamic_rating")
    Double dynamicRating;

    @Column(name="utr_win_ratio_latest")
    Float successRate;

    @Column(name="utr_win_ratio")
    Float wholeSuccessRate;

    @Column(name="utr_fetched_time")
    Timestamp utrFetchedTime;

    @Column(name="dr_fetched_time")
    Timestamp drFetchedTime;

    @Column(name="age_range")
    String ageRange;

    public double getDUTR() {
        return dUTR==null? 0.0d: dUTR.doubleValue();
    }

    public void setDUTR(double dUTR) {
        this.dUTR = dUTR;
    }

    public double getSUTR() {
        return sUTR==null? 0.0d: sUTR.doubleValue();
    }

    public void setSUTR(double sUTR) {
        this.sUTR = sUTR;
    }

    public String getDUTRStatus() {
        return dUTRStatus;
    }

    public void setDUTRStatus(String dUTRStatus) {
        this.dUTRStatus = dUTRStatus;
    }

    public String getSUTRStatus() {
        return sUTRStatus;
    }

    public void setSUTRStatus(String sUTRStatus) {
        this.sUTRStatus = sUTRStatus;
    }

    public double getDynamicRating() {
        return dynamicRating==null? 0.0d: dynamicRating.doubleValue();
    }

    public void setDynamicRating(Double dynamicRating) {
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

    public boolean isRegisteredBayArea() {
        return registeredBayArea == null? false: registeredBayArea.booleanValue();
    }

    public void setRegisteredBayArea(boolean registeredBayArea) {
        this.registeredBayArea = registeredBayArea;
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
        this.utrs = new HashSet<>();
    }

    public Set<EventUTR> getUtrs() {
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
        return "https://tennislink.usta.com/Leagues/Main/StatsAndStandings.aspx?t=R-17&search=" + this.ustaId;
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
        return successRate == null? 0.0f: successRate.floatValue();
    }

    public void setSuccessRate(float successRate) {
        this.successRate = successRate;
    }

    public float getWholeSuccessRate() {
        return wholeSuccessRate == null? 0.0f:wholeSuccessRate.floatValue();
    }

    public void setWholeSuccessRate(float wholeSuccessRate) {
        this.wholeSuccessRate = wholeSuccessRate;
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

    public String getPlayerInfo(boolean isSingle) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName()).append("(").append(this.getGender()).append(") ")
                .append(this.getUstaRating()).append(" - ").append(isSingle? this.getSUTR() + "(S)": this.getDUTR() + "(D)")
                .append(" / ").append(this.getDynamicRating()).append("(DR)");
        return sb.toString();
    }

    public Timestamp getUtrFetchedTime() {
        return utrFetchedTime;
    }

    public void setUtrFetchedTime(Timestamp utrFetchedTime) {
        this.utrFetchedTime = utrFetchedTime;
    }

    public Timestamp getDrFetchedTime() {
        return drFetchedTime;
    }

    public void setDrFetchedTime(Timestamp drFetchedTime) {
        this.drFetchedTime = drFetchedTime;
    }

    public String getUstaNorcalId() {
        return ustaNorcalId;
    }

    public void setUstaNorcalId(String ustaNorcalId) {
        this.ustaNorcalId = ustaNorcalId;
    }

    @JsonIgnore
    public Set<USTATeamMember> getTeamMembers() {
        return this.members;
    }

    public void setUtrs(Set<EventUTR> utrs) {
        this.utrs = utrs;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    @JsonProperty
    public boolean isRefreshedUTR() {

        if (this.utrFetchedTime == null) {
            return false;
        }

        Timestamp now=new Timestamp(System.currentTimeMillis());
        LocalDate nowDate = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate fetchedDate = this.getUtrFetchedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ChronoUnit.DAYS.between(fetchedDate, nowDate) == 0;

    }

    @JsonProperty
    public boolean isUTRRequriedRefresh() {

        if (this.utrFetchedTime == null) {
            return true;
        }

        Timestamp now=new Timestamp(System.currentTimeMillis());
        LocalDate nowDate = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate fetchedDate = this.getUtrFetchedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ChronoUnit.DAYS.between(fetchedDate, nowDate) >= 60;

    }
}
