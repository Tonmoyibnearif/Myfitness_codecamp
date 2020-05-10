package com.uniks.myfit.sensors;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.uniks.myfit.TrackingViewActivity;

/**
 * the service to calculate steps.
 */
public class StepCounterService implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensorCount;
    private TrackingViewActivity trackingViewActivity;
    private float startStepCounter;
    private int actualCount;

    public StepCounterService(TrackingViewActivity trackingViewActivity) {
        this.trackingViewActivity = trackingViewActivity;
    }

    public void onStart() {

        startStepCounter = 0;
        PackageManager pm = trackingViewActivity.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)) {
            //If it's available we can retrieve the value using following code
            sensorManager = (SensorManager) trackingViewActivity.getSystemService(Context.SENSOR_SERVICE);

            if (sensorManager != null) {
                sensorCount = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
                sensorManager.registerListener(this, sensorCount, SensorManager.SENSOR_DELAY_UI);
            }

        } else {
            Log.e("StepCounterService", "does not have step_counter_sensor");
        }

    }

    public void onStop() {
        sensorManager.unregisterListener(this, sensorCount);
    }

    /**
     * An onSensorChanged event gets triggered every time new step count is detected.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {

            if (startStepCounter == 0) {
                startStepCounter = event.values[0];
            }

            float endStepCounter = event.values[0];

            actualCount = (int) (endStepCounter - startStepCounter);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public int getActualCount() {
        return actualCount;
    }
}