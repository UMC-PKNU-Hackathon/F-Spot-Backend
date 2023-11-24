package com.example.spot.controller;


import com.example.spot.config.exception.BaseException;
import com.example.spot.config.exception.BaseResponse;
import com.example.spot.model.DTO.BoardLikeRes;
import com.example.spot.service.LikeService;
import com.example.spot.utils.JwtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.example.spot.config.exception.BaseResponseStatus.REQUEST_ERROR;
import static com.example.spot.config.exception.BaseResponseStatus.USERS_EMPTY_USER_ID;

@RestController
@RequiredArgsConstructor
@Controller
@Api(tags = "Likes", description = "사용자 마커 좋아요(저장)")
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;
    private final JwtService jwtService;


    @GetMapping("/all-board")
    @ApiOperation(value = "모든 게시판 좋아요 조회", notes = "Board 좋아요 레시피(리스트) 출력 \n jwt 넣어주세요")
    @ApiResponses(value={@ApiResponse(code = 2010, message = "유저 아이디 값을 확인해주세요."),
            @ApiResponse(code = 3000,message = "값을 불러오는데 실패하였습니다."),
            @ApiResponse(code = 4000,message = "데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code = 4053,message = "게시판 좋아요 조회 실패")})
    public BaseResponse<List<BoardLikeRes>> getAllBoardLike() {
        try {
            Long idx = jwtService.getUserIdx();
            if (idx == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            List<BoardLikeRes> likes = likeService.getAllBoardLike();
            return new BaseResponse<>(likes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/users-board")
    @ApiOperation(value = "사용자가 저장한 모든 게시판 조회", notes = "Board 좋아요 리스트 출력 \n jwt 넣어주세요" )
    @ApiResponses(value={@ApiResponse(code = 2010, message = "유저 아이디 값을 확인해주세요."),
            @ApiResponse(code = 2011, message = "존재하지 않는 유저입니다."),
            @ApiResponse(code = 3000, message = "값을 불러오는데 실패하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code = 4053, message = "게시판 좋아요 조회 실패")})

    public BaseResponse<List<BoardLikeRes>> getBoardLikeByUserId() throws BaseException {
        try {
            Long idx = jwtService.getUserIdx();
            if (idx == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            List<BoardLikeRes> myBoardLikes = likeService.getBoardLike(idx);
            return new BaseResponse<>(myBoardLikes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    @PostMapping("/board/add")
    @ApiOperation(value = "Board 좋아요(저장)", notes = "사용자가 Board를 좋아요(저장)한다. \n jwt 넣어주세요")
    @ApiResponses(value={@ApiResponse(code = 2000, message = "입력값을 확인해주세요."),
            @ApiResponse(code = 2010, message = "유저 아이디 값을 확인해주세요."),
            @ApiResponse(code = 2011, message = "존재하지 않는 유저입니다."),
            @ApiResponse(code = 2061, message = "존재 하지 않거나 삭제된 게시글 입니다."),
            @ApiResponse(code = 2080, message = "좋아요 실패"),
            @ApiResponse(code = 2081, message = "이미 좋아요한 게시글입니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")})
    public BaseResponse<BoardLikeRes> addBoardLike(@RequestParam("boardId") Long boardId) {
        try {
            Long idx = jwtService.getUserIdx();

            if (idx == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }

            if (boardId == null) {
                return new BaseResponse<>(REQUEST_ERROR);
            }

            BoardLikeRes boardLikeRes = likeService.addBoardLike(idx, boardId);
            return new BaseResponse<>(boardLikeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    @GetMapping("/check-board-like")
    @ApiOperation(value = "사용자 Board 좋아요 확인", notes = "게시글 좋아요 여부 반환 \n jwt 넣어주세요")
    @ApiResponses(value={@ApiResponse(code = 2000, message = "입력값을 확인해주세요."),
            @ApiResponse(code = 2010, message = "유저 아이디 값을 확인해주세요."),
            @ApiResponse(code = 2061, message = "존재 하지 않거나 삭제된 게시글 입니다."),
            @ApiResponse(code = 2081, message = "좋아요가 존재 하지 않습니다."),
            @ApiResponse(code = 3000, message = "값을 불러오는데 실패하였습니다."),
            @ApiResponse(code =4000, message = "데이터베이스 연결에 실패하였습니다.")})
    public BaseResponse<Long> checkBoardLike(@RequestParam("boardId") Long boardId) {
        try {
            Long idx = jwtService.getUserIdx();
            if (idx == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            if (boardId == null) {
                return new BaseResponse<>(REQUEST_ERROR);
            }
            Long checkLike = likeService.checkBoardLike(idx, boardId);
            return new BaseResponse<>(checkLike);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


}
