package com.example.spot.model.clustering;

import com.example.spot.model.Marker;

import java.util.List;

public interface Clustering {
    List<Marker> run(List<Marker> markers, double threshold);
}
