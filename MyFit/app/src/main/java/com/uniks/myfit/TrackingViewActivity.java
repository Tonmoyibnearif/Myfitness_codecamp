package com.uniks.myfit;

import android.arch.persistence.room.Room;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.uniks.myfit.controller.MapsController;
import com.uniks.myfit.database.AppDatabase;
import com.uniks.myfit.model.LocationData;
import com.uniks.myfit.model.SportExercise;
import com.uniks.myfit.sensors.PushupService;
import com.uniks.myfit.sensors.SitupService;
import com.uniks.myfit.sensors.StepCounterService;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * the activity that shows the current tracked data according exercise mode, incl. map for running and cycling
 */
public class TrackingViewActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_FINE_LOCATION = 351;
    private SitupService sitUpsCtrl;
    private StepCounterService stepCounterService;
    private MapsController mapsController;
    private PushupService pushupService;

    private int exerciseMode;
    private boolean activeProcessing;
    private Date startExercisingTime;
    private String customTitle = "Exercise";

    public AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // model
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, MainActivity.DATABASE_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().build();
        activeProcessing = true;
        startExercisingTime = Calendar.getInstance().getTime();
        exerciseMode = getIntent().getIntExtra("EXERCISE", 0);

        // controller
        sitUpsCtrl = new SitupService(this);
        stepCounterService = new StepCounterService(this);
        pushupService = new PushupService(this);

        // view
        switch (exerciseMode) {
            case 0:
                customTitle = getString(R.string.running);
                break;
            case 1:
                customTitle = getString(R.string.cycling);
                break;
            case 2:
                customTitle = getString(R.string.pushups);
                break;
            case 3:
                customTitle = getString(R.string.situps);
                break;
        }

        // set title based on the exercise type
        this.setTitle(customTitle);

        setContentView(R.layout.activity_tracking_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener(this);

        // start sensors
        startSensors();

        // start the state machine
        startProcessingData();
    }

    /**
     * initiates the sensor services
     */
    private void startSensors() {
        switch (exerciseMode) {
            case 0: // running

                stepCounterService.onStart();
                includeMap();

                // set headlines
                // distance
                TextView runningDistanceTitleUI = findViewById(R.id.title_1);
                runningDistanceTitleUI.setText(getResources().getString(R.string.distanceHeadline));
                // steps
                TextView runningStepCounterTitleUI = findViewById(R.id.title_2);
                runningStepCounterTitleUI.setText(getResources().getString(R.string.stepsHeadline));

                break;
            case 1: // cycling

                includeMap();

                // set headlines
                // distance
                TextView cyclingDistanceTitleUI = findViewById(R.id.title_1);
                cyclingDistanceTitleUI.setText(getResources().getString(R.string.distanceHeadline));
                // speed
                TextView cyclingSpeedTitleUI = findViewById(R.id.title_2);
                cyclingSpeedTitleUI.setText(getResources().getString(R.string.currentSpeedHeadline));

                break;
            case 2: // pushups

                pushupService.initialize();

                // set headlines
                // count
                TextView pushupCountTitleUI = findViewById(R.id.title_1);
                pushupCountTitleUI.setText(getResources().getString(R.string.countHeadline));

                break;
            case 3: // situps

                sitUpsCtrl.init();

                // set headlines
                // count
                TextView situpCountTitleUI = findViewById(R.id.title_1);
                situpCountTitleUI.setText(getResources().getString(R.string.countHeadline));

                break;
        }

        // time
        TextView durationTitleUI = findViewById(R.id.title_3);
        durationTitleUI.setText(getResources().getString(R.string.timeHeadline));
    }

    /**
     * starts processor to show actual calculated data
     */
    private void startProcessingData() {
        // start processing-Thread who also updates UI-elements
        new Thread(new Runnable() {
            public void run() {

                while (activeProcessing) {

                    processSensorData();
                }
            }
        }).start();

    }

    /**
     * gets actual data from services and updates UI
     */
    private void processSensorData() {
        final String duration = getFormattedCurrentDuration();
        switch (exerciseMode) {
            case 0: // running
                final int stepsCounted = stepCounterService.getActualCount();

                // set view - show distance and steps
                // distance
                final TextView runningDistanceValueUI = findViewById(R.id.value_1);
                // call this because of thread
                runningDistanceValueUI.post(new Runnable() {
                    @Override
                    public void run() {
                        runningDistanceValueUI.setText(String.format(Locale.GERMANY, "%.2f", mapsController.getTotalDistance()));
                    }
                });

                //steps
                final TextView stepCounterValueUI = findViewById(R.id.value_2);
                // call this because of thread
                stepCounterValueUI.post(new Runnable() {
                    @Override
                    public void run() {
                        stepCounterValueUI.setText(String.valueOf(stepsCounted));
                    }
                });


                break;
            case 1: // cycling

                // set view - show distance, speed
                // distance
                final TextView cyclingDistanceValueUI = findViewById(R.id.value_1);
                cyclingDistanceValueUI.post(new Runnable() {
                    @Override
                    public void run() {
                        cyclingDistanceValueUI.setText(String.format(Locale.GERMANY, "%.2f", mapsController.getTotalDistance()));
                    }
                });

                // speed
                final TextView cyclingSpeedValueUI = findViewById(R.id.value_2);
                cyclingSpeedValueUI.post(new Runnable() {
                    @Override
                    public void run() {
                        cyclingSpeedValueUI.setText(String.valueOf(mapsController.getSpeed()));
                    }
                });

                break;
            case 2: // pushups
                final int calculatedPushUps = pushupService.getCalculatedPushUps();

                // set view - show count
                final TextView pushupCountValueUI = findViewById(R.id.value_1);
                pushupCountValueUI.post(new Runnable() {
                    @Override
                    public void run() {
                        pushupCountValueUI.setText(String.valueOf(calculatedPushUps));
                    }
                });
                break;
            case 3: // situps

                final int situpCount = sitUpsCtrl.getSitupCount();

                // set view - show count
                final TextView situpCountValueUI = findViewById(R.id.value_1);
                situpCountValueUI.post(new Runnable() {
                    @Override
                    public void run() {
                        situpCountValueUI.setText(String.valueOf(situpCount));
                    }
                });
                break;
        }

        // set time in UI
        final TextView timeValueUI = findViewById(R.id.value_3);
        timeValueUI.post(new Runnable() {
            @Override
            public void run() {
                timeValueUI.setText(duration);
            }
        });
    }

    /**
     * load mapsController to show map with live location data
     */
    private void includeMap() {
        //Insert map in our view
        if (exerciseMode <= 1) {
            mapsController = new MapsController(this);

            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.map_container, mapsController.mapFragment);
            fragmentTransaction.commit();
        }
    }

    /**
     * onClickListener for CloseBtn
     */
    @Override
    public void onClick(View v) {

        saveData();
        this.finish();
    }

    /**
     * saves data to db
     */
    private void saveData() {
        //end tracking
        Date now = Calendar.getInstance().getTime();
        activeProcessing = false;

        // save data to database
        SportExercise newSportExercise = new SportExercise();
        newSportExercise.setTripTime(getFormattedCurrentDuration());
        newSportExercise.setDate(now);
        newSportExercise.setUserId(db.userDao().getAll().get(0).getUid());
        newSportExercise.setMode(exerciseMode);
        long exerciseId = db.sportExerciseDao().insert(newSportExercise);
        newSportExercise.setId(exerciseId);

        switch (exerciseMode) {
            case 0: // running

                // set exerciseId to each locationData
                ArrayList<LocationData> runningLinePoints = mapsController.getLinePoints();

                // db
                newSportExercise.setDistance(mapsController.getTotalDistance());
                newSportExercise.setAmountOfRepeats(stepCounterService.getActualCount());
                setExerciseIdToLocationData(runningLinePoints, exerciseId);

                // stop tracking
                stepCounterService.onStop();
                mapsController.stopTracking();
                break;
            case 1: // cycling

                // set exerciseId to each locationData
                ArrayList<LocationData> cyclingLinePoints = mapsController.getLinePoints();

                // db
                newSportExercise.setDistance(mapsController.getTotalDistance());
                newSportExercise.setSpeed(mapsController.getMaxSpeed());
                setExerciseIdToLocationData(cyclingLinePoints, exerciseId);


                // stop tracking
                mapsController.stopTracking();
                break;
            case 2: // pushups

                int calculatedPushUps = pushupService.getCalculatedPushUps();

                // db
                newSportExercise.setAmountOfRepeats(calculatedPushUps);

                // stop tracking
                pushupService.stopListening();
                break;
            case 3: // situps

                int calculatedSitups = sitUpsCtrl.getSitupCount();

                // db
                newSportExercise.setAmountOfRepeats(calculatedSitups);

                // stop tracking
                sitUpsCtrl.stopListening();
                break;
        }

        db.sportExerciseDao().updateSportExercises(newSportExercise);

    }

    /**
     * sets the exercise id to each location data
     *
     * @param linePoints the gathered location data
     * @param exerciseId the exercise id of the current exercise
     */
    private void setExerciseIdToLocationData(ArrayList<LocationData> linePoints, long exerciseId) {
        for (LocationData data :
                linePoints) {
            data.setExerciseId(exerciseId);
            db.locationDataDao().insert(data);
        }
    }

    private String getFormattedCurrentDuration() {
        Date now = Calendar.getInstance().getTime();
        long duration = now.getTime() - startExercisingTime.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        long hours = duration / hoursInMilli;
        long minutes = duration / minutesInMilli - hours * 60;
        long seconds = duration / secondsInMilli - hours * 3600 - minutes * 60;

        return MessageFormat.format("{0}:{1}:{2}", hours, minutes, seconds);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == REQUEST_FINE_LOCATION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mapsController.startLocation();

        }
    }


    @Override
    protected void onDestroy() {

        // if stopBtn is not hit before
        if (activeProcessing) {
            saveData();
        }

        super.onDestroy();

    }
}
