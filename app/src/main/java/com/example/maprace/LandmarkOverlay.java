package com.example.maprace;

import android.graphics.Point;
import android.graphics.drawable.Drawable;

import org.osmdroid.api.IMapView;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class LandmarkOverlay extends ItemizedOverlay<OverlayItem> {

    private ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
    private MapView mapView;

    public LandmarkOverlay(Drawable drawable, MapView mapView) {
        super(drawable);
        this.mapView = mapView;
    }

    public void addOverlayItem(OverlayItem item) {
        overlayItems.add(item);
        populate();
    }

    @Override
    protected OverlayItem createItem(int index) {
        return overlayItems.get(index);
    }

    @Override
    public int size() {
        return overlayItems.size();
    }

    @Override
    protected boolean onTap(int index) {
        return true;
    }

    @Override
    public boolean onSnapToItem(int x, int y, Point snapPoint, IMapView mapView) {
        return false;
    }
}
