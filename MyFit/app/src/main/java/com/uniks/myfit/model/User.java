package com.uniks.myfit.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * the user.
 */
@Entity
public class User {

    @PrimaryKey(autoGenerate = true)
    private long uid;

    @ColumnInfo(name = "weight")
    private int weight; // user weight in kg

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
