package com.aydnorcn.mis_app.repository;

import com.aydnorcn.mis_app.entity.Poll;
import com.aydnorcn.mis_app.entity.User;
import com.aydnorcn.mis_app.entity.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, String> {

    Page<Vote> findAll(Specification<Vote> specification, Pageable pageable);
    Optional<Vote> findByOptionPoll(Poll poll);
    int countByOptionPollAndUser(Poll poll, User user);
}
