package com.example.spot.model.clustering;

import com.example.spot.model.Location;
import com.example.spot.model.Pin;

import java.util.ArrayList;
import java.util.List;

public abstract class Clustering {
    protected List<Pin> pins;
    protected List<Cluster> clusters = new ArrayList<>();

    public abstract List<Pin> run(Location location);
    protected static class Cluster {
        private final Pin center;

        private final List<Pin> locations = new ArrayList<>();

        public Cluster(Pin center) {
            this.center = center;
        }

        public void addLocation(Pin pin) {
            locations.add(pin);
        }

        public Pin getCenter() {
            return center;
        }

        public List<Pin> getLocations() {
            return locations;
        }
    }
}
