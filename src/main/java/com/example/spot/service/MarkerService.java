package com.example.spot.service;

import com.example.spot.model.clustering.Marker;
import com.example.spot.model.clustering.TagType;
import com.example.spot.repository.MarkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarkerService {
    private final MarkerRepository repository;

    public Long addMarker(Double latitude, Double longitude, TagType tagType) {
        Marker marker = Marker.builder()
                .latitude(latitude)
                .longitude(longitude)
                .tagType(tagType)
                .build();

        repository.add(marker);
        return marker.getId();
    }

    public Long removeMarker(Long id) {
        return repository.delete(id);
    }
}
