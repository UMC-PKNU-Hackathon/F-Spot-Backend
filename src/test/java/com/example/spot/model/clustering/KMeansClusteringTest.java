package com.example.spot.model.clustering;

import com.example.spot.model.Location;
import com.example.spot.model.MapType;
import com.example.spot.model.Pin;
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
    void 군집_통일_검사(List<Pin> pins) {
        //given
        Clustering clustering = new KMeansClustering(pins, 100000);
        //when
        List<Pin> result = clustering.run(100L);
        //then
        assertThat(result).hasSize(1);

    }

    @ParameterizedTest
    @MethodSource("getDifferentType")
    @DisplayName("MapType이 다르다면, 다른 군집이 되어야한다.")
    void 군집_분리_검사(List<Pin> pins) {
        //given
        Clustering clustering = new KMeansClustering(pins, 100000);
        //when
        List<Pin> result = clustering.run(100L);
        //then
        assertThat(result).hasSize(2);

    }

    public static Stream<Arguments> getClusters() {
        return Stream.of(
                Arguments.of(
                        List.of(new Pin(new Location(1.0, 2.0), MapType.TOUR),
                                new Pin(new Location(1.5, 2.0), MapType.TOUR),
                                new Pin(new Location(1.3, 2.0), MapType.TOUR))
                ),
                Arguments.of(
                        List.of(new Pin(new Location(1.0, 2.0), MapType.ACCIDENT),
                                new Pin(new Location(2.5, 3.0), MapType.ACCIDENT),
                                new Pin(new Location(2.3, 2.0), MapType.ACCIDENT))
                ));
    }

    public static Stream<Arguments> getDifferentType() {
        return Stream.of(
                Arguments.of(
                        List.of(new Pin(new Location(1.0, 2.0), MapType.TOUR),
                                new Pin(new Location(1.5, 2.0), MapType.ACCIDENT),
                                new Pin(new Location(1.3, 2.0), MapType.TOUR))
                ),
                Arguments.of(
                        List.of(new Pin(new Location(1.0, 2.0), MapType.ACCIDENT),
                                new Pin(new Location(2.5, 3.0), MapType.TOUR),
                                new Pin(new Location(2.3, 2.0), MapType.ACCIDENT))
                ));
    }
}