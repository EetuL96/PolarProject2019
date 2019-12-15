package com.example.polarproject;

import java.io.Serializable;

public class User implements Serializable {

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    private String ID;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    private String firstName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    private String lastName;

    private String email;
    public void setEmail(String email) {this.email = email;}
    public String getEmail (){return this.email;}

    private boolean isFollowed;
    public void setIsFollowed(boolean isFollowed)
    {
        this.isFollowed = isFollowed;
    }

    public boolean getIsFollowed()
    {
        return this.isFollowed;
    }

    private double kilometersRun;
    private long totalTime;
    private double averageSpeed;
    private long runsCompleted;
    private double longestRun;
    private double averageDistance;

    public double getAverageDistance() {
        return averageDistance;
    }

    public void setAverageDistance(double averageDistance) {
        this.averageDistance = averageDistance;
    }

    public double getKilometersRun() {
        return kilometersRun;
    }

    public void setKilometersRun(double kilometersRun) {
        this.kilometersRun = kilometersRun;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public long getRunsCompleted() {
        return runsCompleted;
    }

    public void setRunsCompleted(long runsCompleted) {
        this.runsCompleted = runsCompleted;
    }

    public double getLongestRun() {
        return longestRun;
    }

    public void setLongestRun(double longestRun) {
        this.longestRun = longestRun;
    }
}
