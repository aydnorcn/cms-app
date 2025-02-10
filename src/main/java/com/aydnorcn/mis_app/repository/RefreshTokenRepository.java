package com.aydnorcn.mis_app.repository;

import com.aydnorcn.mis_app.entity.RefreshToken;
import com.aydnorcn.mis_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
    Optional<RefreshToken> findByUser(User user);
}
