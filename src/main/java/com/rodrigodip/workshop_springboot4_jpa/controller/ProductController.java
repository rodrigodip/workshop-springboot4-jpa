package com.rodrigodip.workshop_springboot4_jpa.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rodrigodip.workshop_springboot4_jpa.entities.Product;
import com.rodrigodip.workshop_springboot4_jpa.services.ProductService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

        public final ProductService productService;

        public ProductController(ProductService productService) {
                this.productService = productService;
        }

        @GetMapping
        public ResponseEntity<List<Product>> FindAll() {
                List<Product> productsList = productService.FindAll();
                return ResponseEntity.ok().body(productsList);
        }

        @GetMapping(value = "/{id}")
        public ResponseEntity<Product> findById(@PathVariable Long id) {
                Product productFound = productService.FindByID(id);
                return ResponseEntity.ok().body(productFound);
        }

}
