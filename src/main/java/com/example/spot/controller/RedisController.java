package com.example.spot.controller;

import com.example.spot.config.exception.BaseResponse;
import com.example.spot.model.DTO.BoardRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class RedisController {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private BoardController boardController;

    @GetMapping("/sorting-request-like")
    public ResponseEntity<?> getSortingKeyLike() {
        ZSetOperations<String, String> zop_like = redisTemplate.opsForZSet();

        Set<String> values = zop_like.range("mark-like", 0, -1);

        for (String boardIdString : values) {
            try {
                Long boardId = Long.parseLong(boardIdString);
                ResponseEntity<BaseResponse<BoardRes>> responseEntity = boardController.getBoardByBoardId(boardId);

                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    boardList.add(responseEntity.getBody().getData());
                } else {
                    return new ResponseEntity<>("boardId : HttpStatus Bad", HttpStatus.NOT_FOUND);
                }
            } catch (NumberFormatException e) {
                return new ResponseEntity<>("boardId : 유효한 형식이 아님", HttpStatus.NOT_FOUND);
            }
        }

        if (!values.isEmpty())
            return new ResponseEntity<>(values, HttpStatus.OK);
        else
            return new ResponseEntity<>("No member in redis.", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/sorting-request-date")
    public ResponseEntity<?> getSortingKeyDate() {
        ZSetOperations<String, String> zop_date = redisTemplate.opsForZSet();

        Set<String> values = zop_date.range("mark-date", 0, -1);

        for (String boardIdString : values) {
            try {
                Long boardId = Long.parseLong(boardIdString);
                ResponseEntity<BaseResponse<BoardRes>> responseEntity = boardController.getBoardByBoardId(boardId);

                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    boardList.add(responseEntity.getBody().getData());
                } else {
                    return new ResponseEntity<>("boardId : HttpStatus Bad", HttpStatus.NOT_FOUND);
                }
            } catch (NumberFormatException e) {
                return new ResponseEntity<>("boardId : 유효한 형식이 아님", HttpStatus.NOT_FOUND);
            }
        }

        if (!values.isEmpty())
            return new ResponseEntity<>(values, HttpStatus.OK);
        else
            return new ResponseEntity<>("No member in redis.", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/sorting-request-tag")
    public ResponseEntity<?> getSortingKeyDate(@PathVariable String tagname) {
        ZSetOperations<String, String> zop_date = redisTemplate.opsForZSet();

        Set<String> values = zop_date.range("mark-"+tagname, 0, -1);

        for (String boardIdString : values) {
            try {
                Long boardId = Long.parseLong(boardIdString);
                ResponseEntity<BaseResponse<BoardRes>> responseEntity = boardController.getBoardByBoardId(boardId);

                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    boardController.boardList.add(responseEntity.getBody().getData());
                } else {
                    return new ResponseEntity<>("boardId : HttpStatus Bad", HttpStatus.NOT_FOUND);
                }
            } catch (NumberFormatException e) {
                return new ResponseEntity<>("boardId : 유효한 형식이 아님", HttpStatus.NOT_FOUND);
            }
        }

        if (!values.isEmpty())
            return new ResponseEntity<>(values, HttpStatus.OK);
        else
            return new ResponseEntity<>("No member in redis.", HttpStatus.NOT_FOUND);
    }

}