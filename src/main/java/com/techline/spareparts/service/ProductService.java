package com.techline.spareparts.service;

import com.techline.spareparts.entity.Product;
import com.techline.spareparts.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public List<Product> findByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> findLowStock() {
        return productRepository.findAll().stream()
                .filter(Product::isLowStock)
                .toList();
    }

    public List<Product> search(String term) {
        if (term == null || term.isBlank()) return productRepository.findAll();
        return productRepository.searchByNameCodeOrSpecs(term.trim());
    }

    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
