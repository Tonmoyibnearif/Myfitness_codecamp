package com.uniks.myfit.helper;

import android.content.Intent;
import android.net.Uri;

import com.uniks.myfit.DetailActivity;
import com.uniks.myfit.R;
import com.uniks.myfit.database.AppDatabase;
import com.uniks.myfit.model.LocationData;
import com.uniks.myfit.model.SportExercise;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * helper class to share data.
 */
public class ShareDataHelper {

    private DetailActivity detailActivity;
    private SportExercise exercise;
    private AppDatabase db;
    private File screenshotFile;

    public ShareDataHelper(DetailActivity detailActivity, SportExercise exercise, AppDatabase db) {

        this.detailActivity = detailActivity;
        this.db = db;
        this.exercise = exercise;
    }

    public void shareData() {
        String shareSub = detailActivity.getResources().getString(R.string.share_subject);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getMessage());

        if (exercise.getMode() == 0 || exercise.getMode() == 1) {
            shareIntent.setType("*/*");
            if (screenshotFile != null) {
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(screenshotFile));
            }

        } else if (exercise.getMode() == 2 || exercise.getMode() == 3) {
            shareIntent.setType("text/plain");
        }


        detailActivity.startActivity(Intent.createChooser(shareIntent, detailActivity.getResources().getString(R.string.share_title)));
    }

    private String getMessage() {

        List<LocationData> locationData = db.locationDataDao().getAllFromExercise(exercise.getId());
        FittnessDataHelper fittnessDataHelper = new FittnessDataHelper(exercise, db.userDao().getAll().get(0));

        SimpleDateFormat dateSdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
        SimpleDateFormat timeSdf = new SimpleDateFormat("hh:mm", Locale.GERMANY);


        StringBuilder message = new StringBuilder(detailActivity.getResources().getString(R.string.share_message, dateSdf.format(exercise.getDate()), timeSdf.format(exercise.getDate()), exercise.getTripTime(), fittnessDataHelper.calculateCalories()));

        StringBuilder addition = new StringBuilder();
        switch (exercise.getMode()) {
            case 0: // running

                addition.append(" ").append(detailActivity.getResources().getString(R.string.share_addition_running, String.format(Locale.GERMANY, "%.2f", exercise.getDistance()), exercise.getAmountOfRepeats(), fittnessDataHelper.getAvgSpeed()));

                StringBuilder runningRoute = new StringBuilder(detailActivity.getResources().getString(R.string.share_coordinates));

                // add coordinates of track
                for (int i = 0; i < locationData.size(); i++) {
                    runningRoute.append(locationData.get(i).getLatitude()).append(", ").append(locationData.get(i).getLongitude());
                    if (i != locationData.size() - 1) {
                        // last element
                        runningRoute.append("\n");
                    }
                }


                addition.append(runningRoute);

                break;
            case 1: // cycling

                addition.append(detailActivity.getResources().getString(R.string.share_addition_cycling, exercise.getDistance(), exercise.getSpeed(), fittnessDataHelper.getAvgSpeed()));

                StringBuilder cyclingRoute = new StringBuilder(detailActivity.getResources().getString(R.string.share_coordinates));

                // add coordinates of track
                for (int i = 0; i < locationData.size(); i++) {
                    cyclingRoute.append(locationData.get(i).getLatitude()).append(", ").append(locationData.get(i).getLongitude());
                    if (i != locationData.size() - 1) {
                        // last element
                        cyclingRoute.append("\n");
                    }
                }


                addition.append(cyclingRoute);

                break;

            case 2: // pushups

                addition.append(detailActivity.getResources().getString(R.string.share_amount, exercise.getAmountOfRepeats(), "Push", fittnessDataHelper.calculateCalories()));

                break;
            case 3: // situps

                addition.append(detailActivity.getResources().getString(R.string.share_amount, exercise.getAmountOfRepeats(), "Sit", fittnessDataHelper.calculateCalories()));

                break;

        }



        return message.append(addition).toString();
    }

    public void setScreenshotFile(File screenshotFile) {
        this.screenshotFile = screenshotFile;
    }
}
