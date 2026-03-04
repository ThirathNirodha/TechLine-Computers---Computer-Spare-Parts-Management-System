package com.techline.spareparts.repository;

import com.techline.spareparts.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findBySupplierIdOrderByOrderDateDesc(Long supplierId);
}
