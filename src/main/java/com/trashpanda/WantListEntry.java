package com.trashpanda;

public class WantListEntry {
    private String userName;
    private Item item;
    private double qty;

    public WantListEntry(String userName, Item item, double qty) {
        this.userName = userName;
        this.item = item;
        this.qty = qty;
    }

    // GETTERS

    public String getUsername() {
        return userName;
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
