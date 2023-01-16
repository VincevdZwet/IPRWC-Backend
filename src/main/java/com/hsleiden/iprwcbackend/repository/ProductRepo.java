package com.hsleiden.iprwcbackend.repository;

import com.hsleiden.iprwcbackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepo extends JpaRepository<Product, UUID> {
    /**
     * Find a product by its id
     * @param product the product object containing the id
     * @return the product
     */
    Optional<Product[]> findById(Product product);
    @Query(value = "SELECT DISTINCT * FROM iprwc.product WHERE title LIKE :productTitle", nativeQuery = true)
    Product findByTitle(@Param("productTitle") String title);
    Optional<Product[]> findByEnabled(boolean enabled);
}
