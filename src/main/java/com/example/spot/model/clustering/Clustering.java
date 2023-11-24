package com.example.spot.model.clustering;

import com.example.spot.model.Marker;

import java.util.ArrayList;
import java.util.List;

public abstract class Clustering {
    protected List<Marker> markers;
    protected List<Cluster> clusters = new ArrayList<>();

    public abstract List<Marker> run(Long mapSize);

    protected static class Cluster {
        private final Marker center;
        private final List<Marker> locations = new ArrayList<>();

        public Cluster(Marker center) {
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
}
