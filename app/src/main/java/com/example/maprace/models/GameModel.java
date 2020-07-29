package com.example.maprace.models;

import android.location.Location;

import com.example.maprace.GameActivity;
import com.example.maprace.services.POIService;
import com.example.maprace.utils.StorageUtils;

import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameModel implements IMyLocationConsumer {
    public enum GameMode {
        WALK("WALK"),
        BIKE("BIKE"),
        CAR("CAR");

        private final String value;

        GameMode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private static final int DISTANCE_THRESHOLD = 150;
    // TODO: Fetch poiTypes from Profile/Settings
    private static String[] poiTypes = {"restaurant", "bank", "hotel"};

    private final GameActivity gameActivity;
    private final GpsMyLocationProvider locationProvider;

    private Location previousLocation;
    private Location currentLocation;
    private float distanceWalked;
    private long elapsedTime;
    private final GameMode gameMode;
    private int goal;
    private boolean finished;

    // Note: mPOIs and landmarks store exactly the same candidate landmarks.  POI stores more info than our custom landmark class.
    // Need to decide which one to go with.
    private List<POI> mPOIs;
    private final Set<GeoPoint> visitedPOIs;

    public GameModel(GameActivity gameActivity, GameMode gameMode) {
        this.gameActivity = gameActivity;
        this.gameMode = gameMode;

        locationProvider = new GpsMyLocationProvider(gameActivity);
        locationProvider.startLocationProvider(this);

        // TODO: persist visitedPOIs
        visitedPOIs = new HashSet<>();
        mPOIs = new ArrayList<>();
    }

    public void startGame() {
        fetchLandmarks();
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    private Location getCurrentLocation() {
        return currentLocation;
    }

    private void setCurrentLocation(Location location, IMyLocationProvider locationSource) {
        previousLocation  = currentLocation;
        currentLocation = location;

        gameActivity.updateCurrentLocation(location, previousLocation, locationSource);
    }

    private Location getPreviousLocation() {
        return previousLocation;
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
        if (!finished) gameActivity.updateUIWithPOI(mPOIs);
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

    public void setGoal(int goal) {
        this.goal = goal;
        gameActivity.updateGoal(goal);
    }

    private void fetchLandmarks() {
        Location location = getCurrentLocation();
        if (location == null) return;

        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        // maxDistance: max dist to the position, measured in degrees: (0.008 * km)
        POIService.fetchPOIs(startPoint, poiTypes, 5, 0.008 * 5, this::setmPOIs);
    }

    @Override
    public void onLocationChanged(Location location, IMyLocationProvider source) {
        if (location != null) {
            setCurrentLocation(location, source);

            // updates distance walked
            float distanceWalked = getDistanceWalked();
            if (getPreviousLocation() != null) {
                distanceWalked += location.distanceTo(getPreviousLocation());
            }
            setDistanceWalked(distanceWalked);

            detectLandmarkReached();
        }
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
        UserProfile userProfile = StorageUtils.getUserProfile(gameActivity.getApplicationContext());
        boolean shouldSave = false;

        if (userProfile.getLongestDistance() == null || userProfile.getLongestDistance() < getDistanceWalked()) {
            userProfile.setLongestDistance(getDistanceWalked());
            shouldSave = true;
        }

        if (userProfile.getBestTime() == null || userProfile.getBestTime() > getElapsedTime()) {
            userProfile.setBestTime(getElapsedTime());
            shouldSave = true;
        }

        if (shouldSave) StorageUtils.saveUserProfile(gameActivity.getApplicationContext(), userProfile);
    }

    private void endGame() {
        finished = true;
    }

    public void onDestroy() {
        locationProvider.destroy();
        endGame();
    }
}
