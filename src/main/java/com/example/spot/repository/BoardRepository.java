package com.example.spot.repository;

import com.example.spot.model.Board;
import com.example.spot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    Optional<Board> findByBoardId(Long boardId);
    Optional<Board> findByUserAndBoardId(User user, Long boardId);
    List<Board> findBoardByUser(User user);

    List<Board> findBoardByUserOrderByLikeCntDesc(User user);

    // 검색
    List<Board> findByUserNicknameContaining(@Param("keyword") String keyword);
    List<Board> findByContentContaining(@Param("keyword") String keyword);
    void deleteByBoardId(Long BoardId);

    // 정렬
    List<Board> findAllByOrderByHitsDesc(); // 조회수
    List<Board> findAllByOrderByLikeCntDesc(); // 좋아요




}
