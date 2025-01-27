package com.aydnorcn.mis_app.repository;

import com.aydnorcn.mis_app.entity.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, String> {

    Page<Assignment> findAll(Specification<Assignment> specification, Pageable pageable);
}
