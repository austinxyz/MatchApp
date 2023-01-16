package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usta_team")
public class USTATeam {
    @Id
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "alias")
    private String alias;

    @Column(name = "area")
    private String area;

    @Column(name = "flight")
    private String flight;

    @Column(name = "link")
    private String link;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "usta_division_id")
    private USTADivision division;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usta_team_player",
            joinColumns = {@JoinColumn(name = "usta_team_id")},
            inverseJoinColumns = {@JoinColumn(name = "player_id")})
    @OrderBy(" dutr DESC ")
    private final List<PlayerEntity> players;

    @ManyToOne
    @JoinColumn(name = "captain_id")
    private PlayerEntity captain;
    @Transient
    private String divisionName;

    @Transient
    private String captainName;

    @Transient
    private String areaCode;

    public USTATeam(String name, USTADivision division) {
        this.name = name;
        this.division = division;
        this.players = new ArrayList<>();
    }

    public USTATeam() {
        this.players = new ArrayList<>();
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

    public String getFlight() {
        return flight;
    }

    public void setFlight(String flight) {
        this.flight = flight;
    }

    public USTADivision getDivision() {
        return division;
    }

    public void setDivision(USTADivision division) {
        this.division = division;
    }

    public List<PlayerEntity> getPlayers() {
        return players;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAreaCode() {

        if (this.areaCode != null && !this.areaCode.equals("")) {
            return areaCode;
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

    @JsonProperty
    public String getTennisRecordLink() {
        return "https://www.tennisrecord.com/adult/teamprofile.aspx?teamname=" + this.name + "&year=" + this.division.getLeague().getYear();
    }

    public PlayerEntity getPlayer(String name) {
        for (PlayerEntity player : this.players) {
            if (player.getName().equals(name)) {
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
                ", link='" + link + '\'' +
                ", divisionName='" + divisionName + '\'' +
                ", captainName='" + captainName + '\'' +
                ", area='" + area + '\'' +
                ", flight='" + flight + '\'' +
                '}';
    }
}
