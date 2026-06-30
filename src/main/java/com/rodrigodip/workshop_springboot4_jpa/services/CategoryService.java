package com.rodrigodip.workshop_springboot4_jpa.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rodrigodip.workshop_springboot4_jpa.entities.Category;
import com.rodrigodip.workshop_springboot4_jpa.repositories.CategoryRepository;

@Service
public class CategoryService {

        private final CategoryRepository categoryRepository;

        public CategoryService(CategoryRepository categoryRepository) {
                this.categoryRepository = categoryRepository;
        }

        public List<Category> FindAll() {
                return categoryRepository.findAll();
        }

        public Category FindByID(Long id) {
                Optional<Category> categoryOptional = categoryRepository.findById(id);
                return categoryOptional
                                .orElseThrow(
                                                () -> new RuntimeException("Category Not Found."));
        }
}