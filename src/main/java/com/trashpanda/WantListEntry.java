package com.trashpanda;

public class WantListEntry {
    private Item item;
    private double qty;

    public WantListEntry(Item item, double qty) {
        this.item = item;
        this.qty = qty;
    }

    // Getters
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
