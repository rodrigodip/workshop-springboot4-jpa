package com.rodrigodip.workshop_springboot4_jpa.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.rodrigodip.workshop_springboot4_jpa.dto.OrderRequestDTO;
import com.rodrigodip.workshop_springboot4_jpa.dto.OrderStatusUpdateDTO;
import com.rodrigodip.workshop_springboot4_jpa.entities.Order;
import com.rodrigodip.workshop_springboot4_jpa.services.OrderService;

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

        @PostMapping
        public ResponseEntity<Order> create(@RequestBody OrderRequestDTO dto) {
                Order newOrder = orderService.create(dto);
                URI uri = ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(newOrder.getId())
                                .toUri();
                return ResponseEntity.created(uri).body(newOrder);
        }

        @PutMapping(value = "/{id}")
        public ResponseEntity<Order> updateStatus(@PathVariable Long id,
                        @RequestBody OrderStatusUpdateDTO dto) {
                Order updatedOrder = orderService.updateStatus(id, dto.getOrderStatus());
                return ResponseEntity.ok().body(updatedOrder);
        }

        @DeleteMapping(value = "/{id}")
        public ResponseEntity<Void> delete(@PathVariable Long id) {
                orderService.delete(id);
                return ResponseEntity.noContent().build();
        }
}
