package com.uniks.myfit.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * the sport exercise. One user can have multiple sport exercises.
 */
@Entity(indices = {@Index(value = {"user_id"})})
public class SportExercise {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "user_id")
    private long userId;

    @ColumnInfo(name = "mode")
    private int mode; // Exercising Mode e.g. hiking, cycling, ...

    @ColumnInfo(name = "distance")
    private double distance; // the distance of the track

    @ColumnInfo(name = "speed")
    private double speed; // the measured speed of the track

    @ColumnInfo(name = "trip_time")
    private String tripTime; // the time spend on the track

    @ColumnInfo(name = "date_of_track")
    private Date date; // the day & time of the track

    @ColumnInfo(name = "amount_of_repeats")
    private int amountOfRepeats; // for steps, push-ups and sit-ups

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getTripTime() {
        return tripTime;
    }

    public void setTripTime(String tripTime) {
        this.tripTime = tripTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getAmountOfRepeats() {
        return amountOfRepeats;
    }

    public void setAmountOfRepeats(int amountOfRepeats) {
        this.amountOfRepeats = amountOfRepeats;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

}