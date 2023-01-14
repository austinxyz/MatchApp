package com.utr.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class PlayerEvent {
    String id;
    String name;


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<MatchResult> getResults() {
        return results;
    }

    public PlayerEvent(String id, String name) {
        this.id = id;
        this.name = name;
        results = new ArrayList<>();
    }

    List<MatchResult> results;

    @JsonProperty
    public boolean isUsta() {
        return name.startsWith("USTA");
    }

}
