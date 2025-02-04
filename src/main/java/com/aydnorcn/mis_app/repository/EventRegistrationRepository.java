package com.aydnorcn.mis_app.repository;

import com.aydnorcn.mis_app.entity.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, String> {
}
