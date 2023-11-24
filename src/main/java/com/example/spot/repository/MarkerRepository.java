package com.example.spot.repository;

import com.example.spot.model.clustering.Marker;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarkerRepository {
    // 서버 상의 모든 핀을 받아온다.
    Marker findById(Long id);

    List<Marker> findAll();

    void add(Marker marker);

    Long delete(Long id);
}
