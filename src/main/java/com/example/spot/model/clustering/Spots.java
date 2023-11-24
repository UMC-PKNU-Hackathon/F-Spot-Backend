package com.example.spot.model.clustering;

import java.util.List;

public class Spots {
    private final List<Spot> spots;

    public Spots(List<Spot> spots) {
        this.spots = spots;
    }

    public List<Spot> getSpots() {
        return spots;
    }

    public boolean isSameLevel(Integer level) {
        return spots.get(0).isSameLevel(level);
    }

    public boolean isSameTag(TagType tagType) {
        return spots.get(0)
                .getCenter()
                .getTagType()
                .equals(tagType);
    }
}
