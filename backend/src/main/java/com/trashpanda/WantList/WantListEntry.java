package com.trashpanda.WantList;

import com.trashpanda.Item;

public class WantListEntry {
    private String username;
    private Item item;
    private double qty;

    public WantListEntry(String username, Item item, double qty) {
        this.username = username;
        this.item = item;
        this.qty = qty;
    }

    // GETTERS

    public String getUsername() {
        return username;
    }

    public Item getItem() {
        return item;
    }

    public double getQty() {
        return qty;
    }

    // SETTERS

    public double modifyQty(double reduceBy) {
        qty = qty - reduceBy;
        return qty;
    }
}
