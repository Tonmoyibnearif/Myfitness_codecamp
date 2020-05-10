package com.uniks.myfit.helper;

import com.uniks.myfit.model.SportExercise;
import com.uniks.myfit.model.User;

public class FittnessDataHelper {
    private SportExercise exercise;
    private User user;

    public FittnessDataHelper(SportExercise exercise, User user) {

        this.exercise = exercise;
        this.user = user;
    }

    public int calculateCalories() {

        int burntCalories = 0;

        switch (exercise.getMode()) {
            case 0:
                // Running

                burntCalories = (int) ((getAvgSpeed() - 0.8) * user.getWeight() * getDurationInHours());

                break;
            case 1:
                // Cycling

                burntCalories = (int) ((0.527 * getAvgSpeed() - 1.166) * getDurationInHours() * user.getWeight());

                break;
            case 2:
            case 3:
                // Sit Ups and Push Ups

                burntCalories = (int) (4.5 * user.getWeight() * getDurationInHours());

                break;
        }

        if (burntCalories < 0) {
            burntCalories = 0;
        }

        return burntCalories;
    }

    public double getAvgSpeed() {
        // km / h

        return exercise.getDistance() / getDurationInHours();
    }

    private double getDurationInHours() {

        double hours;
        double minutes;
        double seconds;

        String[] split = exercise.getTripTime().split(":");

        hours = Double.valueOf(split[0]);
        minutes = Double.valueOf(split[1]);
        seconds = Double.valueOf(split[2]);

        hours = hours + (minutes / 60) + (seconds / 3600);

        return hours;

    }
}
