package com.aydnorcn.mis_app.repository;

import com.aydnorcn.mis_app.entity.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollRepository extends JpaRepository<Poll, String> {
}
