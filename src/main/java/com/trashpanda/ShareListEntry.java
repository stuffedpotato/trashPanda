package com.trashpanda;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

public class ShareListEntry {
    String username;
    private Item item;
    private double qty;
    private Date expirationDate;

    public ShareListEntry(String username, Item item, double qty, Date expirationDate) {
        this.username = username;
        this.item = item;
        this.qty = qty;
        this.expirationDate = expirationDate;
    }

    // Getters
    public String getUsername() {
        return username;
    }
    public Item getItem() {
        return item;
    }

    public double getQty() {
        return qty;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    // SETTERS

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public double modifyQty(double reduceBy) {
        qty = qty - reduceBy;
        return qty;
    }
}

