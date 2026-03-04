package com.techline.spareparts.service;

import com.techline.spareparts.entity.*;
import com.techline.spareparts.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;

    public List<Sale> findAll() {
        return saleRepository.findAll();
    }

    public Sale findById(Long id) {
        return saleRepository.findById(id).orElseThrow(() -> new RuntimeException("Sale not found: " + id));
    }

    public List<Sale> findByCustomerId(Long customerId) {
        return saleRepository.findByCustomerIdOrderBySaleDateDesc(customerId);
    }

    public List<Sale> findSalesForDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return saleRepository.findBySaleDateBetween(start, end);
    }

    @Transactional
    public Sale createSale(Customer customer, List<CartItemDto> items) {
        Sale sale = Sale.builder()
                .customer(customer)
                .saleDate(LocalDateTime.now())
                .totalAmount(BigDecimal.ZERO)
                .build();
        sale = saleRepository.save(sale);
        BigDecimal total = BigDecimal.ZERO;
        for (CartItemDto dto : items) {
            Product product = productService.findById(dto.productId());
            if (product.getQuantityInStock() < dto.quantity()) {
                throw new RuntimeException("Insufficient stock for " + product.getName());
            }
            BigDecimal lineTotal = product.getUnitPrice().multiply(BigDecimal.valueOf(dto.quantity()));
            SaleItem item = SaleItem.builder()
                    .sale(sale)
                    .product(product)
                    .quantity(dto.quantity())
                    .unitPrice(product.getUnitPrice())
                    .totalPrice(lineTotal)
                    .build();
            sale.getItems().add(item);
            total = total.add(lineTotal);
            product.setQuantityInStock(product.getQuantityInStock() - dto.quantity());
            productService.save(product);
        }
        sale.setTotalAmount(total);
        return saleRepository.save(sale);
    }

    public record CartItemDto(Long productId, int quantity) {}
}
