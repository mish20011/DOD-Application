package com.catignascabela.dodapplication;

public class Violation {
    private String description;
    private String punishment;
    private long timestamp; // New field for storing timestamp

    // Default constructor required for Firebase Realtime Database
    public Violation() {
        // Default constructor for Firebase
    }

    // Constructor with description, punishment, and timestamp
    public Violation(String description, String punishment, long timestamp) {
        this.description = description;
        this.punishment = punishment;
        this.timestamp = timestamp; // Initialize timestamp
    }

    // Getter for description
    public String getDescription() {
        return description;
    }

    // Setter for description
    public void setDescription(String description) {
        this.description = description;
    }

    // Getter for punishment
    public String getPunishment() {
        return punishment;
    }

    // Setter for punishment
    public void setPunishment(String punishment) {
        this.punishment = punishment;
    }

    // Getter for timestamp
    public long getTimestamp() {
        return timestamp;
    }

    // Setter for timestamp
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
