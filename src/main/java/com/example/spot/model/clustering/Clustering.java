package com.example.spot.model.clustering;


import com.example.spot.model.DTO.Spot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class Clustering {
    public static List<Spot> run(List<Spot> spots, int numClusters) {
        List<Spot> clusterCenters = initializeClusterCenters(spots, numClusters);
        List<List<Spot>> clusters;

        // 최대 반복 횟수나 수렴 조건을 설정하여 반복
        for (int iter = 0; iter < 100; iter++) {
            clusters = assignToClusters(spots, clusterCenters);
            List<Spot> newClusterCenters = calculateNewCenters(clusters);

            // 중심점이 변하지 않으면 수렴
            if (clusterCenters.equals(newClusterCenters)) {
                break;
            }

            clusterCenters = newClusterCenters;
        }

        return clusterCenters;
    }

    private static List<Spot> initializeClusterCenters(List<Spot> spots, int numClusters) {
        // spots에서 무작위로 numClusters 만큼의 중심점을 선택
        List<Spot> clusterCenters = new ArrayList<>();
        Collections.shuffle(spots, new Random());
        for (int i = 0; i < numClusters; i++) {
            clusterCenters.add(spots.get(i));
        }
        return clusterCenters;
    }

    private static List<List<Spot>> assignToClusters(List<Spot> spots, List<Spot> clusterCenters) {
        List<List<Spot>> clusters = new ArrayList<>();
        for (int i = 0; i < clusterCenters.size(); i++) {
            clusters.add(new ArrayList<>());
        }

        for (Spot spot : spots) {
            int closestCenterIndex = findClosestCenterIndex(spot, clusterCenters);
            clusters.get(closestCenterIndex).add(spot);
        }

        return clusters;
    }

    private static int findClosestCenterIndex(Spot spot, List<Spot> clusterCenters) {
        int closestIndex = 0;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < clusterCenters.size(); i++) {
            double distance = calculateDistance(spot, clusterCenters.get(i));
            if (distance < minDistance) {
                minDistance = distance;
                closestIndex = i;
            }
        }

        return closestIndex;
    }

    private static double calculateDistance(Spot spot1, Spot spot2) {
        return Math.sqrt(Math.pow(spot1.getLongitude() - spot2.getLongitude(), 2) +
                Math.pow(spot1.getLatitude() - spot2.getLatitude(), 2));
    }

    private static List<Spot> calculateNewCenters(List<List<Spot>> clusters) {
        return clusters.stream()
                .map(Clustering::calculateClusterCenter)
                .collect(Collectors.toList());
    }

    private static Spot calculateClusterCenter(List<Spot> cluster) {
        double totalLongitude = 0;
        double totalLatitude = 0;

        for (Spot spot : cluster) {
            totalLongitude += spot.getLongitude();
            totalLatitude += spot.getLatitude();
        }

        double centerLongitude = totalLongitude / cluster.size();
        double centerLatitude = totalLatitude / cluster.size();

        return new Spot(centerLongitude, centerLatitude);
    }
}
