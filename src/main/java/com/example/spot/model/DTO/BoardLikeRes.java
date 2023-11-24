package com.example.spot.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardLikeRes {

    // board
    private Long board_id;
    private String nickname; // board's writer
    private String content;
    private Integer hits;
    private Integer likeCnt;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private Long CheckLike;
}
