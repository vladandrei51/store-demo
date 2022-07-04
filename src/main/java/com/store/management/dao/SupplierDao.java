package com.store.management.dao;

import com.store.management.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierDao extends JpaRepository<Supplier, Integer> {
    Optional<Supplier> findByName(String name);
}
