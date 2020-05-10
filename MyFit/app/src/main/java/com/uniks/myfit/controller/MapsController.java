
package com.uniks.myfit.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.TypedValue;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uniks.myfit.TrackingViewActivity;
import com.uniks.myfit.model.LocationData;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * the controller which controls the map during tracking exercise
 */
public class MapsController implements OnMapReadyCallback, LocationListener {
    public SupportMapFragment mapFragment;

    private Location location;

    private GoogleMap mMap;
    private int padding;
    //class type-which manages the location
    private LocationManager locationManager;
    private TrackingViewActivity trackingViewActivity;

    private boolean firstLocation = true;
    private Polyline polyline;
    private int maxSpeed;

    private ArrayList<LocationData> linePoints = new ArrayList<>();

    public MapsController(TrackingViewActivity trackingViewActivity) {
        this.trackingViewActivity = trackingViewActivity;
        this.padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96, trackingViewActivity.getResources().getDisplayMetrics());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);
        maxSpeed = 0;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setPadding(0, padding, 0, 0);

        //intialize the location manager
        locationManager = (LocationManager) trackingViewActivity.getSystemService(LOCATION_SERVICE);

        // ask for location permissions if not granted already
        if (ActivityCompat.checkSelfPermission(trackingViewActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(trackingViewActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    TrackingViewActivity.REQUEST_FINE_LOCATION);

            return;
        }

        startLocation();

    }

    @SuppressLint("MissingPermission")
    public void startLocation() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        this.location = location;

        //get the latitude and longitude
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        if (firstLocation) {
            // mark the starting point
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Starting Point"));
            // auto zoom to current location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10.2f));

            // draw line
            polyline = mMap.addPolyline(new PolylineOptions().add(userLocation).color(0xff0564ff));

            linePoints.add(new LocationData(userLocation.latitude, userLocation.longitude));

            firstLocation = false;
        } else {
            // track path

            linePoints.add(new LocationData(userLocation.latitude, userLocation.longitude));
            List<LatLng> tmp = new ArrayList<>();

            for (LocationData curr :
                    linePoints) {
                tmp.add(curr.getLatLng());
            }

            // draw line
            polyline.setPoints(tmp);

            // keep map focus on location
            mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * calculates the current speed in km/h and saves max speed
     *
     * @return the calculated speed
     */
    public int getSpeed() {
        if (location == null) {
            return 0;
        }

        int currSpeed = (int) (3.6 * location.getSpeed()); // speed in km/h

        if (currSpeed > maxSpeed) {
            maxSpeed = currSpeed;
        }

        return currSpeed;
    }

    public List getPath() {
        return linePoints;
    }

    /**
     * stop track location
     */
    public void stopTracking() {
        locationManager.removeUpdates(this);
    }

    /**
     * calculates the distance between two points
     *
     * @param pointOne point one
     * @param pointTwo point two
     * @return the calculated distance between pointOne and pointTwo
     */
    private double twoPointDistance(LocationData pointOne, LocationData pointTwo) {
        double R = 6371f; // Radius of the earth in km
        double dLat = (pointOne.getLatitude() - pointTwo.getLatitude()) * Math.PI / 180f;
        double dLon = (pointOne.getLongitude() - pointTwo.getLongitude()) * Math.PI / 180f;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(pointOne.getLatitude() * Math.PI / 180f) * Math.cos(pointTwo.getLatitude() * Math.PI / 180f) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2f * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d;
    }

    /**
     * calculates the total distance between all location points
     *
     * @return the calculated total distance from start point to end point
     */
    public double getTotalDistance() {

        if (linePoints.size() < 2) {
            return 0;
        }

        double totalDistance = 0;
        LocationData prevElement = new LocationData(0, 0);

        for (LocationData currElement : linePoints) {
            if (prevElement.getLongitude() == 0 && prevElement.getLatitude() == 0) { // NOT for Santa
                prevElement = currElement;
                continue;
            }

            totalDistance += twoPointDistance(currElement, prevElement);

            prevElement = currElement;
        }

        return totalDistance;
    }

    public ArrayList<LocationData> getLinePoints() {
        return linePoints;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }
}