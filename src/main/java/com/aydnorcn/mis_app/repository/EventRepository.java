package com.aydnorcn.mis_app.repository;

import com.aydnorcn.mis_app.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, String> {

    Page<Event> findAll(Specification<Event> specification, Pageable pageable);
}
