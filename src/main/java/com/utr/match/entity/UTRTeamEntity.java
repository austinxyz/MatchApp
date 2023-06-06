package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "utr_team")
public class UTRTeamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "utr_id")
    private String utrTeamId;

    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final List<UTRTeamMember> players;

    @ManyToOne
    @JoinColumn(name = "captain_id")
    private PlayerEntity captain;

    public UTRTeamEntity(String name, String utrTeamId) {
        this.name = name;
        this.utrTeamId = utrTeamId;
        this.players = new ArrayList<>();
    }

    public UTRTeamEntity() {
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

    public List<UTRTeamMember> getPlayers() {
        players.sort((UTRTeamMember p1, UTRTeamMember p2) -> Double.compare(p2.getDUTR(), p1.getDUTR()));
        return players;
    }

    public UTRTeamMember getMember(String playerUTRId) {
        for (UTRTeamMember member: players) {
            if (member.getPlayer().getUtrId().equals(playerUTRId)) {
                return member;
            }
        }
        return null;
    }

    public void addPlayer(UTRTeamMember member) {
        players.add(member);
    }

    public PlayerEntity getCaptain() {
        return captain;
    }

    public void setCaptain(PlayerEntity captain) {
        this.captain = captain;
    }

    public UTRTeamMember getMember(String firstName, String lastName) {
        for (UTRTeamMember member: players) {
            if (member.getPlayer().getFirstName().trim().equalsIgnoreCase(firstName)
                && member.getPlayer().getLastName().trim().equalsIgnoreCase(lastName)) {
                return member;
            }
        }
        System.out.println("can not find this member:" + firstName + " " + lastName
            + " in team:" + this.getName());
        return null;
    }
}
