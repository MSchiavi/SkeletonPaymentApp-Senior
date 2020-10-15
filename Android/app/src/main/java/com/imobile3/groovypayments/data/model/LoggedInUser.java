package com.imobile3.groovypayments.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String displayName;
    private String userId;
    private String email;
    private double hours;

    public LoggedInUser(String userId, String displayName, String email, double hours) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.hours = hours;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() { return email; }

    public double getHours() { return hours; }
}
