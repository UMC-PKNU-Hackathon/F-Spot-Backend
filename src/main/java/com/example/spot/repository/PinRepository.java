package com.example.spot.repository;

import com.example.spot.model.Pin;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PinRepository {
    // 서버 상의 모든 핀을 받아온다.
    Pin findById(Long id);

    List<Pin> findAll();

    void add(Pin pin);
}
