package com.example.maprace.model;

import android.location.Location;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.util.Consumer;

import com.example.maprace.GameActivity;
import com.example.maprace.data.model.GameMode;
import com.example.maprace.data.model.Preference;
import com.example.maprace.service.POIService;
import com.example.maprace.service.PersistenceService;

import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameModel implements IMyLocationConsumer {
    public enum Status {UNINITIALIZED, LOADING, READY, STARTED, ENDED}
    public static final String[] poiTypes = {"restaurant", "bank", "hotel"};

    private static final int MAX_DISTANCE = 5;
    private static final int MAX_RESULTS_PER_CATEGORY = 10;
    private static final int DISTANCE_THRESHOLD = 150;

    public static final float CAR_SPEED_LIMIT = 80f;
    public static final float BIKE_SPEED_LIMIT = 35f;
    public static final float WALK_SPEED_LIMIT = 20f;

    private final GameActivity gameActivity;
    private final GpsMyLocationProvider locationProvider;
    private final PersistenceService persistenceService;

    private Status status;
    private final GameMode gameMode;
    private Location previousLocation;
    private Location currentLocation;
    private float currentSpeed;
    private float distanceWalked;
    private long elapsedTime;
    private int goal;

    // Note: mPOIs and landmarks store exactly the same candidate landmarks.  POI stores more info than our custom landmark class.
    // Need to decide which one to go with.
    private List<POI> mPOIs;
    private final Set<GeoPoint> visitedPOIs;

    public GameModel(GameActivity gameActivity) {
        setStatus(Status.UNINITIALIZED);
        this.gameActivity = gameActivity;
        persistenceService = PersistenceService.getInstance();
        gameMode = persistenceService.getGameMode();

        locationProvider = new GpsMyLocationProvider(gameActivity);
        locationProvider.startLocationProvider(this);

        // TODO: persist visitedPOIs
        visitedPOIs = new HashSet<>();
        mPOIs = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initGame() {
        setStatus(Status.LOADING);
        fetchLandmarks(pois -> {
            // shuffles landmarks according to preference
            Preference preference = persistenceService.getPreference();
            List<Preference.Entry> preferenceEntries = preference.getEntries();
            Map<String, Integer> counter = new HashMap<>();

            for (Preference.Entry entry : preferenceEntries)
                counter.put(entry.getId(), entry.getValue());

            List<POI> selectedPOIs = new ArrayList<>();

            for (POI poi : pois) {
                int count = counter.getOrDefault(poi.mType, 0);
                if (count == 0) continue;

                selectedPOIs.add(poi);
                counter.put(poi.mType, count - 1);
            }

            setmPOIs(selectedPOIs);
            setStatus(Status.READY);

            gameActivity.onPOIsReceived(getmPOIs());
        });
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    private Location getCurrentLocation() {
        return currentLocation;
    }

    private Status getStatus() {
        return status;
    }

    private void setStatus(Status status) {
        this.status = status;
    }

    private void setCurrentLocation(Location location, IMyLocationProvider locationSource) {
        previousLocation  = currentLocation;
        currentLocation = location;

        gameActivity.updateCurrentLocation(currentLocation, previousLocation, locationSource);
    }

    private Location getPreviousLocation() {
        return previousLocation;
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(float currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    private float getDistanceWalked() {
        return distanceWalked;
    }

    private void setDistanceWalked(float distanceWalked) {
        this.distanceWalked = distanceWalked;
        gameActivity.updateDistanceWalked(distanceWalked);
    }

    private long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public List<POI> getmPOIs() {
        return mPOIs;
    }

    synchronized private void setmPOIs(List<POI> mPOIs) {
        this.mPOIs = mPOIs;
        gameActivity.onUpdatePOIs(mPOIs);
    }

    private void markPOIVisited(POI poi) {
        visitedPOIs.add(poi.mLocation);
        gameActivity.updatePOIVisited();
    }

    public boolean isPOIVisited(POI poi) {
        return visitedPOIs.contains(poi.mLocation);
    }

    public int getNumberOfVisitedPOIs() {
        return visitedPOIs.size();
    }

    public int getGoal() {
        return goal;
    }

    private void setGoal(int goal) {
        this.goal = goal;
        gameActivity.updateGoal(goal);
    }

    public float getSpeedLimit() {
        switch (getGameMode()) {
            case BIKE:
                return BIKE_SPEED_LIMIT;

            case CAR:
                return CAR_SPEED_LIMIT;

            default:
                break;
        }

        return WALK_SPEED_LIMIT;
    }

    private void fetchLandmarks(Consumer<List<POI>> consumer) {
        Location location = getCurrentLocation();
        if (location == null) return;

        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        // maxDistance: max dist to the position, measured in degrees: (0.008 * km)
        POIService.fetchPOIs(startPoint, poiTypes, MAX_RESULTS_PER_CATEGORY, 0.008 * MAX_DISTANCE, consumer);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onLocationChanged(Location location, IMyLocationProvider source) {
        if (location == null) return;

        setCurrentLocation(location, source);

        if (location.hasSpeed()) setCurrentSpeed(location.getSpeed());
        else setCurrentSpeed(0f);

        switch (getStatus()) {
            case UNINITIALIZED:
                initGame();
                break;

            case STARTED: {
                // updates distance walked
                float distanceWalked = getDistanceWalked();
                if (getPreviousLocation() != null) {
                    distanceWalked += location.distanceTo(getPreviousLocation());
                }
                setDistanceWalked(distanceWalked);

                detectLandmarkReached();
                detectSpeeding();
            }

            default:
                break;
        }
    }

    private void detectSpeeding() {
        if (getCurrentSpeed() <= getSpeedLimit()) return;
        endGame();
        gameActivity.onSpeeding();
    }

    private void detectLandmarkReached() {
        Location currentLocation = getCurrentLocation();
        if (currentLocation == null) return;
        float[] distanceFromLandmark = new float[1];

        for (POI poi: getmPOIs()) {
            // skips visited POIs
            if (isPOIVisited(poi)) continue;

            Location.distanceBetween(currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    poi.mLocation.getLatitude(),
                    poi.mLocation.getLongitude(),
                    distanceFromLandmark);

            if(!isPOIVisited(poi) && distanceFromLandmark[0] <= DISTANCE_THRESHOLD) {
                markPOIVisited(poi);

                boolean goalReached = getGoal() - getNumberOfVisitedPOIs() == 0;
                if (goalReached) {
                    endGame();
                    updateScore();
                    gameActivity.onGameEnded();
                }
            }
        }
    }

    private void updateScore() {
        persistenceService.updateRecords(getDistanceWalked(), getElapsedTime());
    }

    public void startGame(int goal) {
        if (getStatus() == Status.READY) {
            setStatus(Status.STARTED);
            setGoal(goal);
        }
    }

    public void endGame() {
        setStatus(Status.ENDED);
    }

    public void onDestroy() {
        locationProvider.destroy();
    }
}
