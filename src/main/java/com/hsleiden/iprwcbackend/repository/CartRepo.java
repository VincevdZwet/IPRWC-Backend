package com.hsleiden.iprwcbackend.repository;

import com.hsleiden.iprwcbackend.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepo extends JpaRepository<Cart, UUID> {
    Optional<Cart[]> findById(Cart cart);
}
