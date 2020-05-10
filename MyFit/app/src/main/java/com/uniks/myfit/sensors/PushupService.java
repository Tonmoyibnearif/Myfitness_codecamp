package com.uniks.myfit.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.uniks.myfit.TrackingViewActivity;

/**
 * the service to calculate push ups. It uses proximity sensor
 */
public class PushupService implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor proximity;
    private static boolean isSensorPresent = true;

    private TrackingViewActivity trackingViewActivity;
    private int pushupCount;

    public PushupService(TrackingViewActivity trackingViewActivity) {
        this.trackingViewActivity = trackingViewActivity;
    }

    public void initialize() {
        sensorManager = (SensorManager) trackingViewActivity.getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
                proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                isSensorPresent = true;
            } else {
                isSensorPresent = false;
            }
            sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public int getCalculatedPushUps() {

        return pushupCount;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isSensorPresent) {
            float distanceFromPhone = event.values[0];
                if (distanceFromPhone == 0) {
                    pushupCount++;
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void stopListening() {
        sensorManager.unregisterListener(this, proximity);
    }

}
