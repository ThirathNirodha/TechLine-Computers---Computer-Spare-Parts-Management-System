package com.techline.spareparts.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String code;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    @DecimalMin("0")
    private BigDecimal unitPrice;

    @Column(nullable = false)
    @Min(0)
    private Integer quantityInStock;

    @Column(nullable = false)
    @Min(0)
    private Integer minStockLevel;

    /** Warranty in months from sale date. */
    private Integer warrantyMonths;

    /** Specifications e.g. "Socket AM4, DDR4, 65W TDP" */
    @Column(length = 1000)
    private String specifications;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SaleItem> saleItems = new ArrayList<>();

    public boolean isLowStock() {
        return quantityInStock <= minStockLevel;
    }
}
