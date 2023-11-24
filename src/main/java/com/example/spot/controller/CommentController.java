package com.example.spot.controller;

import com.example.spot.config.exception.BaseException;
import com.example.spot.config.exception.BaseResponse;
import com.example.spot.model.DTO.CommentReq;
import com.example.spot.model.DTO.CommentRes;
import com.example.spot.service.CommentService;
import com.example.spot.utils.JwtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.spot.config.exception.BaseResponseStatus.*;

@RestController
@RequiredArgsConstructor
@Controller
@Api(tags = "Comment", description = "댓글")
@RequestMapping("/comment")
public class CommentController {

    private final JwtService jwtService;
    private final CommentService commentService;

    /**
     * 게시판 댓글 조회 api
     * [GET] /comment/boardId={boardId}
     *
     * @Return BaseResponse<List>
     */
    @GetMapping("/boardId={boardId}")
    @ApiOperation(value="게시판 댓글 조회", notes="boardId로 게시글의 댓글을 조회한다. \n headers = {\"X-ACCESS-TOKEN\": jwt}; 설정해주기(jwt는 로그인하면 반환되는 jwt이다.")
    @ApiResponses(value={@ApiResponse(code = 2000, message = "입력값을 확인해주세요."),
            @ApiResponse(code = 2010,message = "유저 아이디 값을 확인해주세요."),
            @ApiResponse(code = 2061,message = "존재 하지 않거나 삭제된 게시글 입니다."),
            @ApiResponse(code = 3000,message = "값을 불러오는데 실패하였습니다."),
            @ApiResponse(code = 4000,message = "데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<List<CommentRes>> getCommentByBoardId(@PathVariable Long boardId) {
        if (boardId == null) {
            return new BaseResponse<>(REQUEST_ERROR);
        }
        try {
            Long idx = jwtService.getUserIdx();
            if (idx == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            List<CommentRes> commentRes = commentService.getComments(boardId);
            return new BaseResponse<>(commentRes);
        } catch (Exception exception) {
            return new BaseResponse<>(RESPONSE_ERROR);
        }
    }

    /**
     * 나의 댓글 조회 api
     * [GET] /comment/mycomment
     *
     * @Return BaseResponse<List>
     */
    @GetMapping("/mycomment")
    @ApiOperation(value="나의 댓글 조회", notes="내가 작성한 게시글의 댓글을 조회한다. \n headers = {\"X-ACCESS-TOKEN\": jwt}; 설정해주기(jwt는 로그인하면 반환되는 jwt이다.")
    @ApiResponses(value={@ApiResponse(code = 2010, message = "유저 아이디 값을 확인해주세요."),
            @ApiResponse(code = 2011, message = "존재하지 않는 유저입니다."),
            @ApiResponse(code = 3000, message = "값을 불러오는데 실패하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code = 4043, message = "레시피 좋아요 조회 실패")})
    public BaseResponse<List<CommentRes>> getMyCommentById() {

        try {
            Long idx = jwtService.getUserIdx();
            if (idx == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            List<CommentRes> commentRes = commentService.getMyCommentById(idx);
            return new BaseResponse<>(commentRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 게시글 댓글 추가 api
     * [POST] /comment/add
     *
     * @return BaseResponse<CommentRes>
     */
    @ApiOperation(value="게시판 댓글 추가", notes="게시글의 댓글을 추가한다. \n headers = {\"X-ACCESS-TOKEN\": jwt}; 설정해주기(jwt는 로그인하면 반환되는 jwt이다.")
    @ApiResponses(value={@ApiResponse(code = 2010, message = "유저 아이디 값을 확인해주세요."),
            @ApiResponse(code = 2011, message = "존재하지 않는 유저입니다."),
            @ApiResponse(code = 2070, message = "댓글 등록 실패"),
            @ApiResponse(code = 3000, message = "값을 불러오는데 실패하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")})
    @PostMapping("/add")
    public BaseResponse<CommentRes> addComments(@RequestParam("boardId") Long boardId, @RequestBody CommentReq req) {
        try {
            Long idx = jwtService.getUserIdx();
            if (idx == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            if (req == null) {
                return new BaseResponse<>(REQUEST_ERROR);
            }
            CommentRes commentRes = commentService.addComment(boardId, req, idx);
            return new BaseResponse<>(commentRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 게시글 댓글 수정 api
     * [PATCH] /comment/update
     *
     * @return BaseResponse<CommentRes>
     */
    @PatchMapping("/update")
    @ApiOperation(value = "게시판 댓글 수정", notes = "댓글을 수정한다. \n headers = {\"X-ACCESS-TOKEN\": jwt}; 설정해주기(jwt는 로그인하면 반환되는 jwt이다.")
    @ApiResponses(value={@ApiResponse(code = 2003, message = "권한이 없는 유저의 접근입니다."),
            @ApiResponse(code = 2010, message = "유저 아이디 값을 확인해주세요."),
            @ApiResponse(code = 3000, message = "값을 불러오는데 실패하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code = 4041, message = "댓글 수정 실패")
    })
    public BaseResponse<CommentRes> updateComment(@RequestParam("commentId") Long commentId, CommentReq req) {
        try {
            Long idx = jwtService.getUserIdx();
            if (idx == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            CommentRes commentRes = commentService.updateComment(commentId, req.getBody(), idx);
            return new BaseResponse<>(commentRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 게시판 댓글 삭제 api
     * [DELETE] /comment/delete
     *
     * @Return BaseResponse<String>
     */
    @DeleteMapping("/delete")
    @ApiOperation(value = "게시판 댓글 삭제", notes = "댓글을 삭제한다. \n headers = {\"X-ACCESS-TOKEN\": jwt}; 설정해주기(jwt는 로그인하면 반환되는 jwt이다.")
    @ApiResponses(value={@ApiResponse(code = 2003, message = "권한이 없는 유저의 접근입니다."),
            @ApiResponse(code = 2010, message = "유저 아이디 값을 확인해주세요."),
            @ApiResponse(code = 2011, message = "존재하지 않는 유저입니다."),
            @ApiResponse(code = 2071, message = "존재 하지 않거나 삭제된 댓글 입니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code = 4042, message = "댓글 삭제 실패")
    })
    public BaseResponse<String> deleteComment(@RequestParam("commentId") Long commentId) {
        try {
            Long idx = jwtService.getUserIdx();
            if (idx == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            Long boardId = commentService.deleteComment(commentId, idx);
            return new BaseResponse<>(boardId + "번 게시글의 댓글 삭제 완료 !");
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


}
