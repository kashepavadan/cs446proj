// Guide for map integration: https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library

package com.example.maprace;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.maprace.models.GameModel;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GameActivity extends AppCompatActivity implements LandmarkGoalDialog.LandmarkGoalDialogListener {
    private static final String[] requiredPermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private FolderOverlay markersOverlay;
    private GameModel gameModel;
    private MyLocationNewOverlay myLocationOverlay;
    private IMapController mapController;
    private MapView mapView = null;
    private Chronometer chronometer;
    private TextView goal;
    private TextView distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_game);

        gameModel = new GameModel(this, GameModel.GameMode.WALK);

        chronometer = findViewById(R.id.chronometer);
        goal = (TextView) findViewById(R.id.goal);
        distance = (TextView) findViewById(R.id.distance);
        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        ImageView gameModeIcon = findViewById(R.id.gameModeIcon);
        gameModeIcon.setImageResource(getGameModeIcon(gameModel.getGameMode()));

        markersOverlay = new FolderOverlay();
        mapView.getOverlays().add(markersOverlay);

        requestPermissions();

        initMap();
        openLandmarkGoalDialog();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameModel.onDestroy();
    }

    private void initMap() {
        mapController = mapView.getController();
        myLocationOverlay = new MyLocationNewOverlay(mapView);

        mapController.setZoom(18L);
        mapView.getOverlays().add(myLocationOverlay);
    }

    private void startGame() {
        gameModel.startGame();
        startChronometer();
    }

    //////// UI UPDATE METHODS TRIGGERED BY GAME MODEL ////////

    public void updateCurrentLocation(Location location, Location prevLocation, IMyLocationProvider source) {
        myLocationOverlay.onLocationChanged(location, source);

        if (prevLocation == null) {
            GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            mapController.setCenter(startPoint);
        }
    }

    // display current POIs on the screen
    public void updateUIWithPOI(List<POI> pois) {
        if (pois != null && !pois.isEmpty()) {
            markersOverlay.getItems().clear();

            for (POI poi : pois) {
                Marker poiMarker = new Marker(mapView);
                poiMarker.setTitle(poi.mType);
                poiMarker.setSnippet(poi.mDescription);
                poiMarker.setPosition(poi.mLocation);
                poiMarker.setVisible(!gameModel.isPOIVisited(poi));

                // issue of using custom icon: https://github.com/osmdroid/osmdroid/issues/1349
//                poiMarker.setIcon(poiIcon);
                markersOverlay.add(poiMarker);
            }
        }
    }

    public void updatePOIVisited() {
        updateGoal(gameModel.getGoal());
        updateUIWithPOI(gameModel.getmPOIs());

        Snackbar.make(findViewById(R.id.coordinator_layout), "Landmark Reached!", 5000).show();
    }

    public void updateGoal(int goalNum) {
        int landmarksRemaining = goalNum - gameModel.getNumberOfVisitedPOIs();
        goal.setText(Integer.toString(landmarksRemaining));
    }

    public void updateDistanceWalked(float distanceWalked) {
        distance.setText(String.format(Locale.getDefault(), "%.2f km", distanceWalked / 1000));
    }

    public void onGameEnded() {
        stopChronometer();
        GameEndDialog gameEndDialog = new GameEndDialog(this);
        gameEndDialog.show(getSupportFragmentManager(), "Game end dialog");
    }

    ///////////////////////////////////////////////////////////////

    private int getGameModeIcon(GameModel.GameMode mode) {
        switch (mode) {
            case BIKE:
                return R.drawable.bike;

            case CAR:
                return R.drawable.car;

            default:
                break;
        }

        return R.drawable.walk;
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

    private void startChronometer(){
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                gameModel.setElapsedTime(SystemClock.elapsedRealtime() - chronometer.getBase());
            }
        });

        chronometer.start();
    }

    private void stopChronometer(){
        chronometer.stop();
    }

    public void onExitGame(View v){
        stopChronometer();
        finish();
    }

    public void openLandmarkGoalDialog(){
        DialogFragment dialog = new LandmarkGoalDialog();
        dialog.show(getSupportFragmentManager(), "landmark goal dialog");
    }

    @Override
    public void onLandmarkGoalDialogPositiveClick(DialogFragment dialog, String goalNum) {
        // User touched the dialog's positive button
        gameModel.setGoal(Integer.parseInt(goalNum));
        startGame();
    }
}