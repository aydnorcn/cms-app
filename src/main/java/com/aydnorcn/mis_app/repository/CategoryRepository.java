package com.aydnorcn.mis_app.repository;

import com.aydnorcn.mis_app.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {

    boolean existsByName(String name);
}
