package com.example.spot.model.clustering;

import com.example.spot.model.Marker;

import java.util.ArrayList;
import java.util.List;

public class Spot {
    private final Marker center;
    private final List<Marker> locations = new ArrayList<>();

    public Spot(Marker center) {
        this.center = center;
    }

    public void addLocation(Marker marker) {
        locations.add(marker);
    }

    public Marker getCenter() {
        return center;
    }

    public List<Marker> getLocations() {
        return locations;
    }
}
