package com.example.spot.model.clustering;

import com.example.spot.model.Location;
import com.example.spot.model.Pin;

import java.util.List;

public final class KMeansClustering extends Clustering {
    private static final double EARTH_RADIUS = 6371.01;
    private final double threshold;

    public KMeansClustering(List<Pin> pins, double threshold) {
        this.pins = pins;
        this.threshold = threshold;
    }

    @Override
    public List<Pin> run(Location location) {
        for (Pin pin : pins) {
            boolean addedToCluster = false;

            // 기존 클러스터 중 가장 가까운 클러스터에 위치 추가
            addedToCluster = isAddedToCluster(pin, addedToCluster);

            // 가까운 클러스터가 없을 경우 새로운 클러스터 생성
            if (!addedToCluster) {
                createCluster(pin);
            }
        }
        return convertToPin();
    }

    private boolean isAddedToCluster(Pin pin, boolean addedToCluster) {
        for (Cluster cluster : clusters) {
            if (distance(pin, cluster.getCenter()) <= threshold) {
                cluster.addLocation(pin);
                addedToCluster = true;
                break;
            }
        }
        return addedToCluster;
    }

    private void createCluster(Pin pin) {
        Cluster newCluster = new Cluster(pin);
        clusters.add(newCluster);
    }

    private List<Pin> convertToPin() {
        return clusters.stream()
                .map(Cluster::getCenter)
                .toList();
    }

    private double distance(Pin pin1, Pin pin2) {
        if (!pin1.mapType().equals(pin2.mapType())) {
            return Integer.MAX_VALUE;
        }
        return calculateDistance(pin1.location(), pin2.location());
    }

    private double calculateDistance(Location p1, Location p2) {
        double latitude1 = p1.latitude();
        double longitude1 = p1.longitude();
        double latitude2 = p2.latitude();
        double longitude2 = p2.longitude();

        double a = Math.pow(Math.sin((latitude2 - latitude1) / 2), 2)
                + Math.cos(latitude1) * Math.cos(latitude2)
                * Math.pow(Math.sin((longitude2 - longitude1) / 2), 2);
        return 2 * EARTH_RADIUS * Math.asin(Math.sqrt(a));
    }
}
