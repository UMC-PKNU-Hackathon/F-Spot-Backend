package com.example.spot.model.clustering;

import com.example.spot.model.DTO.Spot;

import java.util.List;

public final class KMeansClustering {
    public static List<Spot> findClusterCenters(List<Spot> spots, int numClusters) {
        List<Spot> clusterCenters = Clustering.run(spots, numClusters);
        return clusterCenters;
    }
}
