package com.utr.match.entity;

public class OrderByCriteria {

    String key;
    boolean asc;

    public String getKey() {
        return key;
    }

    public boolean isAsc() {
        return asc;
    }

    public OrderByCriteria(String key, boolean asc) {
        this.key = key;
        this.asc = asc;
    }
}
