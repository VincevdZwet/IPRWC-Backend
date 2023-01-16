package com.hsleiden.iprwcbackend.repository;

import com.hsleiden.iprwcbackend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepo extends JpaRepository<Order, UUID> {
    Optional<Order[]> findById(Order order);
    @Query(value = "SELECT COUNT(*) FROM `order` WHERE CONVERT(`order`.created_at, date ) = CURRENT_DATE", nativeQuery = true)
    Long countOrdersToday();


}
