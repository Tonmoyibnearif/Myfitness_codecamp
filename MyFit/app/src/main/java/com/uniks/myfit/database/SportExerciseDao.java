package com.uniks.myfit.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.uniks.myfit.model.SportExercise;

import java.util.List;

@Dao
public interface SportExerciseDao {

    @Query("SELECT * FROM sportexercise")
    List<SportExercise> getAll();

    @Query("SELECT * FROM sportexercise WHERE user_id = :userId")
    List<SportExercise> getAllFromUser(long userId);

    @Query("SELECT * FROM sportexercise WHERE id IN (:ids)")
    List<SportExercise> loadAllByIds(long[] ids);

    @Query("SELECT * FROM sportexercise WHERE id = :exerciseId")
    List<SportExercise> getExerciseById(long exerciseId);

    @Update
    void updateSportExercise(SportExercise sportExercise);

    @Update
    void updateSportExercises(SportExercise... sportExercises);

    @Insert
    long[] insertAll(SportExercise... sportExercises);

    @Insert
    long insert(SportExercise sportExercise);

    @Delete
    void deleteExercise(SportExercise sportExercise);

    @Query("DELETE FROM sportexercise WHERE id IS NOT NULL AND id = :id")
    void deleteExercise(long id);
}