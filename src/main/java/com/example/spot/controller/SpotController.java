package com.example.spot.controller;

import com.example.spot.model.DTO.SpotResponse;
import com.example.spot.model.clustering.TagType;
import com.example.spot.service.MarkerService;
import com.example.spot.service.SpotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spots")
public class SpotController {
    private final MarkerService markerService;
    private final SpotService spotService;

    @ResponseBody
    @GetMapping("/{tag}&&{level}")
    private void responseSpot(TagType tag, Integer level) {
        SpotResponse spotResponse = spotService.createSpotResponse(tag, level);
        ObjectMapper objectMapper = new ObjectMapper()
    }
}
