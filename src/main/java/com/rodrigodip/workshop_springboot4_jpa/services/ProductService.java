package com.rodrigodip.workshop_springboot4_jpa.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rodrigodip.workshop_springboot4_jpa.entities.Product;
import com.rodrigodip.workshop_springboot4_jpa.repositories.ProductRepository;

@Service
public class ProductService {

        private final ProductRepository productRepository;

        public ProductService(ProductRepository productRepository) {
                this.productRepository = productRepository;
        }

        public List<Product> FindAll() {
                return productRepository.findAll();
        }

        public Product FindByID(Long id) {
                Optional<Product> productOptional = productRepository.findById(id);
                return productOptional
                                .orElseThrow(
                                                () -> new RuntimeException("Product Not Found."));
        }
}