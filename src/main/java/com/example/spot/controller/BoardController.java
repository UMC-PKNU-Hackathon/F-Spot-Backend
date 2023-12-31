package com.example.spot.controller;

import com.example.spot.config.exception.BaseException;
import com.example.spot.config.exception.BaseResponse;
import com.example.spot.model.DTO.BoardRes;
import com.example.spot.model.DTO.BoardResponse;
import com.example.spot.service.BoardService;
import com.example.spot.utils.JwtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.Date;


import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.spot.config.exception.BaseResponseStatus.*;


@RestController
@RequiredArgsConstructor
@Controller
@Api(tags = "Board", description = "마커")
@RequestMapping("/board")
public class BoardController {


    private final BoardService boardService;
    private final JwtService jwtService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 모든 게시글(리스트) 조회 api
     * [GET] /board/list
     *
     * @return BaseResponse<List>
     */
    @GetMapping("/list")
    @ApiOperation(value="모든 게시글 조회", notes="모든 게시글(리스트)을 조회한다.")
    @ApiResponses(value={@ApiResponse(code = 3000, message = "값을 불러오는데 실패하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code = 4023, message = "게시판 조회 실패")})
    public BaseResponse<List<BoardResponse>> getBoards() {
        try {
            List<BoardResponse> boards = boardService.getBoards();
            return new BaseResponse<>(boards);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 나의 게시글 조회 api
     * [GET] /board/myboard?
     *
     * @return BaseResponse<List>
     */
    @GetMapping("/myboard")
    @ApiOperation(value="나의 게시글 조회", notes="내가 작성한 게시글(리스트)을 조회한다.")
    @ApiResponses(value={@ApiResponse(code = 2010, message = "유저 아이디 값을 확인해주세요."),
            @ApiResponse(code = 2011, message = "존재하지 않는 유저입니다."),
            @ApiResponse(code = 3000, message = "값을 불러오는데 실패하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code = 4023, message = "게시판 조회 실패")})
    public BaseResponse<List<BoardResponse>> getMyBoards() {
        try {
            Long idx = jwtService.getUserIdx();
            if (idx == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            List<BoardResponse> myBoards = boardService.getMyBoards(idx);
            return new BaseResponse<>(myBoards);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * boardId로 게시글 조회 api
     * [GET] /board/boardId={boardId}
     *
     * @return BaseResponse<Board> or <BoardRes>
     */
    @GetMapping("/{boardId}")
    @ApiOperation(value="게시글 조회", notes="boardId로 게시글을 조회한다. \n headers = {\"X-ACCESS-TOKEN\": jwt}; 설정해주기(jwt는 로그인하면 반환되는 jwt이다.")
    @ApiResponses(value={@ApiResponse(code = 2000, message = "입력값을 확인해주세요."),
            @ApiResponse(code = 2010, message = "유저 아이디 값을 확인해주세요."),
            @ApiResponse(code = 2011, message = "존재하지 않는 유저입니다."),
            @ApiResponse(code = 2061, message = "존재 하지 않거나 삭제된 게시글 입니다."),
            @ApiResponse(code = 3000, message = "값을 불러오는데 실패하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
    public BaseResponse<BoardResponse> getBoardByBoardId(@PathVariable Long boardId) {
        try {
            Long idx = jwtService.getUserIdx();

            if (boardId == null) {
                return new BaseResponse<>(REQUEST_ERROR);
            }

            BoardResponse board = boardService.getBoardByBoardId(boardId, idx);

            return new BaseResponse<>(board);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // ==================================================================================

    /**
     * 게시글 정렬 api
     * [GET] /board/
     *
     * @return BaseResponse<List>
     */
    /* 정렬 */
    @GetMapping("/list-order-by-hits")
    @ApiOperation(value="게시판 조건 정렬 API", notes="조회수 높은 순 게시판 정렬")
    @ApiResponses(value={@ApiResponse(code = 3000, message = "값을 불러오는데 실패하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code = 4023, message = "게시판 조회 실패")})
    public BaseResponse<List<BoardResponse>> getBoardByOrderByHitsDesc() {
        try {
            List<BoardResponse> getBoardRes  = boardService.getBoardByOrderByHitsDesc();
            return new BaseResponse<>(getBoardRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 키워드(in 작성자) 리스트 검색 api
     * 키워드(in 제목) 리스트 검색 api
     * 키워드(in 본문) 리스트 검색 api
     * [GET] /board/search-board-of?keyword={keyword}
     *
     *  @return BaseResponse<List>
     */
    /* 검색 */
    @GetMapping("/search-board-of-writer")
    @ApiOperation(value="작성자 검색", notes="작성자로 게시글을 검색한다.")
    @ApiResponses(value={@ApiResponse(code = 2000, message = "입력값을 확인해주세요."),
            @ApiResponse(code = 3000, message = "값을 불러오는데 실패하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code = 4023, message = "게시판 조회 실패")})
    public BaseResponse<List<BoardResponse>> getBoardByUserId(@RequestParam("keyword") String keyword) {
        try {
            if (keyword == null) {
                return new BaseResponse<>(REQUEST_ERROR);
            }
            List<BoardResponse> boardList = boardService.searchBoardByUserNickname(keyword);

            return new BaseResponse<>(boardList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/search-board-of-content")
    @ApiOperation(value = "본문 검색", notes = "본문으로 게시글을 검색한다.")
    @ApiResponses(value={@ApiResponse(code = 2000, message = "입력값을 확인해주세요."),
            @ApiResponse(code = 3000, message = "값을 불러오는데 실패하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code = 4023, message = "게시판 조회 실패")})
    public BaseResponse<List<BoardResponse>> searchBoardByContent(@RequestParam("keyword") String keyword) {
        try {
            if (keyword == null) {
                return new BaseResponse<>(REQUEST_ERROR);
            }
            List<BoardResponse> boardList  = boardService.searchBoardByContent(keyword);

            return new BaseResponse<>(boardList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    // ==================================================================================

    /**
     * 게시글 추가 api
     * [POST] /board/add
     *
     * @return BaseResponse<BoardRes>
     */
    @PostMapping("/add")
    @ApiOperation(value = "게시글 등록", notes = "게시글을 등록한다. \n headers = {\"X-ACCESS-TOKEN\": jwt}; 설정해주기(jwt는 로그인하면 반환되는 jwt이다. \n Body에서 files, title, content 입력하면 됨")
    @ApiResponses(value={@ApiResponse(code = 2010, message = "유저 아이디 값을 확인해주세요."),
            @ApiResponse(code = 2011, message = "존재하지 않는 유저입니다."),
            @ApiResponse(code = 2041, message = "위도를 입력해주세요."),
            @ApiResponse(code = 2042, message = "경도를 입력해주세요."),
            @ApiResponse(code = 2060, message = "게시글 등록을 실패하였습니다."),
            @ApiResponse(code = 2061, message = "존재 하지 않거나 삭제된 게시글 입니다."),
            @ApiResponse(code = 2063, message = "내용을 입력해주세요."),
            @ApiResponse(code = 2065, message = "파일 등록을 실패하였습니다."),
            @ApiResponse(code = 2066, message = "존재 하지 않거나 삭제된 파일 입니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")})
    public BaseResponse<BoardRes> add( @RequestParam String content, @RequestParam Double latitude,  @RequestParam Double longitude,@RequestParam(value = "tags") List<String> tags, @RequestPart(value = "images", required = false) List<MultipartFile> images)
    {
        try {
            Long idx = jwtService.getUserIdx();

            if (idx == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }

            // 이미지 개수 체크
            if (images != null && images.size() > 3) {
                return new BaseResponse<>(false, "이미지는 최대 3개까지만 등록할 수 있습니다.", 6000, null);
            }

            if (content == null || content.equals("")){
                return new BaseResponse<>(POST_BOARD_EMPTY_CONTENT);
            }

            if (latitude == null || latitude.equals("")){
                return new BaseResponse<>(POST_LATITUDE_EMPTY);
            }

            if (longitude == null || longitude.equals("")){
                return new BaseResponse<>(POST_LONGITUDE_EMPTY);
            }

            if (tags.size() == 0){
                return new BaseResponse<>(POST_TAGS_EMPTY);
            }

            BoardRes boardRes = boardService.add(content, images, idx, latitude, longitude, tags);

            Date createdAt = boardRes.getCreatedAt();
            double timestamp = (double) createdAt.getTime() / 1000.0;
            ZSetOperations<String, String> zopDate = redisTemplate.opsForZSet();
            zopDate.add("mark-date", boardRes.getBoardId().toString(), timestamp);
            redisTemplate.expire(boardRes.getBoardId().toString(), 1, TimeUnit.MINUTES);

            ZSetOperations<String, String> zopLike = redisTemplate.opsForZSet();
            zopLike.add("mark-like", boardRes.getBoardId().toString(), timestamp);
            redisTemplate.expire(boardRes.getBoardId().toString(), 1, TimeUnit.MINUTES);

            List<String> Tags = boardRes.getTags();
            ZSetOperations<String, String> zopTag = redisTemplate.opsForZSet();
            zopLike.add("mark-"+Tags.get(0), boardRes.getBoardId().toString(), timestamp);
            redisTemplate.expire(boardRes.getBoardId().toString(), 1, TimeUnit.MINUTES);


            return new BaseResponse<>(boardRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 게시글 수정 api
     * [PATCH] /board/update
     *
     * @return BaseResponse<BoardRes>
     */
    @PatchMapping("/update")
    @ApiOperation(value = "게시글 수정", notes = "게시글을 수정한다. \n headers = {\"X-ACCESS-TOKEN\": jwt}; 설정해주기(jwt는 로그인하면 반환되는 jwt이다.")
    @ApiResponses(value={@ApiResponse(code = 2010,message = "유저 아이디 값을 확인해주세요."),
            @ApiResponse(code = 2061, message = "존재 하지 않거나 삭제된 게시글 입니다."),
            @ApiResponse(code = 2066, message = "존재 하지 않거나 삭제된 파일 입니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code = 4021, message = "게시판 수정 실패"),
            @ApiResponse(code = 4031, message = "파일 수정 실패")})
    public BaseResponse<BoardRes> updateBoard(@RequestParam("boardId") Long boardId,@RequestParam String content,@RequestParam Double latitude,
                                              @RequestParam Double longitude, @RequestParam(value = "tags") List<String> tags,
                                              @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            Long idx = jwtService.getUserIdx();

            if (idx == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }

            // 이미지 개수 체크
            if (images != null && images.size() > 3) {
                return new BaseResponse<>(false, "이미지는 최대 3개까지만 등록할 수 있습니다.", 6000, null);
            }

            BoardRes boardRes = boardService.updateBoard(boardId, content, images, latitude, longitude, tags);

            return new BaseResponse<>(boardRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 게시글 삭제 api
     * [DELETE] /board/delete
     *
     * @return BaseResponse<String>
     */
    @DeleteMapping("/delete")
    @ApiOperation(value = "게시글 삭제", notes = "게시글을 삭제한다. \n headers = {\"X-ACCESS-TOKEN\": jwt}; 설정해주기(jwt는 로그인하면 반환되는 jwt이다.")
    @ApiResponses(value={@ApiResponse(code = 2010, message = "유저 아이디 값을 확인해주세요."),
            @ApiResponse(code = 2063, message = "존재 하지 않거나 삭제된 게시글 입니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")})
    public BaseResponse<String> deleteBoard(@RequestParam("boardId") Long boardId) {
        try {
            Long idx = jwtService.getUserIdx();
            if (idx == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            boardService.deleteBoard(boardId);
            return new BaseResponse<>(boardId + "번 게시글 삭제 완료 !");
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    //저장된 이미지 조회
    //이미지 네임을 알 수 있음 그 거를 기반으로 이미지 조회하기
    //http://localhost:8080/board/images/7f658d91-ef68-4b59-a381-af5bc9938768_fighting.png
    @ResponseBody
    @GetMapping("/images/{imageName}")
    @ApiOperation(value="리뷰 이미지 조회", notes="이미지 조회할 때 url을 여기다가 붙여서 get 보내셈 \n 근데, localhost를 (?)으로 변경해야함!")
    public ResponseEntity<byte[]> getReviewImage(@PathVariable String imageName) {
        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\templates\\image\\";
        String imagePath = projectPath + imageName;

        try {
            FileInputStream imageStream = new FileInputStream(imagePath);
            byte[] imageBytes = imageStream.readAllBytes();
            imageStream.close();

            String contentType = determineContentType(imageName); // 이미지 파일 확장자에 따라 MIME 타입 결정

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String determineContentType(String imageName) {
        String extension = FilenameUtils.getExtension(imageName); // Commons IO 라이브러리 사용
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            // 다른 이미지 타입 추가 가능
            default:
                return "application/octet-stream"; // 기본적으로 이진 파일로 다룸
        }
    }


}
