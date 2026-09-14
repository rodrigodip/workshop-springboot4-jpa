package com.rodrigodip.workshop_springboot4_jpa.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.rodrigodip.workshop_springboot4_jpa.entities.Category;
import com.rodrigodip.workshop_springboot4_jpa.entities.Product;
import com.rodrigodip.workshop_springboot4_jpa.repositories.CategoryRepository;
import com.rodrigodip.workshop_springboot4_jpa.repositories.ProductRepository;
import com.rodrigodip.workshop_springboot4_jpa.services.exceptions.DataBaseException;
import com.rodrigodip.workshop_springboot4_jpa.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

        public static final int CATALOG_PAGE_SIZE = 6;

        private final ProductRepository productRepository;
        private final CategoryRepository categoryRepository;

        public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
                this.productRepository = productRepository;
                this.categoryRepository = categoryRepository;
        }

        public List<Product> FindAll() {
                return productRepository.findAll();
        }

        public Product FindByID(Long id) {
                Optional<Product> productOptional = productRepository.findById(id);
                return productOptional
                                .orElseThrow(() -> new ResourceNotFoundException(id));
        }

        public Page<Product> findCatalogPage(Long categoryId, int page) {
                int safePage = Math.max(page, 0);
                PageRequest pageable = PageRequest.of(safePage, CATALOG_PAGE_SIZE, Sort.by("id"));
                Page<Product> result = (categoryId == null)
                                ? productRepository.findAll(pageable)
                                : productRepository.findByCategories_Id(categoryId, pageable);
                if (safePage >= result.getTotalPages() && result.getTotalPages() > 0) {
                        pageable = PageRequest.of(result.getTotalPages() - 1, CATALOG_PAGE_SIZE,
                                        Sort.by("id"));
                        result = (categoryId == null)
                                        ? productRepository.findAll(pageable)
                                        : productRepository.findByCategories_Id(categoryId, pageable);
                }
                return result;
        }

        public Product insert(Product product) {
                product.setId(null);
                Set<Category> incoming = new HashSet<>(product.getCategories());
                product.getCategories().clear();
                product.getCategories().addAll(resolveCategories(incoming));
                return productRepository.save(product);
        }

        public void delete(Long id) {
                try {
                        if (!productRepository.existsById(id)) {
                                throw new ResourceNotFoundException(id);
                        }
                        productRepository.deleteById(id);
                } catch (DataIntegrityViolationException e) {
                        throw new DataBaseException(e.getMessage());
                }
        }

        public Product update(Long id, Product newProductData) {
                try {
                        Product foundProduct = productRepository.getReferenceById(id);
                        updateData(foundProduct, newProductData);
                        return productRepository.save(foundProduct);
                } catch (EntityNotFoundException e) {
                        throw new ResourceNotFoundException(id);
                }
        }

        private void updateData(Product foundProduct, Product newProductData) {
                foundProduct.setName(newProductData.getName());
                foundProduct.setDescription(newProductData.getDescription());
                foundProduct.setPrice(newProductData.getPrice());
                foundProduct.setImgUrl(newProductData.getImgUrl());
                foundProduct.getCategories().clear();
                foundProduct.getCategories().addAll(resolveCategories(newProductData.getCategories()));
        }

        private Set<Category> resolveCategories(Set<Category> categories) {
                Set<Category> resolved = new HashSet<>();
                if (categories == null) {
                        return resolved;
                }
                for (Category cat : categories) {
                        if (cat.getId() == null) {
                                throw new DataBaseException("Category id must be provided");
                        }
                        Category managed = categoryRepository.findById(cat.getId())
                                        .orElseThrow(() -> new ResourceNotFoundException(cat.getId()));
                        resolved.add(managed);
                }
                return resolved;
        }
}
