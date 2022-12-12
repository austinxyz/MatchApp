package com.utr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {

    Map<String, Division> divisions;
    String name;
    String id;
    @JsonIgnore
    LocalDateTime startDate;
    String duration;

    public Event(String id) {
        this.id = id;
        this.divisions = new HashMap<String, Division>();
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @JsonProperty
    public String getYear() {
        return startDate==null? "":String.valueOf(startDate.getYear());
    }

    public List<Division> getDivisions() {
        return new ArrayList<>(divisions.values());
    }

    public void addDivision(Division division) {
        divisions.put(division.getId(), division);
    }

    public Division getDivision(String divisionId) {
        return divisions.get(divisionId);
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public Division getDivisionByName(String divisionName) {
        for (Division division : this.divisions.values()) {
            if (division.getName().equals(divisionName)) {
                return division;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
