package com.uniks.myfit.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.uniks.myfit.TrackingViewActivity;
import com.uniks.myfit.model.AccTripleVec;

/**
 * the service to detect an sit up.
 */
public class SitupService implements SensorEventListener {

    private static final float THRESHOLD = 0.3f;
    private static final float ALPHA = 0.98f;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    public Context context;
    private static boolean running = true;

    private TrackingViewActivity trackingViewActivity;
    private float accelerationX, accelerationY, accelerationZ;

    private AccTripleVec prevTriple;
    private boolean rising;
    private int situpCount;

    public SitupService(TrackingViewActivity trackingViewActivity) {
        this.trackingViewActivity = trackingViewActivity;
    }

    /**
     * start listening for sensor data
     */
    public void init() {
        rising = false;
        situpCount = 0;

        running = true;
        // Get an instance of the SensorManager
        sensorManager = (SensorManager) trackingViewActivity.getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (running) {
            // Smooth out readings with a low-pass filter.
            accelerationX = ALPHA * accelerationX + (1 - ALPHA) * sensorEvent.values[0];
            accelerationY = ALPHA * accelerationY + (1 - ALPHA) * sensorEvent.values[1];
            accelerationZ = ALPHA * accelerationZ + (1 - ALPHA) * sensorEvent.values[2];

            AccTripleVec accTripleVec = new AccTripleVec(accelerationX, accelerationY, accelerationZ);

            if (prevTriple == null) {
                prevTriple = accTripleVec;
                return;
            }

            // check for rising tide
            if (accTripleVec.getSquaredMagnitude() > (prevTriple.getSquaredMagnitude() + THRESHOLD) && !rising) {
                rising = true;
            } else if (accTripleVec.getSquaredMagnitude() < (prevTriple.getSquaredMagnitude() - THRESHOLD) && rising) {
                // if there was rising before and now there is a falling, means there is a peak -> sit up
                rising = false;
                situpCount++;
            }

            prevTriple = accTripleVec;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //Unregisters listeners close accelerometer
    public void stopListening() {
        sensorManager.unregisterListener(this, accelerometer);
    }

    public int getSitupCount() {
        if (situpCount >= 1) {
            return situpCount - 1;
        }
        return situpCount;
    }
}

