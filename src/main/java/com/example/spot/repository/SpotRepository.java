package com.example.spot.repository;

import com.example.spot.model.clustering.Spots;
import com.example.spot.model.clustering.TagType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpotRepository {
    Optional<Spots> findAllByTagAndLevel(TagType tagType, Integer level);

    void update(List<Spots> spots);
}
