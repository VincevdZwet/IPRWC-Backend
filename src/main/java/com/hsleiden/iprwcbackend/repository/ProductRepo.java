package com.hsleiden.iprwcbackend.repository;

import com.hsleiden.iprwcbackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepo extends JpaRepository<Product, UUID> {
    /**
     * Find a product by its id
     * @param product the product object containing the id
     * @return the product
     */
    public Optional<Product[]> findById(Product product);
}
