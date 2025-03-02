package com.aydnorcn.mis_app.repository;

import com.aydnorcn.mis_app.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByName(String name);
    boolean existsByName(String name);
}
