package com.trashpanda;

import com.google.gson.annotations.Expose;

public class MatchResult {
    @Expose
    private User user;
    
    @Expose
    private double distance;

    public MatchResult(User user, double distance) {
        this.user = user;
        this.distance = distance;
    }

    public User getUser() {
        return user;
    }

    public double getDistance() {
        return distance;
    }
}