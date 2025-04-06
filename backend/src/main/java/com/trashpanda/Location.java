package com.trashpanda;

public class Location {
    private double latitude;
    private double longitude;
    
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // GETTERS

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    /*
     * To calculate the distance between two locations.
     */

    public double distanceTo(Location other) {
        final int RADIUS = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(other.latitude - this.latitude);
        double lonDistance = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double angularDist = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIUS * angularDist;
    }
}
