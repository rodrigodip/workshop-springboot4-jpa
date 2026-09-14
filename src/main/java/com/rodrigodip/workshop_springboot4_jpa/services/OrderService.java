package com.rodrigodip.workshop_springboot4_jpa.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rodrigodip.workshop_springboot4_jpa.dto.OrderItemRequestDTO;
import com.rodrigodip.workshop_springboot4_jpa.dto.OrderRequestDTO;
import com.rodrigodip.workshop_springboot4_jpa.entities.Order;
import com.rodrigodip.workshop_springboot4_jpa.entities.OrderItem;
import com.rodrigodip.workshop_springboot4_jpa.entities.Product;
import com.rodrigodip.workshop_springboot4_jpa.entities.User;
import com.rodrigodip.workshop_springboot4_jpa.entities.enums.OrderStatus;
import com.rodrigodip.workshop_springboot4_jpa.repositories.OrderItemRepository;
import com.rodrigodip.workshop_springboot4_jpa.repositories.OrderRepository;
import com.rodrigodip.workshop_springboot4_jpa.repositories.ProductRepository;
import com.rodrigodip.workshop_springboot4_jpa.repositories.UserRepository;
import com.rodrigodip.workshop_springboot4_jpa.services.exceptions.DataBaseException;
import com.rodrigodip.workshop_springboot4_jpa.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class OrderService {

        private final OrderRepository orderRepository;
        private final UserRepository userRepository;
        private final ProductRepository productRepository;
        private final OrderItemRepository orderItemRepository;

        public OrderService(OrderRepository orderRepository, UserRepository userRepository,
                        ProductRepository productRepository, OrderItemRepository orderItemRepository) {
                this.orderRepository = orderRepository;
                this.userRepository = userRepository;
                this.productRepository = productRepository;
                this.orderItemRepository = orderItemRepository;
        }

        public List<Order> FindAll() {
                return orderRepository.findAll();
        }

        /** Web-only filter (AD-04c). REST contract unchanged. */
        public List<Order> findFiltered(String status, Long clientId) {
                String normalized = (status == null || status.isBlank() || status.equalsIgnoreCase("ALL"))
                                ? null
                                : status.trim().toUpperCase();
                return orderRepository.findAll().stream()
                                .filter(o -> normalized == null || o.getOrderStatus().name().equals(normalized))
                                .filter(o -> clientId == null || (o.getClient() != null
                                                && clientId.equals(o.getClient().getId())))
                                .toList();
        }

        /** Web-only filter by client name, contains case-insensitive (AD-07). */
        public List<Order> findFilteredByClientName(String status, String clientName) {
                String normalized = (status == null || status.isBlank() || status.equalsIgnoreCase("ALL"))
                                ? null
                                : status.trim().toUpperCase();
                String needle = (clientName == null || clientName.isBlank()) ? null
                                : clientName.trim().toLowerCase();
                return orderRepository.findAll().stream()
                                .filter(o -> normalized == null || o.getOrderStatus().name().equals(normalized))
                                .filter(o -> needle == null || (o.getClient() != null
                                                && o.getClient().getName() != null
                                                && o.getClient().getName().toLowerCase().contains(needle)))
                                .toList();
        }

        public Order FindByID(Long id) {
                Optional<Order> orderOptional = orderRepository.findById(id);
                return orderOptional
                                .orElseThrow(() -> new ResourceNotFoundException(id));
        }

        @Transactional
        public Order create(OrderRequestDTO dto) {
                if (dto.getClientId() == null) {
                        throw new DataBaseException("clientId must be provided");
                }
                if (dto.getItems() == null || dto.getItems().isEmpty()) {
                        throw new DataBaseException("Order must contain at least one item");
                }

                User client = userRepository.findById(dto.getClientId())
                                .orElseThrow(() -> new ResourceNotFoundException(dto.getClientId()));

                OrderStatus status = parseStatus(dto.getOrderStatus(), OrderStatus.WAITING_PAYMENT);

                Order order = new Order(null, Instant.now(), status, client);
                order = orderRepository.save(order);

                for (OrderItemRequestDTO itemDto : dto.getItems()) {
                        if (itemDto.getProductId() == null) {
                                throw new DataBaseException("productId must be provided for every item");
                        }
                        if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
                                throw new DataBaseException("quantity must be greater than zero");
                        }
                        Product product = productRepository.findById(itemDto.getProductId())
                                        .orElseThrow(() -> new ResourceNotFoundException(itemDto.getProductId()));
                        OrderItem item = new OrderItem(order, product, itemDto.getQuantity(), product.getPrice());
                        orderItemRepository.save(item);
                        order.getItens().add(item);
                }

                return order;
        }

        @Transactional
        public Order updateStatus(Long id, String orderStatus) {
                try {
                        Order order = orderRepository.getReferenceById(id);
                        order.setOrderStatus(parseStatus(orderStatus, null));
                        return orderRepository.save(order);
                } catch (EntityNotFoundException e) {
                        throw new ResourceNotFoundException(id);
                }
        }

        @Transactional
        public void delete(Long id) {
                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(id));
                try {
                        orderItemRepository.deleteAll(order.getItens());
                        orderRepository.delete(order);
                } catch (DataIntegrityViolationException e) {
                        throw new DataBaseException(e.getMessage());
                }
        }

        private OrderStatus parseStatus(String raw, OrderStatus defaultValue) {
                if (raw == null || raw.isBlank()) {
                        if (defaultValue != null) {
                                return defaultValue;
                        }
                        throw new DataBaseException("orderStatus must be provided");
                }
                String normalized = raw.trim().toUpperCase();
                for (OrderStatus status : OrderStatus.values()) {
                        if (status.name().equals(normalized)) {
                                return status;
                        }
                }
                try {
                        return OrderStatus.valueOf(Integer.parseInt(normalized));
                } catch (Exception ex) {
                        throw new DataBaseException("Invalid orderStatus: " + raw);
                }
        }
}
