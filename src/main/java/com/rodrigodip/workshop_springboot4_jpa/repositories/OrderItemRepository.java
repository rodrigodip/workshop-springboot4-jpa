package com.rodrigodip.workshop_springboot4_jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rodrigodip.workshop_springboot4_jpa.entities.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
        // No need for implementations Spring Data JPA takes care of it.
}