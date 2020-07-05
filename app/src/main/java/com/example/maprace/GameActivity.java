// Guide for map integration: https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library

package com.example.maprace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private static final String[] requiredPermissions = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    private MapView mapView = null;
    private Location currentLocation = null;
    private Landmark[] landmarks = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_game);

        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        requestPermissions();

        initMap();
        initLandmarks();
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
                    mapController.setCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));
                }
                currentLocation = location;
            }
        });
        mapController.setZoom(18L);
    }

    private void initLandmarks() {
        if (this.landmarks.length != 0) return;
        // Initialize Landmarks
        // TODO: Fetch data from somewhere
        this.landmarks = new Landmark[]{
                new Landmark("Davis Center", new GeoPoint(43.472482, -80.542116))
        };
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
}