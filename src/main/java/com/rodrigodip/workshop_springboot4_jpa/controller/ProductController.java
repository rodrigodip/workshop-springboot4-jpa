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

import com.rodrigodip.workshop_springboot4_jpa.entities.Product;
import com.rodrigodip.workshop_springboot4_jpa.services.ProductService;

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

        @PostMapping
        public ResponseEntity<Product> insert(@RequestBody Product newProduct) {
                newProduct = productService.insert(newProduct);
                URI uri = ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(newProduct.getId())
                                .toUri();
                return ResponseEntity.created(uri).body(newProduct);
        }

        @DeleteMapping(value = "/{id}")
        public ResponseEntity<Void> delete(@PathVariable Long id) {
                productService.delete(id);
                return ResponseEntity.noContent().build();
        }

        @PutMapping(value = "/{id}")
        public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product newProductData) {
                Product updatedProduct = productService.update(id, newProductData);
                return ResponseEntity.ok().body(updatedProduct);
        }
}
