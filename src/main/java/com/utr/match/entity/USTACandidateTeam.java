package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usta_candidate_team")
public class USTACandidateTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "alias")
    private String alias;

    @Column(name = "area")
    private String area;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usta_division_id")
    private USTADivision division;

    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final List<USTACandidate> candidates;

    @ManyToOne
    @JoinColumn(name = "captain_id")
    private PlayerEntity captain;
    @Transient
    private String divisionName;

    @Transient
    private String captainName;

    @Transient
    private String areaCode;

    public USTACandidateTeam(String name, USTADivision division) {
        this.name = name;
        this.division = division;
        this.candidates = new ArrayList<>();
    }

    public USTACandidateTeam() {
        this.candidates = new ArrayList<>();
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

    public USTADivision getDivision() {
        return division;
    }

    public void setDivision(USTADivision division) {
        this.division = division;
    }

    public List<USTACandidate> getCandidates() {
        candidates.sort(USTACandidate::compareByGenderAndRating);
        return candidates;
    }

    public void addPlayer(USTACandidate member) {
        candidates.add(member);
    }

    public PlayerEntity getCaptain() {
        return captain;
    }

    public void setCaptain(PlayerEntity captain) {
        this.captain = captain;
    }

    @JsonProperty
    public String getDivisionName() {
        if (this.division != null) {
            this.divisionName = division.getName();
        }
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    public String getCaptainName() {
        if (this.captain != null) {
            this.captainName = captain.getName();
        }
        return captainName;
    }

    public void setCaptainName(String captainName) {
        this.captainName = captainName;
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


    public USTACandidate getPlayer(String name) {
        for (USTACandidate player : this.candidates) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "USTATeam{" +
                "name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", divisionName='" + divisionName + '\'' +
                ", captainName='" + captainName + '\'' +
                ", area='" + area + '\'' +
                '}';
    }

    public USTACandidate addCandidate(PlayerEntity player) {
        for (USTACandidate candidate: this.candidates) {
            if (candidate.getId() == player.getId()) {
                return candidate;
            }
        }
        USTACandidate newCandidate = new USTACandidate(player, this);
        this.addPlayer(newCandidate);
        return newCandidate;
    }

    public int getRequiredTotalMaleMatchesNo() {
        return getRequiredTotalMatchesNo("M");
    }

    private int getRequiredTotalMatchesNo(String gender) {
        int result = 0;
        for (USTACandidate candidate: this.candidates) {
            if (candidate.getGender().equals(gender)) {
                result+=candidate.getRequiredMatchNo();
            }
        }
        return result;
    }

    public int getRequiredTotalFemaleMatchesNo() {
        return getRequiredTotalMatchesNo("F");
    }
}
