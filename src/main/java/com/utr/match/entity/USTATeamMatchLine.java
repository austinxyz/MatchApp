package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "usta_team_match_line")
public class USTATeamMatchLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "type")
    private String type;  // S for single, D for double

    @Column(name = "name")
    private String name;  // D1, D2, D3 etc.

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "match_id")
    private USTATeamMatch match;


    @ManyToOne
    @JoinColumn(name = "player1_id")
    private PlayerEntity player1;


    @ManyToOne
    @JoinColumn(name = "player2_id")
    private PlayerEntity player2;

    public USTATeamMatchLine(PlayerEntity player1, PlayerEntity player2, String type, String name)
    {
        this.player1 = player1;
        this.player2 = player2;
        this.type = type;
        this.name = name;
    }

    public USTATeamMatchLine() {

    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public USTATeamMatch getMatch() {
        return match;
    }

    public void setMatch(USTATeamMatch match) {
        this.match = match;
    }

    public PlayerEntity getPlayer1() {
        return player1;
    }

    public void setPlayer1(PlayerEntity player1) {
        this.player1 = player1;
    }

    public PlayerEntity getPlayer2() {
        return player2;
    }

    public void setPlayer2(PlayerEntity player2) {
        this.player2 = player2;
    }

    public boolean isPair(PlayerEntity player1, PlayerEntity player2) {
        if (this.player1 == null || this.player2 == null) {
            return false;
        }
        return type.equals("D") && ((player1.getId() == this.player1.getId()
                && player2.getId() == this.player2.getId())
                || (player2.getId() == this.player1.getId()
                && player1.getId() == this.player2.getId()));
    }
    @Override
    public String toString() {
        return "USTATeamMatchLine{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", player1=" + player1.getName() +
                (player2==null?  "":"player2=" + player2.getName()) +
                '}';
    }
}
