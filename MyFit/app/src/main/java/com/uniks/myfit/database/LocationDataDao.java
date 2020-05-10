package com.uniks.myfit.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.uniks.myfit.model.LocationData;

import java.util.List;

@Dao
public interface LocationDataDao {

    @Query("SELECT * FROM locationdata")
    List<LocationData> getAll();

    @Query("SELECT * FROM locationdata WHERE exercise_id = :exerciseId")
    List<LocationData> getAllFromExercise(long exerciseId);

    @Update
    void updateLocationDatas(LocationData... locationData);

    @Insert
    void insertAll(LocationData... locationData);

    @Insert
    void insert(LocationData locationData);

    @Delete
    void delete(LocationData locationData);

}
