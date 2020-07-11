package com.example.maprace.models;

import android.location.Location;

import com.example.maprace.GameActivity;

import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameModel {
    private final GameActivity gameActivity;

    private Location previousLocation;
    private Location currentLocation;
    private float distanceWalked;
    private int goal;

    // Note: mPOIs and landmarks store exactly the same candidate landmarks.  POI stores more info than our custom landmark class.
    // Need to decide which one to go with.
    private List<POI> mPOIs;
    private final Set<GeoPoint> visitedPOIs;

    public GameModel(GameActivity gameActivity) {
        this.gameActivity = gameActivity;

        // TODO: persist visitedPOIs
        visitedPOIs = new HashSet<>();
        mPOIs = new ArrayList<>();
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location location, IMyLocationProvider locationSource) {
        previousLocation  = currentLocation;
        currentLocation = location;

        gameActivity.updateCurrentLocation(location, previousLocation, locationSource);
    }

    public Location getPreviousLocation() {
        return previousLocation;
    }

    public float getDistanceWalked() {
        return distanceWalked;
    }

    public void setDistanceWalked(float distanceWalked) {
        this.distanceWalked = distanceWalked;
        gameActivity.updateDistanceWalked(distanceWalked);
    }

    public List<POI> getmPOIs() {
        return mPOIs;
    }

    synchronized public void setmPOIs(List<POI> mPOIs) {
        this.mPOIs = mPOIs;
        gameActivity.updateUIWithPOI(mPOIs);
    }

    public void markPOIVisited(POI poi) {
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
}
