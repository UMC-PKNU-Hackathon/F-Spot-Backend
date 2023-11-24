package com.example.spot.service;

import com.example.spot.config.exception.BaseException;
import com.example.spot.model.Board;
import com.example.spot.model.BoardLike;
import com.example.spot.model.DTO.BoardLikeRes;
import com.example.spot.model.User;
import com.example.spot.repository.BoardLikeRepository;
import com.example.spot.repository.BoardRepository;
import com.example.spot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.spot.config.exception.BaseResponseStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;

    public List<BoardLikeRes> getAllBoardLike() throws BaseException {
        try {
            List<BoardLike> boardLikes = boardLikeRepository.findAll();

            if (boardLikes.isEmpty()) {
                throw new BaseException(SHOW_FAIL_BOARD_LIKE);
            }

            List<BoardLikeRes> boardLikeReses = new ArrayList<>();
            for (BoardLike boardLike : boardLikes) {
                Board board = boardLike.getBoard();
                BoardLikeRes boardLikeRes = new BoardLikeRes();
                boardLikeRes.setBoard_id(boardLike.getBoard().getBoardId());
                boardLikeRes.setNickname(board.getUser().getNickname());
                boardLikeRes.setContent(board.getContent());
                boardLikeRes.setHits(board.getHits());
                boardLikeRes.setLikeCnt(board.getLikeCnt());
                boardLikeRes.setCreatedAt(board.getCreatedAt());

                boardLikeReses.add(boardLikeRes);
            }

            return boardLikeReses;
        } catch (BaseException exception) {
            if(exception.getStatus().equals(SHOW_FAIL_BOARD_LIKE)){
                throw exception;
            } else {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    public List<BoardLikeRes> getBoardLike(Long idx) throws BaseException {
        try {
            Optional<User> optUser = userRepository.findById(idx);
            if (optUser.isPresent()) {
                User user = optUser.get();

                List<BoardLike> myBoardLike = boardLikeRepository.findBoardLikesByUser(user).orElse(null);

                if (myBoardLike.isEmpty()) {
                    throw new BaseException(SHOW_FAIL_BOARD_LIKE);
                }

                List<BoardLikeRes> boardLikeReses = new ArrayList<>();
                for (BoardLike boardLike : myBoardLike) {
                    Board board = boardLike.getBoard();
                    BoardLikeRes boardLikeRes = new BoardLikeRes();
                    boardLikeRes.setBoard_id(boardLike.getBoard().getBoardId());
                    boardLikeRes.setNickname(board.getUser().getNickname());
                    boardLikeRes.setContent(board.getContent());
                    boardLikeRes.setHits(board.getHits());
                    boardLikeRes.setLikeCnt(board.getLikeCnt());
                    boardLikeRes.setCreatedAt(board.getCreatedAt());

                    boardLikeReses.add(boardLikeRes);
                }

                return boardLikeReses;


            } else {
                throw new BaseException(USERS_NOT_EXISTS);
            }
        } catch (BaseException exception) {
            if(exception.getStatus().equals(USERS_NOT_EXISTS)){
                throw exception;
            } else if (exception.getStatus().equals(SHOW_FAIL_BOARD_LIKE)) {
                throw exception;
            } else {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    @Transactional
    public BoardLikeRes addBoardLike(Long idx, Long boardId) throws BaseException {
        try {
            Optional<User> optUser = userRepository.findById(idx);
            Optional<Board> optBoard = boardRepository.findByBoardId(boardId);

            if (optUser.isPresent()) {
                User user = optUser.get();
                if (optBoard.isEmpty()) {
                    throw new BaseException(BOARD_NOT_EXISTS);
                }
                Board board = optBoard.get();
                User boardWriter = board.getUser();

                // 게시글 1개당 좋아요 1번만 가능하도록 추가
                // 이미 좋아요 누른 게시글인지 확인하기
                BoardLike checkBoardLike = boardLikeRepository.findBoardLikeByUserAndBoard_BoardId(user, boardId);

                if (checkBoardLike == null) {
                    System.out.println("boardWriter : " + boardWriter.getId() + ", login user : " + user.getId());

                    if (!boardWriter.equals(user)) { // 자신이 누른 좋아요가 아니라면
                        boardWriter.likeChange(boardWriter.getReceivedLikeCnt() + 1);
                    }
                    board.likeChange(board.getLikeCnt() + 1);


                    BoardLike boardLike = boardLikeRepository.save(BoardLike.builder()
                            .user(user)
                            .board(board)
                            .checkLike(1L)
                            .build());

                    BoardLikeRes boardLikeRes = new BoardLikeRes();
                    boardLikeRes.setBoard_id(boardLike.getBoard().getBoardId());
                    boardLikeRes.setNickname(board.getUser().getNickname());
                    boardLikeRes.setContent(board.getContent());
                    boardLikeRes.setHits(board.getHits());
                    boardLikeRes.setLikeCnt(board.getLikeCnt());
                    boardLikeRes.setCreatedAt(board.getCreatedAt());

                    return boardLikeRes;
                } else { // 이미 좋아요한 게시글이라면
                    throw new BaseException(LIKE_BOARD_ALREADY_EXISTS);
                }
            } else {
                throw new BaseException(USERS_NOT_EXISTS);
            }
        } catch (BaseException exception) {
            if(exception.getStatus().equals(BOARD_NOT_EXISTS)){
                throw exception;
            } else if (exception.getStatus().equals(LIKE_BOARD_ALREADY_EXISTS)) {
                throw exception;
            } else if (exception.getStatus().equals(USERS_NOT_EXISTS)) {
                throw exception;
            }else {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    public Long checkBoardLike(Long idx, Long boardId) throws BaseException {
        try {
            Optional<User> optUser = userRepository.findById(idx);
            Optional<Board> optBoard = boardRepository.findById(boardId);

            if (optUser.isPresent()) {
                User user = optUser.get();
                if (optBoard.isEmpty()) {
                    throw new BaseException(BOARD_NOT_EXISTS);
                }
                BoardLike checkBoardLike = boardLikeRepository.findBoardLikeByUserAndBoard_BoardId(user, boardId);
                return checkBoardLike.getCheckLike();
            } else {
                throw new BaseException(LIKE_NOT_EXISTS);
            }
        } catch (BaseException exception) {
            if(exception.getStatus().equals(BOARD_NOT_EXISTS)){
                throw exception;
            } else if (exception.getStatus().equals(LIKE_NOT_EXISTS)) {
                throw exception;
            } else {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

}
