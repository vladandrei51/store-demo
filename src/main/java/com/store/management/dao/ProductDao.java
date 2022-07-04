package com.store.management.dao;

import com.store.management.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDao extends JpaRepository<Product, Integer> {
    Product findProductById(int id);
}
