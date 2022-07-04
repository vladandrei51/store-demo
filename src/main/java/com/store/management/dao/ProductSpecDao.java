package com.store.management.dao;

import com.store.management.model.ProductSpec;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductSpecDao extends JpaRepository<ProductSpec, Integer> {
    Optional<ProductSpec> findByColorAndCapacity(String color, String capacity);
}
