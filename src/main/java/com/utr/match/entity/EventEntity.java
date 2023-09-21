package com.utr.match.entity;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="event")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="event_name")
    private String name;

    @Column(name="alias")
    private String alias;

    @Column(name="event_year")
    private String year;

    @Column(name="event_type")
    private String type;

    @Column(name="event_id")
    private String eventId;

    @Column(name="start_date")
    private Date startDate;

    @Column(name="status")
    private String status;  //Register, Ongoing, Completed

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<DivisionEntity> divisions;

    public EventEntity(String name, String type, String eventId) {
        this.name = name;
        this.type = type;
        this.eventId = eventId;
    }

    public EventEntity() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getEventId() {
        return eventId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getStatus() {
        return status;
    }

    public List<DivisionEntity> getDivisions() {
        return divisions;
    }

    public DivisionEntity getDivision(String divId) {
        for (DivisionEntity div: this.divisions) {
            if (div.getDivisionId().equals(divId)) {
                return div;
            }
        }
        return null;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
