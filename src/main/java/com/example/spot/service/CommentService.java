package com.example.spot.service;

import com.example.spot.config.exception.BaseException;
import com.example.spot.model.Board;
import com.example.spot.model.BoardLike;
import com.example.spot.model.Comment;
import com.example.spot.model.DTO.BoardLikeRes;
import com.example.spot.model.DTO.CommentReq;
import com.example.spot.model.DTO.CommentRes;
import com.example.spot.model.User;
import com.example.spot.repository.BoardRepository;
import com.example.spot.repository.CommentRepository;
import com.example.spot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.spot.config.exception.BaseResponseStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;


    public List<CommentRes> getComments(Long boardId) throws BaseException {
        try {
            Optional<Board> findBoard = this.boardRepository.findByBoardId(boardId);
            if (findBoard.isPresent()) {
                List<Comment> comments =commentRepository.findAllByBoard_BoardId(boardId).orElse(null);

                List<CommentRes> commentReses = new ArrayList<>();
                for (Comment comment : comments) {
                    CommentRes commentRes = new CommentRes();
                    commentRes.setBoardId(comment.getBoard().getBoardId());
                    commentRes.setBody(comment.getBody());
                    commentRes.setNickname(comment.getUser().getNickname());
                    commentRes.setCreatedAt(comment.getBoard().getCreatedAt());
                    commentRes.setId(comment.getId());

                    commentReses.add(commentRes);
                }

                return commentReses;
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

    public List<CommentRes> getMyCommentById(Long idx) throws BaseException {
        try {
            Optional<User> userOptional = userRepository.findById(idx);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                List<Comment> myComments = commentRepository.findAllByUser_Id(idx).orElse(null);

                if (myComments.isEmpty()) {
                    throw new BaseException(SHOW_FAIL_COMMENT);
                }

                List<CommentRes> commentReses = new ArrayList<>();
                for (Comment comment : myComments) {
                    CommentRes commentRes = new CommentRes();
                    commentRes.setBoardId(comment.getBoard().getBoardId());
                    commentRes.setBody(comment.getBody());
                    commentRes.setNickname(comment.getUser().getNickname());
                    commentRes.setCreatedAt(comment.getBoard().getCreatedAt());
                    commentRes.setId(comment.getId());

                    commentReses.add(commentRes);
                }

                return commentReses;

            } else {
                // 사용자를 찾지 못한 경우 에러 처리
                throw new BaseException(USERS_NOT_EXISTS);
            }
        } catch(BaseException exception){
            if(exception.getStatus().equals(USERS_NOT_EXISTS)){
                throw exception;
            } else if (exception.getStatus().equals(SHOW_FAIL_COMMENT)) {
                throw exception;
            } else {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }


    public CommentRes addComment(Long boardId, CommentReq req, Long idx) throws BaseException {
        try {
            Optional<User> optUser = userRepository.findById(idx);
            Optional<Board> optBoard = boardRepository.findByBoardId(boardId);

            if (optUser.isPresent()) {
                User user = optUser.get();

                if (optBoard.isEmpty()) {
                    throw new BaseException(BOARD_NOT_EXISTS);
                }
                Board board = optBoard.get();
                board.commentChange(board.getCommentCnt() + 1);
                Comment comment = commentRepository.save(req.toEntity(board, user));

                CommentRes commentRes = new CommentRes();
                commentRes.setId(comment.getId());
                commentRes.setBoardId(board.getBoardId());
                commentRes.setBody(comment.getBody());
                commentRes.setNickname(comment.getUser().getNickname());
                commentRes.setCreatedAt(comment.getBoard().getCreatedAt());

                return commentRes;
            } else {
                throw new BaseException(USERS_NOT_EXISTS);
            }
        } catch(BaseException exception){
            if(exception.getStatus().equals(BOARD_NOT_EXISTS)){
                throw exception;
            } else if (exception.getStatus().equals(USERS_NOT_EXISTS)) {
                throw exception;
            } else {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }


    @Transactional
    public CommentRes updateComment(Long commentId, String newBody, Long idx) throws BaseException {
        try {
            Optional<Comment> findComment = commentRepository.findById(commentId);
            if (findComment.isPresent()) {
                if (idx.equals(findComment.get().getUser().getId())) {
                    Comment comment = findComment.get();
                    comment.update(newBody);

                    CommentRes commentRes = new CommentRes();
                    commentRes.setId(comment.getId());
                    commentRes.setBoardId(comment.getBoard().getBoardId());
                    commentRes.setBody(comment.getBody());
                    commentRes.setNickname(comment.getUser().getNickname());
                    commentRes.setCreatedAt(comment.getBoard().getCreatedAt());

                    return  commentRes;
                } else {
                    throw new BaseException(INVALID_USER_JWT);
                }
            } else {
                throw new BaseException(COMMENT_NOT_EXISTS);
            }
        } catch (BaseException exception){
            if(exception.getStatus().equals(INVALID_USER_JWT)){
                throw exception;
            } else if (exception.getStatus().equals(COMMENT_NOT_EXISTS)) {
                throw exception;
            } else {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    public Long deleteComment(Long commentId, Long idx) throws BaseException {
        try {
            Optional<User> findUser = userRepository.findById(idx);
            Optional<Comment> findComment = commentRepository.findById(commentId);

            if (findUser.isPresent()) {
                User user = findUser.get();
                if (findComment.isEmpty()) {
                    throw new BaseException(COMMENT_NOT_EXISTS);
                }
                User writer = findComment.get().getUser();
                Board board = findComment.get().getBoard();

                if (!user.equals(writer)) {
                    throw new BaseException(INVALID_USER_JWT);
                }
                // 댓글 삭제 하면 board 의 commentCnt 값도 1씩 줄어야 함.
                board.commentChange(board.getCommentCnt() - 1);

                commentRepository.delete(findComment.get());
                return board.getBoardId();
            } else {
                throw new BaseException(USERS_NOT_EXISTS);
            }

        } catch (BaseException exception){
            if(exception.getStatus().equals(COMMENT_NOT_EXISTS)){
                throw exception;
            } else if (exception.getStatus().equals(USERS_NOT_EXISTS)) {
                throw exception;
            } else {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }
}
