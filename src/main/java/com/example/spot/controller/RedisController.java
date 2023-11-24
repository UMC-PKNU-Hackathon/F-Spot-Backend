package com.example.spot.controller;

import com.example.spot.config.exception.BaseException;
import com.example.spot.config.exception.BaseResponse;
import com.example.spot.model.DTO.BoardRes;
import com.example.spot.model.DTO.BoardResponse;
import com.example.spot.service.BoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.example.spot.config.exception.BaseResponseStatus.REQUEST_ERROR;
@Api(tags = "Sorting", description = "분류")
@RestController
public class RedisController {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private BoardService boardService;

    @ApiOperation(value="좋아요 분류", notes="좋아요순으로 분류해서 조회합니다.")
    @GetMapping("/sorting-request-like")
    public BaseResponse<List<BoardResponse>> getSortingKeyLike() {
        ZSetOperations<String, String> zop_like = redisTemplate.opsForZSet();

        Set<String> values = zop_like.range("mark-like", 0, -1);
        List<BoardResponse> Boards = new ArrayList<>();
        for (String boardIdString : values) {
            try {
                Long boardId = Long.parseLong(boardIdString);

                if (boardId == null)
                    return new BaseResponse<>(REQUEST_ERROR);


                BoardResponse board = boardService.getBoardByBoardId(boardId, boardId);
                Boards.add(board);

            } catch (BaseException e) {
                return new BaseResponse<>(e.getStatus());
            }
        }
        return new BaseResponse<>(Boards);
    }


    @ApiOperation(value="작성날짜 분류", notes="작성한 날짜순으로 분류해서 조회합니다.")
    @GetMapping("/sorting-request-date")
    public BaseResponse<List<BoardResponse>> getSortingKeyDate() {
        ZSetOperations<String, String> zop_date = redisTemplate.opsForZSet();

        Set<String> values = zop_date.range("mark-date", 0, -1);
        List<BoardResponse> Boards = new ArrayList<>();
        for (String boardIdString : values) {
            try {
                Long boardId = Long.parseLong(boardIdString);

                if (boardId == null)
                    return new BaseResponse<>(REQUEST_ERROR);


                BoardResponse board = boardService.getBoardByBoardId(boardId, boardId);
                Boards.add(board);

            } catch (BaseException e) {
                return new BaseResponse<>(e.getStatus());
            }
        }
        return new BaseResponse<>(Boards);
    }

    @ApiOperation(value="태그별 분류", notes="해당하는 태그만 분류해서 조회합니다.")
    @GetMapping("/sorting-request-tag/{tagname}")
    public BaseResponse<List<BoardResponse>> getSortingKeyDate(@PathVariable String tagname) {
        ZSetOperations<String, String> zop_date = redisTemplate.opsForZSet();

        Set<String> values = zop_date.range("mark-"+tagname, 0, -1);
        List<BoardResponse> Boards = new ArrayList<>();
        for (String boardIdString : values) {
            try {
                Long boardId = Long.parseLong(boardIdString);

                if (boardId == null)
                    return new BaseResponse<>(REQUEST_ERROR);


                BoardResponse board = boardService.getBoardByBoardId(boardId, boardId);
                Boards.add(board);

            } catch (BaseException e) {
                return new BaseResponse<>(e.getStatus());
            }
        }
        return new BaseResponse<>(Boards);
    }

}