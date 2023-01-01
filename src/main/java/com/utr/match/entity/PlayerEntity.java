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
}
