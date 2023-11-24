package com.example.spot.model.clustering;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "spot")
public class Spot {
    private final Marker center;
    private final Integer level;
    private List<Marker> locations = new ArrayList<>();

    public void addLocation(Marker marker) {
        locations.add(marker);
    }

    public boolean isSameLevel(Integer level) {
        return level.equals(this.level);
    }

    public Marker getCenter() {
        return center;
    }
}
