package com.store.management.dao;

import com.store.management.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryDao extends JpaRepository<Category, Integer> {
    Optional<Category> findByCategoryName(String name);
}
