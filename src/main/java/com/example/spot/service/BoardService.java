package com.example.spot.service;

import com.example.spot.config.exception.BaseException;
import com.example.spot.model.Board;
import com.example.spot.model.BoardImage;
import com.example.spot.model.DTO.BoardRes;
import com.example.spot.model.User;
import com.example.spot.repository.BoardImageRepository;
import com.example.spot.repository.BoardRepository;
import com.example.spot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.spot.config.exception.BaseResponseStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    /**
     * 조회 - get
     * 검색 - search(Controller & Service) / find(Repository)
     * 생성 - save
     * 수정 - update
     * 삭제 - delete
     */

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardImageRepository boardImageRepository;


//    /* 모든 게시글(리스트) 조회 */
//    public List<BoardRes> getBoards() throws BaseException {
//        try {
//            List<BoardRes> boards =
//                    boardRepository.findAll()
//                            .stream()
//                            .map(BoardRes::new)
//                            .collect(Collectors.toList());
//
//            if (boards.isEmpty()) {
//                throw new BaseException(SHOW_FAIL_BOARD);
//            }
//            return boards;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    /* 나의 게시글 조회 */
//    public List<BoardRes> getMyBoards(Long idx) throws BaseException {
//        try {
//            Optional<User> userOptional = userRepository.findById(idx);
//
//            if (userOptional.isPresent()) {
//                User user = userOptional.get();
//                List<BoardRes> myBoards =
//                        boardRepository.findBoardByUser(user)
//                                .stream()
//                                .map(BoardRes::new)
//                                .toList();
//
//                if (myBoards.isEmpty()) {
//                    throw new BaseException(SHOW_FAIL_BOARD);
//                }
//
//                return myBoards;
//            } else {
//                // 사용자를 찾지 못한 경우 에러 처리
//                throw new BaseException(USERS_NOT_EXISTS);
//            }
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    /* boardId로 게시글 조회 */
//    public Board getBoardByBoardId(Long boardId, Long idx) throws BaseException {
//        try {
//            Optional<Board> findBoard = this.boardRepository.findByBoardId(boardId);
//            if (findBoard.isPresent()) {
//                Board board = findBoard.get();
//                if (!idx.equals(findBoard.get().getUser().getId())) {
//                    board.setHits(board.getHits() + 1); // 조회수 증가
//                }
//                this.boardRepository.save(board);
//                return board;
//            } else {
//                throw new BaseException(BOARD_NOT_EXISTS);
//            }
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    /* 게시글 정렬 */
//    public List<BoardRes> getBoardByOrderByHitsDesc() throws BaseException { // 조회 수
//        try {
//            List<BoardRes> getBoards =
//                    boardRepository.findAllByOrderByHitsDesc()
//                            .stream()
//                            .map(BoardRes::new)
//                            .toList();
//
//            if (getBoards.isEmpty()) {
//                throw new BaseException(SHOW_FAIL_BOARD);
//            }
//            return getBoards;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public List<BoardRes> getBoardByOrderByLikeCntDesc() throws BaseException { // 좋아요
//        try {
//            List<BoardRes> getBoards =
//                    boardRepository.findAllByOrderByLikeCntDesc()
//                            .stream()
//                            .map(BoardRes::new)
//                            .toList();
//
//            if (getBoards.isEmpty()) {
//                throw new BaseException(SHOW_FAIL_BOARD);
//            }
//            return getBoards;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//
//    /* 게시글 검색 */
//    public List<BoardRes> searchBoardByUserNickname(String writer) throws BaseException {
//        try {
//            List<BoardRes> boardList = boardRepository.findByUserNicknameContaining(writer)
//                    .stream()
//                    .map(BoardRes::new)
//                    .toList();
//
//            if (boardList.isEmpty()) {
//                throw new BaseException(SHOW_FAIL_BOARD);
//            }
//            return boardList;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public List<BoardRes> searchBoardByContent(String keyword) throws BaseException {
//        try {
//            List<BoardRes> boardList = boardRepository.findByContentContaining(keyword)
//                    .stream()
//                    .map(BoardRes::new)
//                    .toList();
//
//            if (boardList.isEmpty()) {
//                throw new BaseException(SHOW_FAIL_BOARD);
//            }
//            return boardList;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }


    // ==================================================================================


    /* 게시글 추가 */
    public BoardRes add(String content, List<MultipartFile> images, Long idx, Double latitude, Double longitude, List<String> tags) throws BaseException {
        try {
            Optional<User> optUser = userRepository.findById(idx);

            if (optUser.isPresent()) {
                User user = optUser.get();

                Board board = new Board();
                board.setContent(content);
                board.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                board.setUser(user);
                board.setLatitude(latitude);
                board.setLongitude(longitude);
                board.setTags(tags);

                if (images != null) {
                    for (MultipartFile imageFile : images) {

                        List<BoardImage> savedImages = saveImages(Collections.singletonList(imageFile),board);
                        board.getBoardImage().addAll(savedImages);
                    }
                }

                board.fillBoardImageUrl();
                boardRepository.save(board);

                BoardRes boardRes = new BoardRes();
                boardRes.setBoardId(board.getBoardId());
                boardRes.setNickname(board.getUser().getNickname());
                boardRes.setContent(board.getContent());
                boardRes.setHits(board.getHits());
                boardRes.setLikeCnt(board.getLikeCnt());
                boardRes.setCreatedAt(board.getCreatedAt());
                boardRes.setUpdatedAt(board.getUpdatedAt());
                boardRes.setBoardImageUrl(board.getBoardImageUrl());
                boardRes.setLatitude(board.getLatitude());
                boardRes.setLongitude(board.getLongitude());
                boardRes.setTags(board.getTags());

                return boardRes;

            } else {
                throw new BaseException(USERS_NOT_EXISTS);
            }

        } catch (BaseException exception) {
            if(exception.getStatus().equals(USERS_NOT_EXISTS)){
                throw exception;
            } else {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    /* 게시글 수정 */
    @Transactional
    public BoardRes updateBoard(Long boardId, String content, List<MultipartFile> images, Double latitude, Double longitude, List<String> tags) throws BaseException {

        try {
            Optional<Board> findBoard = boardRepository.findById(boardId);
            if (findBoard.isPresent()) {
                Board board = findBoard.get();

                if (content != null || content.equals("")) { // 내용 변경
                    board.setContent(content);
                }

                if (latitude != null ) {
                    board.setLatitude(latitude);
                }

                if (longitude != null) {
                    board.setLongitude(longitude);
                }

                if (tags.size() != 0) {
                    board.setTags(tags);
                }

                if (images != null) {

                    for (MultipartFile imageFile : images) {

                        List<BoardImage> savedImages = saveImagesFix(Collections.singletonList(imageFile),board);
                        board.getBoardImage().addAll(savedImages);
                    }

                }

                board.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                board.fillBoardImageUrl();
                boardRepository.save(board);

                BoardRes boardRes = new BoardRes();
                boardRes.setBoardId(board.getBoardId());
                boardRes.setNickname(board.getUser().getNickname());
                boardRes.setContent(board.getContent());
                boardRes.setHits(board.getHits());
                boardRes.setLikeCnt(board.getLikeCnt());
                boardRes.setCreatedAt(board.getCreatedAt());
                boardRes.setUpdatedAt(board.getUpdatedAt());
                boardRes.setBoardImageUrl(board.getBoardImageUrl());
                boardRes.setLatitude(board.getLatitude());
                boardRes.setLongitude(board.getLongitude());
                boardRes.setTags(board.getTags());

                return boardRes;

            } else {
                throw new BaseException(BOARD_NOT_EXISTS);
            }
        } catch (BaseException exception) {
            if(exception.getStatus().equals(BOARD_NOT_EXISTS)){
                throw exception;
            } else {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    /* 게시글 삭제 */
    public void deleteBoard(Long boardId) throws BaseException {
        try {
            Optional<Board> findBoard = boardRepository.findById(boardId);
            if (findBoard.isPresent()) {
                Board board = findBoard.get();

                deleteImagesByBoard(board);
                boardRepository.deleteByBoardId(boardId);
            } else {
                throw new BaseException(BOARD_NOT_EXISTS);
            }
        } catch (BaseException exception) {
            if(exception.getStatus().equals(BOARD_NOT_EXISTS)){
                throw exception;
            } else {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    //이미지 저장
    public List<BoardImage> saveImages(List<MultipartFile> imageFiles, Board board) {
        List<BoardImage> images = new ArrayList<>();

        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\templates\\image\\";


        for (MultipartFile imageFile : imageFiles) {
            UUID uuid = UUID.randomUUID();
            String originalFileName = uuid + "_" + imageFile.getOriginalFilename();
            File saveFile = new File(projectPath + originalFileName);

            BoardImage image = new BoardImage();

            // 이미지 파일 저장 로직
            try {
                imageFile.transferTo(saveFile);

                image.setImgName(originalFileName);
                image.setImgOriName(imageFile.getOriginalFilename());
                image.setImgPath(saveFile.getAbsolutePath());
                image.setBoard(board);

                images.add(image); // 이미지 객체를 리스트에 추가

            } catch (IOException e) {
                throw new RuntimeException("이미지 저장에 실패하였습니다.", e);
            }
        }
        boardImageRepository.saveAll(images);


        return images;
    }

    //이미지 수정
    public List<BoardImage> saveImagesFix(List<MultipartFile> imageFiles, Board board) {
        List<BoardImage> images = boardImageRepository.findByBoard(board);

        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\templates\\image\\";


        for (MultipartFile imageFile : imageFiles) {
            UUID uuid = UUID.randomUUID();
            String originalFileName = uuid + "_" + imageFile.getOriginalFilename();
            File saveFile = new File(projectPath + originalFileName);

            BoardImage image = new BoardImage();

            // 이미지 파일 저장 로직
            try {
                imageFile.transferTo(saveFile);

                image.setImgName(originalFileName);
                image.setImgOriName(imageFile.getOriginalFilename());
                image.setImgPath(saveFile.getAbsolutePath());
                image.setBoard(board);

                images.add(image); // 이미지 객체를 리스트에 추가

            } catch (IOException e) {
                throw new RuntimeException("이미지 저장에 실패하였습니다.", e);
            }
        }
        boardImageRepository.saveAll(images);


        return images;
    }

    //이미지 삭제
    public void deleteImagesByBoard(Board board) throws BaseException {

        // board에 속한 모든 이미지를 조회합니다.
        List<BoardImage> images = boardImageRepository.findByBoard(board);

        // 조회한 이미지들을 하나씩 삭제합니다.
        for (BoardImage image : images) {
            // 이미지 삭제
            boardImageRepository.delete(image);
        }
    }


}
