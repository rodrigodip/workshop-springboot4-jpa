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

import com.rodrigodip.workshop_springboot4_jpa.entities.Category;
import com.rodrigodip.workshop_springboot4_jpa.services.CategoryService;

@RestController
@RequestMapping(value = "/categories")
public class CategoryController {

        public final CategoryService categoryService;

        public CategoryController(CategoryService categoryService) {
                this.categoryService = categoryService;
        }

        @GetMapping
        public ResponseEntity<List<Category>> FindAll() {
                List<Category> categorysList = categoryService.FindAll();
                return ResponseEntity.ok().body(categorysList);
        }

        @GetMapping(value = "/{id}")
        public ResponseEntity<Category> findById(@PathVariable Long id) {
                Category categoryFound = categoryService.FindByID(id);
                return ResponseEntity.ok().body(categoryFound);
        }

        @PostMapping
        public ResponseEntity<Category> insert(@RequestBody Category newCategory) {
                newCategory = categoryService.insert(newCategory);
                URI uri = ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(newCategory.getId())
                                .toUri();
                return ResponseEntity.created(uri).body(newCategory);
        }

        @DeleteMapping(value = "/{id}")
        public ResponseEntity<Void> delete(@PathVariable Long id) {
                categoryService.delete(id);
                return ResponseEntity.noContent().build();
        }

        @PutMapping(value = "/{id}")
        public ResponseEntity<Category> update(@PathVariable Long id, @RequestBody Category newCategoryData) {
                Category updatedCategory = categoryService.update(id, newCategoryData);
                return ResponseEntity.ok().body(updatedCategory);
        }
}
