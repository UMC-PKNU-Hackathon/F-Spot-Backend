package com.example.spot.model.DTO;

import com.example.spot.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRes {
    private Long id;
    private Long boardId;
    private String nickname;
    private String body;
    private Timestamp createdAt;

}