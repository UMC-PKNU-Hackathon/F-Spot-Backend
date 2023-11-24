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
    private BoardController boardController;
    @Autowired
    private BoardService boardService;

    @ApiOperation(value="모든 게시글 조회", notes="모든 게시글(리스트)을 조회한다.")
    @ApiResponses(value={@ApiResponse(code = 3000, message = "값을 불러오는데 실패하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code = 4023, message = "게시판 조회 실패")})
    @GetMapping("/sorting-request-like")
    public BaseResponse<BoardResponse> getSortingKeyLike() {
        ZSetOperations<String, String> zop_like = redisTemplate.opsForZSet();

        Set<String> values = zop_like.range("mark-like", 0, -1);

        for (String boardIdString : values) {
            try {
                Long boardId = Long.parseLong(boardIdString);
                BaseResponse<BoardResponse> responseEntity = boardController.getBoardByBoardId(boardId);

                if (boardId == null) {
                    return new BaseResponse<>(REQUEST_ERROR);
                }

                BoardResponse board = boardService.getBoardByBoardId(boardId, boardId);
                return new BaseResponse<>(board);
            } catch (BaseException e) {
                return new BaseResponse<>(e.getStatus());
            }
        }
        return new BaseResponse<>(null);
    }



    @GetMapping("/sorting-request-date")
    public BaseResponse<BoardResponse> getSortingKeyDate() {
        ZSetOperations<String, String> zop_date = redisTemplate.opsForZSet();

        Set<String> values = zop_date.range("mark-date", 0, -1);
            for (String boardIdString : values) {
                try {
                    Long boardId = Long.parseLong(boardIdString);
                    BaseResponse<BoardResponse> responseEntity = boardController.getBoardByBoardId(boardId);

                    if (boardId == null) {
                        return new BaseResponse<>(REQUEST_ERROR);
                    }

                    BoardResponse board = boardService.getBoardByBoardId(boardId, boardId);
                    return new BaseResponse<>(board);
                } catch (BaseException e) {
                    return new BaseResponse<>(e.getStatus());
                }
            }
        return new BaseResponse<>(null);
        }

    @GetMapping("/sorting-request-tag")
    public BaseResponse<BoardResponse> getSortingKeyDate(@PathVariable String tagname) {
        ZSetOperations<String, String> zop_date = redisTemplate.opsForZSet();

        Set<String> values = zop_date.range("mark-"+tagname, 0, -1);

        for (String boardIdString : values) {
            try {
                Long boardId = Long.parseLong(boardIdString);
                BaseResponse<BoardResponse> responseEntity = boardController.getBoardByBoardId(boardId);

                if (boardId == null) {
                    return new BaseResponse<>(REQUEST_ERROR);
                }

                BoardResponse board = boardService.getBoardByBoardId(boardId, boardId);
                return new BaseResponse<>(board);
            } catch (BaseException e) {
                return new BaseResponse<>(e.getStatus());
            }
        }
        return new BaseResponse<>(null);
    }

}