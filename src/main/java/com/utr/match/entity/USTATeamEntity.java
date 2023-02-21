package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "usta_team")
public class USTATeamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "tr_link")
    private String tennisRecordLink;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usta_division_id")
    private USTADivision division;


    @JsonIgnore
    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final List<USTATeamMember> players;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id")
    private USTAFlight ustaFlight;

//    @ManyToMany(fetch = FetchType.EAGER, cascade = {
//            CascadeType.PERSIST,
//            CascadeType.MERGE
//    })
//    @JoinTable(name = "usta_team_player",
//            joinColumns = {@JoinColumn(name = "usta_team_id")},
//            inverseJoinColumns = {@JoinColumn(name = "player_id")})
//    @OrderBy(" gender DESC, dutr DESC ")
//    private final List<USTATeamMember> players;

    @ManyToOne
    @JoinColumn(name = "captain_id")
    private PlayerEntity captain;
    @Transient
    private String divisionName;

    @Transient
    private String captainName;

    @Transient
    private String areaCode;

    public USTATeamEntity(String name, USTADivision division) {
        this.name = name;
        this.division = division;
        this.players = new ArrayList<>();
    }

    public USTATeamEntity() {
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

    public List<USTATeamMember> getPlayers() {
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
        return this.tennisRecordLink;
    }

    public void setTennisRecordLink(String tennisRecordLink) {
        this.tennisRecordLink = tennisRecordLink;
    }

    public USTATeamMember getPlayer(String name) {
        for (USTATeamMember player : this.players) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

    public USTAFlight getUstaFlight() {
        return ustaFlight;
    }

    public void setUstaFlight(USTAFlight ustaFlight) {
        this.ustaFlight = ustaFlight;
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
