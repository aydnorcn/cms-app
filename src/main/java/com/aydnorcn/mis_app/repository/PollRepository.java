package com.aydnorcn.mis_app.repository;

import com.aydnorcn.mis_app.entity.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollRepository extends JpaRepository<Poll, String> {

    Page<Poll> findAll(Specification<Poll> specification, Pageable pageable);
}
