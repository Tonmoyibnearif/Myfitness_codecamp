package com.uniks.myfit;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.MapFragment;
import com.uniks.myfit.controller.DetailViewMapsController;
import com.uniks.myfit.database.AppDatabase;
import com.uniks.myfit.model.LocationData;
import com.uniks.myfit.model.SportExercise;
import com.uniks.myfit.model.User;
import com.uniks.myfit.helper.FittnessDataHelper;
import com.uniks.myfit.helper.ShareDataHelper;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * the activity that is shown, when user clicked on done exercises to review his exercise. Implements listener for share button clicks.
 */
public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    AppDatabase db;
    User user;
    SportExercise exercise;
    List<LocationData> allLocation;


    DetailViewMapsController detailViewMapsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Model
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, MainActivity.DATABASE_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().build();

        user = db.userDao().getAll().get(0); // same call as in MainActivity -> for this project ok, because just one user

        int index = getIntent().getIntExtra("POSITION", 0);

        exercise = db.sportExerciseDao().getAllFromUser(user.getUid()).get(index); // get the clicked exercise of all user exercises

        allLocation = db.locationDataDao().getAllFromExercise(exercise.getId());

        // View
        // set title also choose layout based on exercise type from stored data
        switch (exercise.getMode()) {
            case 0:
                this.setTitle("Running");
                setContentView(R.layout.activity_detail_tracked);
                break;
            case 1:
                this.setTitle("Cycling");
                setContentView(R.layout.activity_detail_tracked);
                break;
            case 2:
                this.setTitle("Push Ups");
                setContentView(R.layout.activity_detail_repetitions);
                break;
            case 3:
                this.setTitle("Sit Ups");
                setContentView(R.layout.activity_detail_repetitions);
                break;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.share_button);
        fab.setOnClickListener(this);

        // Controller
        detailViewMapsController = new DetailViewMapsController(this, exercise, db);

        if (exercise.getMode() == 0 || exercise.getMode() == 1) {
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(detailViewMapsController);
        }

        setExerciseData();
    }

    /**
     * connects data with UI elements
     */
    private void setExerciseData() {
        TextView startTime = findViewById(R.id.exercise_started_time);
        TextView duration = findViewById(R.id.exercise_duration);
        TextView calories = findViewById(R.id.exercise_kcal);

        FittnessDataHelper fittnessDataHelper = new FittnessDataHelper(exercise, user);

        startTime.setText(formattedDate());
        duration.setText(exercise.getTripTime());
        calories.setText(String.valueOf(fittnessDataHelper.calculateCalories()));

        switch (exercise.getMode()) {
            case 0:
                // Running

                TextView runningDistance = findViewById(R.id.exercise_distance);
                TextView steps = findViewById(R.id.exercise_steps_count);
                TextView avgRunningSpeed = findViewById(R.id.exercise_avg_speed);

                runningDistance.setText(getResources().getString(R.string.detail_distance, exercise.getDistance()));
                steps.setText(getResources().getString(R.string.detail_steps, exercise.getAmountOfRepeats()));
                avgRunningSpeed.setText(getResources().getString(R.string.detail_avg_speed, fittnessDataHelper.getAvgSpeed()));

                break;
            case 1:
                // Cycling

                TextView cyclingDistance = findViewById(R.id.exercise_distance);
                TextView cyclingSpeed = findViewById(R.id.exercise_steps_count);
                TextView avgCyclingSpeed = findViewById(R.id.exercise_avg_speed);

                cyclingDistance.setText(getResources().getString(R.string.detail_distance, exercise.getDistance()));
                cyclingSpeed.setText(getResources().getString(R.string.detail_max_speed, exercise.getSpeed()));
                avgCyclingSpeed.setText(getResources().getString(R.string.detail_avg_speed, fittnessDataHelper.getAvgSpeed()));

                break;
            case 2:
                // Push Ups

                TextView pushupsRepetitions = findViewById(R.id.exercise_repetitions);

                pushupsRepetitions.setText(getResources().getString(R.string.detail_amount, exercise.getAmountOfRepeats(), "push"));

                break;
            case 3:
                // Sit Ups

                TextView situpsRepetitions = findViewById(R.id.exercise_repetitions);

                situpsRepetitions.setText(getResources().getString(R.string.detail_amount, exercise.getAmountOfRepeats(), "sit"));

                break;
        }
    }

    private String formattedDate() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.GERMANY);

        return sdf.format(exercise.getDate());
    }

    @Override
    public void onClick(View v) {

        if (exercise.getMode() == 0 || exercise.getMode() == 1) {
            detailViewMapsController.doMapScreenshot();
        } else {

            ShareDataHelper shareDataHelper = new ShareDataHelper(this, exercise, db);
            shareDataHelper.shareData();

        }

    }

}