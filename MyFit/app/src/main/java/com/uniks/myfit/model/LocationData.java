package com.uniks.myfit.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

/**
 * the location data, which stores Longitude and Latitude and also generates LatLng Objects.
 */
@Entity(foreignKeys = @ForeignKey(entity = SportExercise.class, parentColumns = "id", childColumns = "exercise_id", onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE), indices = {@Index(value = {"exercise_id"})})
public class LocationData {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "exercise_id")
    private long exerciseId;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    public LocationData(double latitude, double longitude) {

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }
}
