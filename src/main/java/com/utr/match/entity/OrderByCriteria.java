package com.utr.match.entity;

import java.util.ArrayList;
import java.util.List;

public class OrderByCriteria {

    String key;
    boolean asc;

    List<SortOrder> orders = new ArrayList<>();

    public OrderByCriteria(String key, boolean asc) {
        SortOrder order = new SortOrder(key, asc);
        orders.add(order);
    }

    public void addOrder(String key, boolean asc) {
        SortOrder order = new SortOrder(key, asc);
        orders.add(order);
    }

    public List<SortOrder> getOrders() {
        return orders;
    }

    protected class SortOrder {
        String key;
        boolean asc;

        public SortOrder(String key, boolean asc) {
            this.key = key;
            this.asc = asc;
        }

        public String getKey() {
            return key;
        }

        public boolean isAsc() {
            return asc;
        }
    }
}
