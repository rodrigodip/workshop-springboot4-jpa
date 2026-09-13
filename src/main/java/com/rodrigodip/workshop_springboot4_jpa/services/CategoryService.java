package com.rodrigodip.workshop_springboot4_jpa.services;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.rodrigodip.workshop_springboot4_jpa.entities.Category;
import com.rodrigodip.workshop_springboot4_jpa.repositories.CategoryRepository;
import com.rodrigodip.workshop_springboot4_jpa.services.exceptions.DataBaseException;
import com.rodrigodip.workshop_springboot4_jpa.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

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
                                .orElseThrow(() -> new ResourceNotFoundException(id));
        }

        public Category insert(Category category) {
                category.setId(null);
                return categoryRepository.save(category);
        }

        public void delete(Long id) {
                try {
                        if (!categoryRepository.existsById(id)) {
                                throw new ResourceNotFoundException(id);
                        }
                        categoryRepository.deleteById(id);
                } catch (DataIntegrityViolationException e) {
                        throw new DataBaseException(e.getMessage());
                }
        }

        public Category update(Long id, Category newCategoryData) {
                try {
                        Category foundCategory = categoryRepository.getReferenceById(id);
                        updateData(foundCategory, newCategoryData);
                        return categoryRepository.save(foundCategory);
                } catch (EntityNotFoundException e) {
                        throw new ResourceNotFoundException(id);
                }
        }

        private void updateData(Category foundCategory, Category newCategoryData) {
                foundCategory.setName(newCategoryData.getName());
        }
}
