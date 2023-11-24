package com.example.spot.service;

import com.example.spot.model.DTO.SpotResponse;
import com.example.spot.model.clustering.Clustering;
import com.example.spot.model.clustering.Marker;
import com.example.spot.model.clustering.Spots;
import com.example.spot.model.clustering.TagType;
import com.example.spot.repository.MarkerRepository;
import com.example.spot.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpotService {
    private static final List<Long> SPOT_SECTION = List.of(1000L, 3000L, 5000L, 7000L, 10000L);
    private final SpotRepository spotRepository;
    private final MarkerRepository markerRepository;
    private final Clustering clustering;

    public void updateSpot() {
        List<Marker> markers = markerRepository.findAll();
        List<Spots> updates = new ArrayList<>();
        for (int i = 0; i < SPOT_SECTION.size(); i++) {
            Spots spots = clustering.run(markers, SPOT_SECTION.get(i));
            updates.add(spots);
        }
        spotRepository.update(updates);
    }

    public SpotResponse createSpotResponse(TagType tagType, Integer level) {
        Optional<Spots> spot = spotRepository.findAllByTagAndLevel(tagType, level);
        //spot.orElse()
    }
}
