package com.example.spot.model.clustering;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class KMeansClusteringTest {
    @ParameterizedTest
    @MethodSource("getClusters")
    @DisplayName("임계값 안에 들어간다면, 모두 하나의 군집이 되어야 한다.")
    void 군집_통일_검사(List<Marker> markers) {
        //given
        Clustering clustering = new KMeansClustering();
        //when
        List<Spot> result = clustering.run(markers, 10000);
        //then
        assertThat(result).hasSize(1);

    }

    @ParameterizedTest
    @MethodSource("getDifferentType")
    @DisplayName("MapType이 다르다면, 다른 군집이 되어야한다.")
    void 군집_분리_검사(List<Marker> markers) {
        //given
        Clustering clustering = new KMeansClustering();
        //when
        List<Spot> result = clustering.run(markers, 10000);
        //then
        assertThat(result).hasSize(2);

    }

    public static Stream<Arguments> getClusters() {
        return Stream.of(
                Arguments.of(
                        List.of(new Marker(0L, 1.0, 2.0, TagType.TOUR),
                                new Marker(1L, 1.5, 2.0, TagType.TOUR),
                                new Marker(2L, .3, 2.0, TagType.TOUR))
                ),
                Arguments.of(
                        List.of(new Marker(0L, 1.0, 2.0, TagType.ACCIDENT),
                                new Marker(1L, 2.5, 3.0, TagType.ACCIDENT),
                                new Marker(2L, 2.3, 2.0, TagType.ACCIDENT))
                ));
    }

    public static Stream<Arguments> getDifferentType() {
        return Stream.of(
                Arguments.of(
                        List.of(new Marker(0L, 1.0, 2.0, TagType.TOUR),
                                new Marker(1L, 1.5, 2.0, TagType.ACCIDENT),
                                new Marker(2L, 1.3, 2.0, TagType.TOUR)),
                        List.of(new Marker(0L, 1.0, 2.0, TagType.ACCIDENT),
                                new Marker(3L, 2.5, 3.0, TagType.TOUR),
                                new Marker(4L, 2.3, 2.0, TagType.ACCIDENT))
                ));
    }
}