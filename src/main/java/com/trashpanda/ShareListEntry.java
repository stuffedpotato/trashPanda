package com.trashpanda;
import java.time.LocalDate;
import java.util.Optional;

public class ShareListEntry {
    private Item item;
    private double qty;
    private Optional<LocalDate> expirationDate;

    public ShareListEntry(Item item, double qty, Optional<LocalDate> expirationDate) {
        this.item = item;
        this.qty = qty;
        this.expirationDate = expirationDate;
    }

    // Getters
    public Item getItem() {
        return item;
    }

    public double getQty() {
        return qty;
    }

    public Optional<LocalDate> getExpirationDate() {
        return expirationDate;
    }

    // SETTERS

    public void setExpirationDate(Optional<LocalDate> expirationDate) {
        this.expirationDate = expirationDate;
    }

    public double modifyQty(double reduceBy) {
        qty = qty - reduceBy;
        return qty;
    }
}

