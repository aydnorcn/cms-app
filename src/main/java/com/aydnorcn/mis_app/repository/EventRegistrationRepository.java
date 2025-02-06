package com.aydnorcn.mis_app.repository;

import com.aydnorcn.mis_app.entity.EventRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, String> {

    Page<EventRegistration> findAll(Specification<EventRegistration> specification, Pageable pageable);
}
