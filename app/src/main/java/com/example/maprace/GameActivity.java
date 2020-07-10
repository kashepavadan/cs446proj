// Guide for map integration: https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library

package com.example.maprace;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity implements LandmarkGoalDialog.LandmarkGoalDialogListener {

    private static final String[] requiredPermissions = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    private MapView mapView = null;
    private Location currentLocation = null;
    private ArrayList<Landmark> landmarks = new ArrayList<Landmark>();
    private Chronometer chronometer;
    private TextView goal;
    private boolean running;
    private int landmarksRemaining;
    private float[] distance = new float[1];

    // Note: mPOIs and landmarks store exactly the same candidate landmarks.  POI stores more info than our custom landmark class.
    // Need to decide which one to go with.
    private ArrayList<POI> mPOIs = new ArrayList<POI>();

    // TODO: Fetch poiTypes from Profile/Settings
    private String[] poiTypes = {"restaurant", "bank", "hotel"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_game);

        chronometer = findViewById(R.id.chronometer);
        goal = (TextView) findViewById(R.id.goal);
        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        requestPermissions();

        initMap();
        openLandmarkGoalDialog();
        //startChronometer();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mapView.onResume();
    }

    private void initMap() {
        GpsMyLocationProvider locationProvider = new GpsMyLocationProvider(this);
        final MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(mapView);
        final IMapController mapController = mapView.getController();

        mapView.getOverlays().add(myLocationOverlay);

        locationProvider.startLocationProvider(new IMyLocationConsumer() {
            @Override
            public void onLocationChanged(Location location, IMyLocationProvider source) {
                myLocationOverlay.onLocationChanged(location, source);
                if (currentLocation == null) {
                    GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mapController.setCenter(startPoint);
                    // maxDistance: max dist to the position, measured in degrees: (0.008 * km)
                    getPOIsAsync(startPoint, poiTypes, 5, 0.008 * 5);
                }
                currentLocation = location;

                for(POI landmark: mPOIs){
                    Location.distanceBetween(currentLocation.getLatitude(),
                            currentLocation.getLongitude(),
                            landmark.mLocation.getLatitude(),
                            landmark.mLocation.getLongitude(),
                            distance);

                    if(distance[0] <= 100){
                        landmarksRemaining -= 1;
                        goal.setText(Integer.toString(landmarksRemaining));
                        Snackbar.make(findViewById(R.id.coordinator_layout), "location reached", 5).show();
                        if(landmarksRemaining != 0){
                            getPOIsAsync(landmark.mLocation, poiTypes, 5, 0.008 * 5);
                        }else{
                            stopChronometer(goal);
                        }
                    }
                }
            }
        });
        mapController.setZoom(18L);
    }

    // unused method. initMap would init landmarks automatically. Left here for later updates if needed
    private void initLandmarks() {
        if (this.landmarks.size() != 0) return;
        // Initialize Landmarks
        if (this.currentLocation != null) {
            getPOIsAsync(new GeoPoint(this.currentLocation), this.poiTypes, 5, 0.008 * 5);
        }

        // Add marker to map
        if (this.mapView != null) {
            List<Overlay> mapOverlays = this.mapView.getOverlays();
            LandmarkOverlay landmarkOverlay = new LandmarkOverlay(getDrawable(R.drawable.marker), mapView);
            for (Landmark landmark: this.landmarks) {
                OverlayItem overlayItem = new OverlayItem(landmark.getName(), "", landmark.getLocation());
                landmarkOverlay.addOverlayItem(overlayItem);
            }
            mapOverlays.add(landmarkOverlay);
        }
    }

    private void requestPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();

        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty())
            ActivityCompat.requestPermissions(this,
                    permissionsToRequest.toArray(new String[0]), 1);
    }

    private class fetchPOIs extends AsyncTask<Object, Void, ArrayList<POI>> {
        protected ArrayList<POI> doInBackground(Object... params) {
            GeoPoint startPoint = (GeoPoint) params[0];
            String[] poiTypes = (String[]) params[1];
            Integer maxResultsPerCategory = (Integer) params[2];
            Double maxDistance = (Double) params[3];
            NominatimPOIProvider poiProvider = new NominatimPOIProvider("OSMBonusPackTutoUserAgent");
            //GeoNamesPOIProvider poiProvider = new GeoNamesPOIProvider("maprace"); // get wikipedia entries
            ArrayList<POI> pois = new ArrayList<POI>();
            for (String poiType : poiTypes) {
                ArrayList<POI> pois_of_type = poiProvider.getPOICloseTo(startPoint, poiType, maxResultsPerCategory, maxDistance);
                pois.addAll(pois_of_type);
            }
            return pois;
        }

        protected void onPostExecute(ArrayList<POI> pois) {
            if (pois == null || pois.isEmpty()) {
                System.out.printf("Problems occurred when fetching POIs - Empty array");
                // TODO: What to do if no nearby locations exists ?
            } else {
                // TODO: Shuffle POIs according to preference ranking ?
                for (POI poi : pois) {
                    Landmark landmark = new Landmark(poi.mCategory, poi.mLocation);
                    landmarks.add(landmark);
                }
                mPOIs = pois;
                updateUIWithPOI(pois);
            }
        }
    }

    // get a list of nearby POIs
    private void getPOIsAsync(GeoPoint startPoint, String[] poiTypes, Integer maxResultsPerCategory, Double maxDistance) {
        new fetchPOIs().execute(startPoint, poiTypes, maxResultsPerCategory, maxDistance);
    }

    // display current POIs on the screen for testing purpose
    private void updateUIWithPOI(ArrayList<POI> pois) {
        if (pois != null && !pois.isEmpty()) {
            FolderOverlay poiMarkers = new FolderOverlay(this);
            mapView.getOverlays().add(poiMarkers);
            Drawable poiIcon = getDrawable(R.drawable.marker);
            for (POI poi:pois) {
                Marker poiMarker = new Marker(mapView);
                poiMarker.setTitle(poi.mType);
                poiMarker.setSnippet(poi.mDescription);
                poiMarker.setPosition(poi.mLocation);
                poiMarker.setIcon(poiIcon);
                poiMarkers.add(poiMarker);
            }
        }
    }

    public void startChronometer(){
        if(!running){
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            running = true;
        }
    }

    public void stopChronometer(View v){
        chronometer.setBase(SystemClock.elapsedRealtime());
        running = false;
        finish();
    }

    public void openLandmarkGoalDialog(){
        DialogFragment dialog = new LandmarkGoalDialog();
        dialog.show(getSupportFragmentManager(), "landmark goal dialog");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String goalNum) {
        // User touched the dialog's positive button
        landmarksRemaining = Integer.parseInt(goalNum);
        goal.setText(goalNum);
        startChronometer();
    }
}