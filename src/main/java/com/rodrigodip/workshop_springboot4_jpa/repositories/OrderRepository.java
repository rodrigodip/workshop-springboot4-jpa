package com.rodrigodip.workshop_springboot4_jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rodrigodip.workshop_springboot4_jpa.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
        // No need for implementations Spring Data JPA takes care of it.
}