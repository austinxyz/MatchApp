package com.utr.match.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="division")
public class DivisionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="div_id")
    private String divisionId;

    @Column(name="name")
    private String name;

    @Column(name="formal_name_chinese")
    private String chineseName;

    @Column(name="formal_name_english")
    private String englishName;

    public List<UTRTeamCandidate> getCandidates() {
        candidates.sort((UTRTeamCandidate p1, UTRTeamCandidate p2) -> Double.compare(p2.getUTR(), p1.getUTR()));
        return candidates;
    }

    public void setCandidates(List<UTRTeamCandidate> candidates) {
        this.candidates = candidates;
    }

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "division_players",
        joinColumns = {@JoinColumn(name="division_id")},
        inverseJoinColumns = {@JoinColumn(name="player_id")})
    private List<PlayerEntity> players;

/*
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "division_candidates",
            joinColumns = {@JoinColumn(name="division_id")},
            inverseJoinColumns = {@JoinColumn(name="candidate_player_id")})
    @OrderBy(" gender DESC, dutr DESC ")
    private List<PlayerEntity> candidates;
*/
    @JsonIgnore
    @OneToMany(mappedBy = "division", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<UTRTeamCandidate> candidates;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private EventEntity event;

    public DivisionEntity(String name, EventEntity event) {
        this.name = name;
        this.event = event;
        this.players = new ArrayList<>();
        this.candidates = new ArrayList<>();
    }

    public DivisionEntity() {
        this.players = new ArrayList<>();
        this.candidates = new ArrayList<>();
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

    public String getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    public void removePlayer(Long playerId) {
        PlayerEntity target = null;
        for (PlayerEntity player: this.players) {
            if (player.getId() == playerId)  {
                target = player;
                break;
            }
        }

        if (target!=null) {
            this.players.remove(target);
        }
    }

    public void addCandidate(PlayerEntity player) {
        for (UTRTeamCandidate candidate: this.candidates) {
            if (candidate.getUtrId().equals(player.getUtrId())) {
                return;
            }
        }
        UTRTeamCandidate candidate = new UTRTeamCandidate(player, this);
        this.candidates.add(candidate);
    }
}
