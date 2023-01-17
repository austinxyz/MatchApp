package com.utr.match.entity;

import javax.persistence.*;

@Entity
@Table(name="player_event_utr")
public class EventUTR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="event_id")
    private String event_id;

    @Column(name="event_utr")
    private double utr;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private PlayerEntity player;

    public EventUTR() {
    }

    public EventUTR(String event_id, double utr, PlayerEntity player) {
        this.event_id = event_id;
        this.utr = utr;
        this.player = player;
    }

    public String getEvent_id() {
        return event_id;
    }

    public double getUtr() {
        return utr;
    }

    public PlayerEntity getPlayer() {
        return player;
    }
}
