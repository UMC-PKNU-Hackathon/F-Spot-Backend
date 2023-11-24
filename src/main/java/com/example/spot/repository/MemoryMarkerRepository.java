package com.example.spot.repository;

import com.example.spot.model.MapType;
import com.example.spot.model.Marker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryMarkerRepository implements MarkerRepository {
    private static Map<Long, Marker> store = new HashMap<>();
    private long sequence;

    public MemoryMarkerRepository() {
        init();
    }

    private void init() {
        store.put(0L, new Marker(0L, 1.0, 2.4, MapType.TOUR));
        store.put(1L, new Marker(1L, 1.1, 2.6, MapType.SNS));
        store.put(2L, new Marker(2L, 1.5, 2.1, MapType.SNS));
        store.put(3L, new Marker(3L, 1.3, 1.5, MapType.ACCIDENT));
        store.put(4L, new Marker(4L, 1.5, 2.5, MapType.TOUR));
        sequence = store.size();
    }


    @Override
    public Marker findById(Long id) {
        return store.get(id);
    }

    @Override
    public List<Marker> findAll() {
        return store.values()
                .stream()
                .toList();
    }

    @Override
    public void add(Marker marker) {
        store.put(sequence++, marker);
    }
}
