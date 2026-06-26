package com.rodrigodip.workshop_springboot4_jpa.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rodrigodip.workshop_springboot4_jpa.entities.Order;
import com.rodrigodip.workshop_springboot4_jpa.services.OrderService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping(value = "/orders")
public class OrderController {

        public final OrderService orderService;

        public OrderController(OrderService orderService) {
                this.orderService = orderService;
        }

        @GetMapping
        public ResponseEntity<List<Order>> FindAll() {
                List<Order> ordersList = orderService.FindAll();
                return ResponseEntity.ok().body(ordersList);
        }

        @GetMapping(value = "/{id}")
        public ResponseEntity<Order> findById(@PathVariable Long id) {
                Order orderFound = orderService.FindByID(id);
                return ResponseEntity.ok().body(orderFound);
        }

}
