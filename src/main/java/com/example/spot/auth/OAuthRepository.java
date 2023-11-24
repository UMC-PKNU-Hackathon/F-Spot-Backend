package com.example.spot.auth;

import com.example.spot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthRepository  extends JpaRepository<User, Long> {
    Optional<User> findByNicknameAndType(String nickname, String type);

}
