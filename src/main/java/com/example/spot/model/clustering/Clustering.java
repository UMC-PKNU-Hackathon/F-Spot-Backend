package com.example.spot.model.clustering;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ComponentScan(basePackageClasses = SpringApplication.class)
public interface Clustering {
    Spots run(List<Marker> markers, double threshold);
}
