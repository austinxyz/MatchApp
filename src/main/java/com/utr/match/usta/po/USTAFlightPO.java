package com.utr.match.usta.po;

import com.fasterxml.jackson.annotation.JsonProperty;

public class USTAFlightPO {

    private String id;

    private String link;

    public USTAFlightPO() {
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
