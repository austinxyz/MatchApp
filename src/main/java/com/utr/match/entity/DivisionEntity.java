package com.utr.match.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="division")
public class DivisionEntity {
    @Id
    private long id;

    @Column(name="name")
    private String name;

    @Column(name="formal_name_chinese")
    private String chineseName;

    @Column(name="formal_name_english")
    private String englishName;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "division_players",
        joinColumns = {@JoinColumn(name="division_id")},
        inverseJoinColumns = {@JoinColumn(name="player_id")})
    private List<PlayerEntity> players;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventEntity event;

    public DivisionEntity(String name, EventEntity event) {
        this.name = name;
        this.event = event;
        this.players = new ArrayList<>();
    }

    public DivisionEntity() {
        this.players = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getChineseName() {
        return chineseName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public List<PlayerEntity> getPlayers() {
        return players;
    }

    public EventEntity getEvent() {
        return event;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }
}
