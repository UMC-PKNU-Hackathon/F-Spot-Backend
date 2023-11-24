package com.example.spot.model.clustering;

import com.example.spot.model.Marker;

import java.util.ArrayList;
import java.util.List;

public final class KMeansClustering implements Clustering {
    private static final double EARTH_RADIUS = 6371.01;
    private final List<Spot> spots = new ArrayList<>();

    @Override
    public List<Marker> run(List<Marker> markers, double threshold) {
        for (Marker marker : markers) {
            boolean addedToCluster = false;

            // 기존 클러스터 중 가장 가까운 클러스터에 위치 추가
            addedToCluster = isAddedToCluster(marker, addedToCluster, threshold);

            // 가까운 클러스터가 없을 경우 새로운 클러스터 생성
            if (!addedToCluster) {
                createCluster(marker);
            }
        }
        return convertToPin();
    }

    private boolean isAddedToCluster(Marker marker, boolean addedToSpot, double threshold) {
        for (Spot spot : spots) {
            if (distance(marker, spot.getCenter()) <= threshold) {
                spot.addLocation(marker);
                addedToSpot = true;
                break;
            }
        }
        return addedToSpot;
    }

    private void createCluster(Marker marker) {
        Spot newCluster = new Spot(marker);
        spots.add(newCluster);
    }

    private List<Marker> convertToPin() {
        return spots.stream()
                .map(Spot::getCenter)
                .toList();
    }

    private double distance(Marker marker1, Marker marker2) {
        if (!marker1.getMapType().equals(marker2.getMapType())) {
            return Integer.MAX_VALUE;
        }
        return calculateDistance(marker1, marker2);
    }

    private double calculateDistance(Marker p1, Marker p2) {
        double latitude1 = p1.getLatitude();
        double longitude1 = p1.getLongitude();
        double latitude2 = p2.getLatitude();
        double longitude2 = p2.getLongitude();

        double a = Math.pow(Math.sin((latitude2 - latitude1) / 2), 2)
                + Math.cos(latitude1) * Math.cos(latitude2)
                * Math.pow(Math.sin((longitude2 - longitude1) / 2), 2);
        return 2 * EARTH_RADIUS * Math.asin(Math.sqrt(a));
    }
}
