package com.rodrigodip.workshop_springboot4_jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rodrigodip.workshop_springboot4_jpa.entities.OrderItem;
import com.rodrigodip.workshop_springboot4_jpa.entities.pk.OrderItemPK;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {
        // No need for implementations Spring Data JPA takes care of it.
}