package com.rodrigodip.workshop_springboot4_jpa.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rodrigodip.workshop_springboot4_jpa.entities.Order;
import com.rodrigodip.workshop_springboot4_jpa.repositories.OrderRepository;

@Service
public class OrderService {

        private final OrderRepository orderRepository;

        public OrderService(OrderRepository orderRepository) {
                this.orderRepository = orderRepository;
        }

        public List<Order> FindAll() {
                return orderRepository.findAll();
        }

        public Order FindByID(Long id) {
                Optional<Order> orderOptional = orderRepository.findById(id);
                return orderOptional
                                .orElseThrow(
                                                () -> new RuntimeException("Order Not Found."));
        }
}