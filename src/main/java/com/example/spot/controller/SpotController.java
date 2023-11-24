package com.example.spot.controller;

import com.example.spot.config.exception.BaseException;
import com.example.spot.config.exception.BaseResponse;
import com.example.spot.model.DTO.BoardResponse;
import com.example.spot.model.DTO.Spot;
import com.example.spot.service.BoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "군집화", description = "군집화 진짜 합니다.")
@RestController
public class SpotController {

    private static final List<Long> SPOT_SECTION = List.of(1000L, 3000L, 5000L, 7000L, 10000L);

    @Autowired
    BoardService boardService;


    @ApiOperation(value = "Spot 표시", notes = "Mark가 밀집된 구역을 찾아서 Spot을 형성합니다. Spot의 중심점과 반지름을 반환합니다.")
    @GetMapping("/GetSpots/{level}")
    public BaseResponse<List<Spot>> getBoardsByLevel(@PathVariable Long level) {
        List<Spot> clusterCenters = new ArrayList<>();
        List<Spot> spotCenters = new ArrayList<>();
        Long curLevel = SPOT_SECTION.get(level.intValue());
        try {

            List<BoardResponse> boards = boardService.getBoards();

            for (BoardResponse board : boards) {
                Spot spot = new Spot();
                spot.setLatitude(board.getLatitude());
                spot.setLongitude(board.getLongitude());
                spotCenters.add(spot);
            }

            List<Spot> filteredSpots = filterSpots(spotCenters, curLevel);
            if (!filteredSpots.isEmpty()) {
                Spot averageSpot = calculateAverageSpot(filteredSpots);
                clusterCenters.add(averageSpot);
            }
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
        return new BaseResponse<>(clusterCenters);
    }

    private List<Spot> filterSpots(List<Spot> spots, Long curLevel) {
        List<Spot> filteredSpots = new ArrayList<>();

        for (Spot spot1 : spots) {
            int count = 0;

            for (Spot spot2 : spots) {

                if (spot1.equals(spot2)) {
                    continue;
                }

                if (Math.abs(spot1.getLatitude() - spot2.getLatitude()) < curLevel / 1000 &&
                        Math.abs(spot1.getLongitude() - spot2.getLongitude()) < curLevel * 1000) {
                    count++;
                }
            }
            if (count > curLevel / 1000) {
                filteredSpots.add(spot1);
            }
        }

        return filteredSpots;
    }


    private Spot calculateAverageSpot(List<Spot> spots) {
        double totalLatitude = 0;
        double totalLongitude = 0;

        for (Spot spot : spots) {
            totalLatitude += spot.getLatitude();
            totalLongitude += spot.getLongitude();
        }

        double averageLatitude = totalLatitude / spots.size();
        double averageLongitude = totalLongitude / spots.size();

        return new Spot(averageLongitude, averageLatitude);
    }
}
