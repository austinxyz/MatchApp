package com.utr.model;

import java.util.ArrayList;
import java.util.List;

public class Club {
    String id;
    String name;
    String location;
    List<Event> events;

    public Club() {
        events = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Club{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", events=" + events +
                '}';
    }

    public List<Event> getEvents() {
        return events;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
