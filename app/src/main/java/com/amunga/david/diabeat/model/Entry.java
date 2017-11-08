package com.amunga.david.diabeat.model;

/**
 * Created by amush on 04-Nov-17.
 */

public class Entry {
    private String date,time,reminder,note,bloodSugar,activityDuration,hb;


    public Entry(String date, String time, String reminder, String note, String bloodSugar, String activityDuration, String hb) {
        this.date = date;
        this.time = time;
        this.reminder = reminder;
        this.note = note;
        this.bloodSugar = bloodSugar;
        this.activityDuration = activityDuration;
        this.hb = hb;
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

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getBloodSugar() {
        return bloodSugar;
    }

    public void setBloodSugar(String bloodSugar) {
        this.bloodSugar = bloodSugar;
    }

    public String getActivityDuration() {
        return activityDuration;
    }

    public void setActivityDuration(String activityDuration) {
        this.activityDuration = activityDuration;
    }

    public String getHb() {
        return hb;
    }

    public void setHb(String hb) {
        this.hb = hb;
    }
}
