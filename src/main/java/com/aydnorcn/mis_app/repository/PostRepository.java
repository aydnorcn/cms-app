package com.aydnorcn.mis_app.repository;

import com.aydnorcn.mis_app.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, String> {

    Page<Post> findAll(Specification<Post> specification, Pageable pageable);
}
