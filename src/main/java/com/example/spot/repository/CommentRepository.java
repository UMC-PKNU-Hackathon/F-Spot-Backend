package com.example.spot.repository;

import com.example.spot.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<List<Comment>> findAllByBoard_BoardId(Long boardId);
    Optional<List<Comment>> findAllByUser_Id (Long userId);

}
