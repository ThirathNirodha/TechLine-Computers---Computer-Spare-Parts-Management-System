package com.techline.spareparts.service;

import com.techline.spareparts.entity.PurchaseOrder;
import com.techline.spareparts.entity.PurchaseOrderItem;
import com.techline.spareparts.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierService supplierService;
    private final ProductService productService;

    public List<PurchaseOrder> findAll() {
        return purchaseOrderRepository.findAll();
    }

    public PurchaseOrder findById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found: " + id));
    }

    @Transactional
    public PurchaseOrder save(PurchaseOrder order) {
        if (order.getOrderDate() == null) order.setOrderDate(LocalDateTime.now());
        if (order.getStatus() == null) order.setStatus(PurchaseOrder.PurchaseOrderStatus.PENDING);
        BigDecimal total = order.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);
        return purchaseOrderRepository.save(order);
    }

    @Transactional
    public PurchaseOrder createOrder(Long supplierId, String itemsString) {
        var supplier = supplierService.findById(supplierId);
        PurchaseOrder order = PurchaseOrder.builder()
                .supplier(supplier)
                .orderDate(LocalDateTime.now())
                .status(PurchaseOrder.PurchaseOrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();
        order = purchaseOrderRepository.save(order);
        BigDecimal total = BigDecimal.ZERO;
        for (String part : itemsString.split(";")) {
            if (part.isBlank()) continue;
            String[] kv = part.split(":");
            if (kv.length >= 3) {
                Long productId = Long.parseLong(kv[0]);
                int qty = Integer.parseInt(kv[1]);
                BigDecimal unitPrice = new BigDecimal(kv[2]);
                var product = productService.findById(productId);
                PurchaseOrderItem item = PurchaseOrderItem.builder()
                        .purchaseOrder(order)
                        .product(product)
                        .quantity(qty)
                        .unitPrice(unitPrice)
                        .build();
                order.getItems().add(item);
                total = total.add(unitPrice.multiply(BigDecimal.valueOf(qty)));
            }
        }
        order.setTotalAmount(total);
        return purchaseOrderRepository.save(order);
    }

    @Transactional
    public void setStatus(Long id, PurchaseOrder.PurchaseOrderStatus status) {
        PurchaseOrder order = findById(id);
        order.setStatus(status);
        if (status == PurchaseOrder.PurchaseOrderStatus.DELIVERED) {
            for (PurchaseOrderItem item : order.getItems()) {
                var product = productService.findById(item.getProduct().getId());
                product.setQuantityInStock(product.getQuantityInStock() + item.getQuantity());
                productService.save(product);
            }
        }
        purchaseOrderRepository.save(order);
    }
}
