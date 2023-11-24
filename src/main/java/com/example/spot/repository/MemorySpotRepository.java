package com.example.spot.repository;

import com.example.spot.model.clustering.Spots;
import com.example.spot.model.clustering.TagType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemorySpotRepository implements SpotRepository {
    private static List<Spots> stores = new ArrayList<>();

    @Override
    public Optional<Spots> findAllByTagAndLevel(TagType tagType, Integer level) {
        return stores.stream()
                .filter(e -> e.isSameLevel(level) && e.isSameTag(tagType))
                .findAny();
    }

    @Override
    public void update(List<Spots> spots) {
        stores.clear();
        stores = spots;
    }
}
