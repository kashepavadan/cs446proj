// Guide for map integration: https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library

package com.example.maprace;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.maprace.component.MapRaceDialog;
import com.example.maprace.component.MapRaceDialogFactory;
import com.example.maprace.data.model.GameMode;
import com.example.maprace.model.GameModel;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GameActivity extends AppCompatActivity {
    private static final String SPEEDING_MESSAGE_TEMPLATE = "Your speed is %.2f km/h! This exceeds the speed limit of %s mode (%.2f km/h). The game will end now.";
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

        gameModel = new GameModel(this);

        chronometer = findViewById(R.id.chronometer);
        goal = findViewById(R.id.goal);
        distance = findViewById(R.id.distance);
        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        ImageView gameModeIcon = findViewById(R.id.gameModeIcon);
        gameModeIcon.setImageResource(getGameModeIcon(gameModel.getGameMode()));

        markersOverlay = new FolderOverlay();
        mapView.getOverlays().add(markersOverlay);

        requestPermissions();

        initMap();
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

    @Override
    public void onBackPressed() {
        showExitDialog(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                GameActivity.super.onBackPressed();
            }
        });
    }

    private void initMap() {
        mapController = mapView.getController();
        myLocationOverlay = new MyLocationNewOverlay(mapView);

        onLocationFocus(null);
        mapView.getOverlays().add(myLocationOverlay);
    }

    private void startGame(int goal) {
        gameModel.startGame(goal);
        startChronometer();
    }

    private void endGame() {
        stopChronometer();
        gameModel.endGame();
    }

    //////// UI UPDATE METHODS TRIGGERED BY GAME MODEL ////////

    public void updateCurrentLocation(Location location, Location prevLocation, IMyLocationProvider source) {
        myLocationOverlay.onLocationChanged(location, source);
    }

    public void onPOIsReceived(List<POI> pois) {
        if (pois.isEmpty()) {
            MapRaceDialog notificationDialog = MapRaceDialogFactory.getNotificationDialog();
            notificationDialog.setMessage("No nearby landmarks were found.");
            notificationDialog.show(getSupportFragmentManager(), "emptyPOIDialog");
            return;
        }

        openLandmarkGoalDialog();
    }

    public void updatePOIVisited() {
        updateGoal(gameModel.getGoal());
        onUpdatePOIs(gameModel.getmPOIs());

        Snackbar.make(findViewById(R.id.coordinator_layout), "Landmark Reached!", 5000).show();
    }

    public void onUpdatePOIs(List<POI> pois) {
        markersOverlay.getItems().clear();

        for (POI poi : pois) {
            Marker poiMarker = new Marker(mapView);
            poiMarker.setTitle(poi.mType);
            poiMarker.setSnippet(poi.mDescription);
            poiMarker.setPosition(poi.mLocation);
            poiMarker.setVisible(!gameModel.isPOIVisited(poi));

            markersOverlay.add(poiMarker);
        }
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
        MapRaceDialog gameEndDialog = MapRaceDialogFactory.getNotificationDialog();

        long milliseconds = gameModel.getElapsedTime();
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds - TimeUnit.HOURS.toMillis(hours));

        String msg = String.format(Locale.getDefault(), "Congratulations, you've reached the goal! Your time is %d hrs %d mins and distance is %.2f km.",
                hours,
                minutes,
                gameModel.getDistanceWalked() / 1000);
        gameEndDialog.setMessage(msg);
        gameEndDialog.setCanceledOnTouchOutside(false);
        gameEndDialog.setOnPositiveClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        gameEndDialog.show(getSupportFragmentManager(), "gameEndDialog");
    }

    ///////////////////////////////////////////////////////////////

    private int getGameModeIcon(GameMode mode) {
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

    private void showExitDialog(DialogInterface.OnClickListener onPositiveClickListener) {
        MapRaceDialog confirmationDialog = MapRaceDialogFactory.getConfirmationDialog();

        confirmationDialog.setMessage("Are you sure you want to exit the game?");
        confirmationDialog.setOnPositiveClickListener(onPositiveClickListener);
        confirmationDialog.show(getSupportFragmentManager(), "exitGameConfirmationDialog");
    }

    public void onExitGame(View v) {
        showExitDialog(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                endGame();
                finish();
            }
        });
    }

    private void openLandmarkGoalDialog(){
        MapRaceDialog dialog = MapRaceDialogFactory.getNumberPickerDialog(1, gameModel.getmPOIs().size());

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Select the number of landmarks you aim to visit:");
        dialog.setOnConfirmListener(new MapRaceDialog.OnConfirmListener() {
            @Override
            public void onConfirm(Object goal) {
                startGame((Integer) goal);
            }
        });
        dialog.show(getSupportFragmentManager(), "landmark goal dialog");
    }

    public void onSpeeding() {
        GameMode gameMode = gameModel.getGameMode();
        String message = String.format(Locale.getDefault(), SPEEDING_MESSAGE_TEMPLATE,
                gameModel.getCurrentSpeed(), gameMode.getValue(), gameModel.getSpeedLimit());
        MapRaceDialog notificationDialog = MapRaceDialogFactory.getNotificationDialog();

        notificationDialog.setMessage(message);
        notificationDialog.setCanceledOnTouchOutside(false);
        notificationDialog.setOnPositiveClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                endGame();
                finish();
            }
        });
        notificationDialog.show(getSupportFragmentManager(), "speedingNotificationDialog");
    }

    public void onLocationFocus(View view) {
        myLocationOverlay.enableFollowLocation();
        mapController.setZoom(18L);
    }
}