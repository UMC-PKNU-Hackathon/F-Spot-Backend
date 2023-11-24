package com.example.spot.model.DTO;

import com.example.spot.model.clustering.Spots;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpotResponse {
    private Spots spots;
}
