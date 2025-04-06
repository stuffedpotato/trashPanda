package com.trashpanda;

public class MatchResult {
    private User user;
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
