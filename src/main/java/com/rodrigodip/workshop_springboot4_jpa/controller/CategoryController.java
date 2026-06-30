package com.rodrigodip.workshop_springboot4_jpa.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rodrigodip.workshop_springboot4_jpa.entities.Category;
import com.rodrigodip.workshop_springboot4_jpa.services.CategoryService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

}
