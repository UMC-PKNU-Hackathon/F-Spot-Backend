package com.example.spot.repository;

import com.example.spot.model.BoardLike;
import com.example.spot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    Optional<List<BoardLike>> findBoardLikesByUser(User user);
    BoardLike findBoardLikeByUserAndBoard_BoardId(User user, Long boardId);
}
