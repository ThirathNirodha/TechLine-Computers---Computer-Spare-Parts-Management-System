package com.techline.spareparts.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "warranty_claims")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarrantyClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_item_id")
    private SaleItem saleItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_ticket_id")
    private ServiceTicket serviceTicket;

    @Column(nullable = false)
    private LocalDateTime claimDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WarrantyClaimStatus status;

    @Column(length = 2000)
    private String notes;

    @Column(length = 1000)
    private String documentationRef;

    public enum WarrantyClaimStatus {
        SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, RESOLVED
    }
}
