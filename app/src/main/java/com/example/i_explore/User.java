package com.example.i_explore;

public class User {
    private  String activityName;
    private  String location;
    private  String date;
    private  String time;
    private  String reporter;

    public User() {
    }

    public User(String activityName, String location, String date, String time, String reporter) {
        this.activityName = activityName;
        this.location = location;
        this.date = date;
        this.time = time;
        this.reporter = reporter;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }
}
