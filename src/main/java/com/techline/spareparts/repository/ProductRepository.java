package com.techline.spareparts.repository;

import com.techline.spareparts.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByQuantityInStockLessThanEqual(Integer maxStock);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR LOWER(p.code) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR LOWER(p.specifications) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Product> searchByNameCodeOrSpecs(String term);
}
