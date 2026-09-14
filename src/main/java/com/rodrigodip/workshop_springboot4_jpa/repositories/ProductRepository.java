package com.rodrigodip.workshop_springboot4_jpa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.rodrigodip.workshop_springboot4_jpa.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
        // No need for implementations Spring Data JPA takes care of it.

        Page<Product> findByCategories_Id(Long categoryId, Pageable pageable);
}