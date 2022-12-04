package com.utr.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {

    Map<String, Division> divisions;
    String name;
    String id;

    public Event(String id) {
        this.id = id;
        this.divisions = new HashMap<String, Division>();
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
