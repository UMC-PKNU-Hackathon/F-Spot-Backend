package com.example.spot.model.DTO;

import com.example.spot.model.Board;
import com.example.spot.model.BoardImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardRes {
    private Long boardId;
    private String nickname;
    private String content;
    private Integer hits;
    private Integer likeCnt;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    private Double latitude;
    private Double longitude;

    private List<String> boardImageUrl; //이미지

}
