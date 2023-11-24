package com.example.spot.service;

import com.example.spot.model.clustering.Clustering;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarkerService {
    private final Clustering clustering;
}
