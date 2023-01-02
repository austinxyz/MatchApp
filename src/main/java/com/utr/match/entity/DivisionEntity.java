package com.utr.match.entity;

import javax.persistence.*;
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

    @OneToMany
    @JoinTable(name = "division_players",
        joinColumns = {@JoinColumn(name="division_id")},
        inverseJoinColumns = {@JoinColumn(name="player_id")})
    private List<PlayerEntity> players;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private EventEntity event;

    public DivisionEntity(String name, EventEntity event) {
        this.name = name;
        this.event = event;
    }

    public DivisionEntity() {
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
}
