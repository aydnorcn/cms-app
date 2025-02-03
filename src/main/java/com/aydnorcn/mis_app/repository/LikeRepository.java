package com.aydnorcn.mis_app.repository;

import com.aydnorcn.mis_app.entity.Like;
import com.aydnorcn.mis_app.entity.Post;
import com.aydnorcn.mis_app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, String> {

    Optional<Like> findByUserAndPost(User user, Post post);
    boolean existsByUserAndPost(User user, Post post);
    Page<Like> findAllByPost(Post post, Pageable pageable);
    Page<Like> findAllByUser(User user, Pageable pageable);
}
