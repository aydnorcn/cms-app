package com.aydnorcn.mis_app.repository;

import com.aydnorcn.mis_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {

    Optional<User> findByUserCredentialEmail(String email);
    boolean existsByUserCredentialEmail(String email);
}
