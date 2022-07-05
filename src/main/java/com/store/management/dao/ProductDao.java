package com.store.management.dao;

import com.store.management.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductDao extends JpaRepository<Product, Integer> {
    Optional<Product> findByName(String name);
}
