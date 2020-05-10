package com.uniks.myfit.controller;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.uniks.myfit.DetailActivity;
import com.uniks.myfit.database.AppDatabase;
import com.uniks.myfit.model.LocationData;
import com.uniks.myfit.model.SportExercise;
import com.uniks.myfit.helper.ShareDataHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This controller controls the map, that is displayed in DetailView
 */
public class DetailViewMapsController implements OnMapReadyCallback, GoogleMap.SnapshotReadyCallback {

    private DetailActivity detailActivity;
    private GoogleMap map;
    private SportExercise exercise;
    private AppDatabase db;
    private List<LocationData> allLocation;

    public DetailViewMapsController(DetailActivity detailActivity, SportExercise exercise, AppDatabase db) {
        this.detailActivity = detailActivity;
        this.exercise = exercise;
        this.db = db;
        allLocation = db.locationDataDao().getAllFromExercise(exercise.getId());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        // get the LatLng from locationData
        List<LatLng> tmp = new ArrayList<>();
        for (LocationData ld :
                allLocation) {
            tmp.add(ld.getLatLng());
        }

        // draw a path where user exercised before
        Polyline polyline = map.addPolyline(new PolylineOptions().color(0xff0564ff));
        polyline.setPoints(tmp);

        if (allLocation.size() > 0) {
            // zoom to location, where user started exercising
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(allLocation.get(0).getLatLng(), 15));
            map.addMarker(new MarkerOptions().position(allLocation.get(0).getLatLng())).setVisible(true);
            map.addMarker(new MarkerOptions().position(allLocation.get(allLocation.size() - 1).getLatLng())).setVisible(true);
        }
    }

    /**
     * initiates a screenshot of the map
     */
    public void doMapScreenshot() {
        map.snapshot(this);
    }

    @Override
    public void onSnapshotReady(Bitmap bitmap) {

        // if app will be uninstalled, the screenshots will be removed too
        File mainDir = new File(detailActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyFit");
        if (!mainDir.exists()) {
            if (mainDir.mkdir()) {
                Log.e("Create Directory", "Main Directory Created: " + mainDir);
            }
        }

        File dir = new File(mainDir.getAbsolutePath());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // store bitmap in file
        File file = new File(mainDir.getAbsolutePath(), "screenshot" + Calendar.getInstance().getTime().getTime() + ".jpg");
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // share
        ShareDataHelper shareDataHelper = new ShareDataHelper(detailActivity, exercise, db);
        shareDataHelper.setScreenshotFile(file);
        shareDataHelper.shareData();
    }

}
