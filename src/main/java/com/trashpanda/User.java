package com.trashpanda;

import java.util.*;

public class User {
    private String firstName;
    private String lastName;
    private String contact;
    private String userName;
    private String hashedPw;
    private Location location;
    private double radius;

    private List<WantListEntry> wantList;
    private List<ShareListEntry> shareList;

    public User(String firstName, String lastName, String contact, String userName, String plainPw, Location location, double radius) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.contact = contact;
        this.userName = userName;
        this.hashedPw = PasswordUtils.hashPassword(plainPw);
        this.location = location;
        this.radius = radius;
        this.wantList = new ArrayList<>();
        this.shareList = new ArrayList<>();
    }

    // GETTERS

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getContact() {
        return contact;
    }

    public String getUserName() {
        return userName;
    }

    public String getHashedPw() {
        return hashedPw;
    }

    public Location getLocation() {
        return location;
    }

    public double getRadius() {
        return radius;
    }

    public List<WantListEntry> getWantList() {
        return Collections.unmodifiableList(wantList);
    }

    public List<ShareListEntry> getShareList() {
        return Collections.unmodifiableList(shareList);
    }

    // SETTERS

    public String setFirstName(String firstName) {
        this.firstName = firstName;
        return firstName;
    }

    public String setLastName(String lastName) {
        this.lastName = lastName;
        return lastName;
    }

    public double setRadius(double radius) {
        this.radius = radius;
        return radius;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void insertWantListEntry(WantListEntry wantListEntry) {
        wantList.add(wantListEntry);
    }

    public void insertShareListEntry(ShareListEntry shareListEntry) {
        shareList.add(shareListEntry);
    }

    public boolean removeWantListEntry(WantListEntry entry) {
        return wantList.remove(entry);
    }
    
    public boolean removeShareListEntry(ShareListEntry entry) {
        return shareList.remove(entry);
    }    
}   
