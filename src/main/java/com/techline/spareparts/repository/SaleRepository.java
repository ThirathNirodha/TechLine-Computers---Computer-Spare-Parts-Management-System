package com.techline.spareparts.repository;

import com.techline.spareparts.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByCustomerIdOrderBySaleDateDesc(Long customerId);

    @Query("SELECT s FROM Sale s WHERE s.saleDate >= :start AND s.saleDate < :end ORDER BY s.saleDate DESC")
    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);
}
