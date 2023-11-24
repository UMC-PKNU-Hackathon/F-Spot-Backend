package com.example.spot.model.DTO;

import com.example.spot.model.Board;
import com.example.spot.model.Comment;
import com.example.spot.model.User;
import lombok.Data;

@Data
public class CommentReq {

    private String body;

    public Comment toEntity(Board board, User user) {
        return Comment.builder()
                .user(user)
                .board(board)
                .body(body)
                .build();
    }

}
