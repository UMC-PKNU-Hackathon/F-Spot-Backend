package com.example.spot.repository;

import com.example.spot.model.Location;
import com.example.spot.model.MapType;
import com.example.spot.model.Pin;

import java.util.List;
import java.util.Map;

public class MemoryPinRepository implements PinRepository {
    private static Map<Long, Pin> store = Map.ofEntries(
            Map.entry(0L, new Pin(new Location(1.0, 2.5), MapType.SNS)),
            Map.entry(1L, new Pin(new Location(1.1, 2.6), MapType.SNS)),
            Map.entry(2L, new Pin(new Location(1.5, 2.1), MapType.SNS)),
            Map.entry(3L, new Pin(new Location(1.3, 1.5), MapType.ACCIDENT)),
            Map.entry(4L, new Pin(new Location(1.5, 2.5), MapType.TOUR))
    );
    private long sequence = store.size();

    @Override
    public Pin findById(Long id) {
        return store.get(id);
    }

    @Override
    public List<Pin> findAll() {
        return store.values()
                .stream()
                .toList();
    }

    @Override
    public void add(Pin pin) {
        store.put(sequence++, pin);
    }
}
