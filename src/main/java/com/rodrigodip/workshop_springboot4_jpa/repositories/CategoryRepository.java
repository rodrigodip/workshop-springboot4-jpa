package com.rodrigodip.workshop_springboot4_jpa.repositories;

import com.rodrigodip.workshop_springboot4_jpa.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
        // No need for implementations Spring Data JPA takes care of it.
}
